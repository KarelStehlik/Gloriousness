

#type vertex
#version 450 core
layout (location=0) in vec4 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;

out VS_OUT {
    vec4 fColor;
    vec2 fTexCoords;
} vs_out;

void main(){
    mat4 newMat=mat4(1);

    vs_out.fColor = color;
    vs_out.fTexCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1)*newMat;
}


#type geometry
#version 450 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in VS_OUT {
    vec4 fColor;
    vec2 fTexCoords;
} gs_in[];

out VS_OUT {
    vec4 gColor;
    vec2 gTexCoords;
} geo_out;



void main()
{
    int i;
    vec4 center=(gl_in[2].gl_Position+gl_in[1].gl_Position)/2;

    for(i = 0; i < gl_in.length(); i++)
    {
        gl_Position = gl_in[i].gl_Position*2-center;
        geo_out.gTexCoords = gs_in[i].fTexCoords;
        geo_out.gColor=gs_in[i].fColor;
        EmitVertex();
    }
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
