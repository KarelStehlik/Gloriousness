

#type vertex
#version 330 core
layout (location=0) in vec4 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;
uniform float rotat;

out vec4 fColor;
out vec2 texCoords;
void main(){
    mat4 newMat=mat4(1);
    newMat[0][0]=cos(rotat);
    newMat[0][1]=sin(rotat);
    newMat[1][0]=-sin(rotat);
    newMat[1][1]=cos(rotat);

    fColor = color;
    texCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1)*newMat;
}


#type geometry
#version 450 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in vec4 fColor[];
in vec2 textCoords[];

out vec4 Color;
out vec2 TextCoords;


void main()
{
    int i;
    Color=fColor;
    Color[0]=70;
    for(i = 0; i < gl_in.length(); i++)
    {
        gl_Position = gl_in[i].gl_Position;
        TextCoords = textCoords[i];

        EmitVertex();
    }
    EndPrimitive();
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
