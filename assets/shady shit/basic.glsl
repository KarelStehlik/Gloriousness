#type vertex
#version 330 core
layout (location=0) in vec2 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;

out vec4 fColor;
out vec2 texCoords;

void main(){
    fColor = color;
    texCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1);
}

#type fragment
#version 330 core

uniform sampler2D sampler;

in vec4 fColor;
in vec2 texCoords;
out vec4 color;

void main(){
    float avg = (fColor[0] + fColor[1] + fColor[2])*.333;
    float opacity = texture(sampler, texCoords)[3];
    if(avg>1){
        opacity*=avg;
        avg=1;
    }
    color = fColor * avg * opacity + texture(sampler, texCoords) * (1-avg);
    color[3]*=fColor[3];
}
