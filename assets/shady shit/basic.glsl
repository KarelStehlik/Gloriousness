
#type vertex
#version 450 core
layout (location=0) in vec3 pos;
layout (location=1) in vec4 color[4];
layout (location=2) in vec2 inTexCoords[2];
layout (location=3) in vec4 size;
layout (location=4) in float rotation;


uniform mat4 projection;
uniform mat4 view;

out VS_OUT {
    vec4 fColor[4];
    vec2 fTexCoords[2];
    vec4 fSize;
    float fRotation;
} vs_out;

void main(){
    vs_out.fRotation=rotation;
    vs_out.fColor = color;
    vs_out.fSize = size;
    vs_out.fTexCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1);
}

#type geometry
#version 450 core
layout (points) in;
layout (triangle_strip, max_vertices = 6) out;

in VS_OUT {
    vec4 fColor[4];
    vec2 fTexCoords[2];
    vec4 fSize;
    float fRotation;
} gs_in[];

out VS_OUT {
    vec4 gColor;
    vec2 gTexCoords;
} geo_out;

void EmitPoint(vec4 position,int corner)
{
    gl_Position = position;
    switch(corner){
    case(1):
        geo_out.gTexCoords=vec2(gs_in[0].fTexCoords[0][0],gs_in[0].fTexCoords[0][1]);
        geo_out.gColor=gs_in[0].fColor[0];
        break;
    case(2):
        geo_out.gTexCoords=vec2(gs_in[0].fTexCoords[1][0],gs_in[0].fTexCoords[0][1]);
        geo_out.gColor=gs_in[0].fColor[1];
        break;
    case(3):
        geo_out.gTexCoords=vec2(gs_in[0].fTexCoords[0][0],gs_in[0].fTexCoords[1][1]);
        geo_out.gColor=gs_in[0].fColor[2];
        break;
    case(4):
        geo_out.gTexCoords=vec2(gs_in[0].fTexCoords[1][0],gs_in[0].fTexCoords[1][1]);
        geo_out.gColor=gs_in[0].fColor[3];
        break;
    }
    EmitVertex();
}

void BuildSquare(vec4 position,vec4 size)
{
    EmitPoint(position+size,4);
    EmitPoint(position+vec4(size[0],-size[1],size[2],size[3]),2);
    EmitPoint(position+vec4(-size[0],size[1],size[2],size[3]),3);
    EmitPoint(position-size,1);
    EmitPoint(position+vec4(size[0],-size[1],size[2],size[3]),2);
    EmitPoint(position+vec4(-size[0],size[1],size[2],size[3]),3);
}

void main()
{
    float cosr=cos(gs_in[0].fRotation);
    float sinr=sin(gs_in[0].fRotation);
    float x=gs_in[0].fSize[0];
    float y=gs_in[0].fSize[1];
    float z=gs_in[0].fSize[2];
    float w=gs_in[0].fSize[3];

    BuildSquare(vec4(cosr*x-sinr*y,cosr*y-sinr*x,z,w));

    EndPrimitive();
}

#type fragment
#version 450 core

uniform sampler2D sampler;
in VS_OUT {
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
