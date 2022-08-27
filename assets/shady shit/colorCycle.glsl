#type vertex
#version 330 core
layout (location=0) in vec3 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;

out vec4 fColor;
out vec2 texCoords;

void main(){
    fColor = color;
    texCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, pos.z, 1);
}


    #type fragment
    #version 330 core

uniform sampler2D sampler;
uniform int time;

in vec4 fColor;
in vec2 texCoords;
out vec4 color;

void main(){
    float t = time/30000.0;
    vec4 color1 = vec4(fColor[1],fColor[2], fColor[0],1);
    vec4 color2 = vec4(fColor[2],fColor[0], fColor[1],1);
    vec4 rColor = color1 * (sin(t)*.5+.5) + fColor * (sin(t+2.0943951023931953)*.5+.5) + color2 * (sin(t+4.1887902047863905)*.5+.5);
    rColor[3]=fColor[3];
    float avg = (fColor[0] + fColor[1] + fColor[2])*.333;
    color = rColor * avg + texture(sampler, texCoords) * (1-avg);
}