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

void emitPoint(vec2 position, vec4 color, vec2 texCoords)
{
    gl_Position = gl_in[0].gl_Position +  vec4(position*sizeScale, 0, 0);
    geo_out.gTexCoords=texCoords;
    geo_out.gColor=color;
    EmitVertex();
}

void main()
{
    float cosr= cos(gs_in[0].fRotation);
    float sinr= sin(gs_in[0].fRotation);
    float w = gs_in[0].fSize[0];
    float h = gs_in[0].fSize[1];

    float cosw = cosr * w;
    float sinw = sinr * w;
    float cosh = cosr * h;
    float sinh = sinr * h;

    float x1 = gs_in[0].fTexCoords[2];
    float y1 = gs_in[0].fTexCoords[3];
    float x2 = gs_in[0].fTexCoords[0];
    float y2 = gs_in[0].fTexCoords[1];

    emitPoint(vec2(cosw+sinh,-cosh+sinw), gs_in[0].fColor[0], vec2(x2,y1));
    emitPoint(vec2(-cosw+sinh,-cosh-sinw), gs_in[0].fColor[1], vec2(x1,y1));
    emitPoint(vec2(cosw-sinh,cosh+sinw), gs_in[0].fColor[2], vec2(x2,y2));
    emitPoint(vec2(-cosw-sinh,cosh-sinw), gs_in[0].fColor[3], vec2(x1,y2));
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
