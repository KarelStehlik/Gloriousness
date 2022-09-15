#type vertex
#version 330 core
layout (location=0) in vec3 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;
uniform int time;

out vec4 fColor;
out vec2 texCoords;

void main(){
    float t = time/16384.0;
    fColor = vec4(
    sin(t + color[0]*180 + color[1]*90)*.5+.5,
    sin(t + color[1]*180 + color[2]*90)*.5+.5,
    sin(t + color[2]*180 + color[0]*90)*.5+.5,
    color[3]);
    texCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, pos.z, 1);
}


    #type fragment
    #version 330 core

uniform sampler2D sampler;

in vec4 fColor;
in vec2 texCoords;
out vec4 color;

void main(){
    float avg = (fColor[0] + fColor[1] + fColor[2])*.5;
    vec4 tex = texture(sampler, texCoords);
    color = (fColor * avg * tex[3] + tex * (1-avg)) * fColor[3];
}