#type vertex
#version 450 core
layout (location=0) in vec2 pos;
layout (location=1) in vec4 color[4];
layout (location=5) in vec4 inTexCoords;
layout (location=6) in vec2 size;
layout (location=7) in float rotation;


uniform mat4 projection;
uniform mat4 view;
uniform float rotat;

out VS_OUT {
    vec4 fColor[4];
    vec4 fTexCoords;
    vec2 fSize;
    float fRotation;
} vs_out;

void main(){
    vs_out.fRotation = rotation+rotat;
    vs_out.fColor = color;

    vs_out.fTexCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1);
    vs_out.fSize = size;
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