#type vertex
#version 450 core
layout (location=0) in vec2 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;

out VS_OUT {
    vec4 fColor;
    vec2 texCoords;
} vs_out;

void main(){
    vs_out.fColor = color;
    vs_out.texCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1);
}

#type fragment
#version 450 core

uniform sampler2D sampler;

in VS_OUT {
    vec4 fColor;
    vec2 texCoords;
} gs_in;

out vec4 color;

void main(){
    float avg = (gs_in.fColor[0] + gs_in.fColor[1] + gs_in.fColor[2])*.333;
    float opacity = texture(sampler, gs_in.texCoords)[3];
    if(avg>1){
        opacity*=avg;
        avg=1;
    }
    color = gs_in.fColor * avg * opacity + texture(sampler, gs_in.texCoords) * (1-avg);
    color[3]*=gs_in.fColor[3];
}
