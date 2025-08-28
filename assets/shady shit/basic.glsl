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

struct SpriteProperties{
    float w, h;
    float cosr, sinr;
    float tx1, tx2, ty1, ty2;
};

void emitPoint(vec2 relPosition, SpriteProperties props, vec4 color)
{
    gl_Position.xy = vec2(props.cosr*props.w*(2*relPosition.x-1) - props.sinr*props.h*(2*relPosition.y-1),
                          props.cosr*props.h*(2*relPosition.y-1) + props.sinr*props.w*(2*relPosition.x-1))
                            *sizeScale + gl_in[0].gl_Position.xy;

    geo_out.gTexCoords=vec2(props.tx1 + (props.tx2-props.tx1)*relPosition.x,
                            props.ty1+(props.ty2-props.ty1)*relPosition.y);
    geo_out.gColor=color;
    EmitVertex();
}

void main()
{
    SpriteProperties props;

    props.cosr = cos(gs_in[0].fRotation);
    props.sinr = sin(gs_in[0].fRotation);
    props.w = gs_in[0].fSize[0];
    props.h = gs_in[0].fSize[1];

    props.tx1 = gs_in[0].fTexCoords[2];
    props.ty1 = gs_in[0].fTexCoords[3];
    props.tx2 = gs_in[0].fTexCoords[0];
    props.ty2 = gs_in[0].fTexCoords[1];

    emitPoint(vec2(1, 0), props, gs_in[0].fColor[0]);
    emitPoint(vec2(0, 0), props, gs_in[0].fColor[1]);
    emitPoint(vec2(1, 1), props, gs_in[0].fColor[2]);
    emitPoint(vec2(0, 1), props, gs_in[0].fColor[3]);
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
