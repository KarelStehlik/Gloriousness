#type vertex
#version 450 core
layout (location=0) in vec2 pos;
layout (location=1) in vec4 color[4];
layout (location=5) in vec4 inTexCoords;
layout (location=6) in vec2 size;
layout (location=7) in float rotation;


uniform mat4 projection;
uniform mat4 view;

out VS_OUT {
    vec4 fColor[4];
    vec4 fTexCoords;
    vec2 fSize;
    float fRotation;
} vs_out;

void main(){
    vs_out.fRotation = rotation;
    vs_out.fColor = color;

    vs_out.fTexCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1);
    vs_out.fSize = size;
}

#type geometry
#version 450 core
layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

uniform vec2 sizeScale;

in VS_OUT {
    vec4 fColor[4];
    vec4 fTexCoords;
    vec2 fSize;
    float fRotation;
} gs_in[];

out GS_OUT {
    vec4 gColor;
    vec2 gTexCoords;
} geo_out;

void emitPoint(vec2 relPosition, vec4 color, float cosr, float sinr, float w, float h, float tx1, float tx2, float ty1, float ty2)
{
    gl_Position.xy = vec2(cosr*w*(2*relPosition.x-1) - sinr*h*(2*relPosition.y-1),
                          cosr*h*(2*relPosition.y-1) + sinr*w*(2*relPosition.x-1))
                            *sizeScale + gl_in[0].gl_Position.xy;
    gl_Position.zw=vec2(1,1);
    geo_out.gTexCoords=vec2(tx1 + (tx2-tx1)*relPosition.x,
                            ty1+(ty2-ty1)*relPosition.y);
    geo_out.gColor=color;
    EmitVertex();
}

void main()
{
    float cosr = cos(gs_in[0].fRotation);
    float sinr = sin(gs_in[0].fRotation);
    float w = gs_in[0].fSize[0];
    float h = gs_in[0].fSize[1];

    float tx1 = gs_in[0].fTexCoords[2];
    float ty1 = gs_in[0].fTexCoords[3];
    float tx2 = gs_in[0].fTexCoords[0];
    float ty2 = gs_in[0].fTexCoords[1];

    emitPoint(vec2(1, 0), gs_in[0].fColor[0], cosr, sinr, w, h, tx1, tx2, ty1, ty2);
    emitPoint(vec2(0, 0), gs_in[0].fColor[1], cosr, sinr, w, h, tx1, tx2, ty1, ty2);
    emitPoint(vec2(1, 1), gs_in[0].fColor[2], cosr, sinr, w, h, tx1, tx2, ty1, ty2);
    emitPoint(vec2(0, 1), gs_in[0].fColor[3], cosr, sinr, w, h, tx1, tx2, ty1, ty2);
    EndPrimitive();
}

#type fragment
#version 450 core

uniform sampler2D sampler;

in GS_OUT {
    vec4 gColor;
    vec2 gTexCoords;
} frag_in;

out vec4 color;

void main(){
    float avg = (frag_in.gColor[0] + frag_in.gColor[1] + frag_in.gColor[2])*.333;
    float opacity = texture(sampler, frag_in.gTexCoords)[3];
    if(avg>1){
        opacity*=avg;
        avg=1;
    }
    color = frag_in.gColor * avg * opacity + texture(sampler, frag_in.gTexCoords) * (1-avg);
    color[3]*=frag_in.gColor[3];
}
