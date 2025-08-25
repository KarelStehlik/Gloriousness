
#type vertex
#version 450 core
layout (location=0) in vec2 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;

out VS_OUT {
    vec4 fColor;
    vec2 fTexCoords;
} vs_out;

void main(){

    vs_out.fColor = color;
    vs_out.fTexCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, 1, 1);
}

#type geometry
#version 450 core
layout (triangles) in;
layout (triangle_strip, max_vertices = 6) out;

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
    vec4 otherPOS=gl_in[2].gl_Position+gl_in[1].gl_Position-gl_in[0].gl_Position;
    vec2 otherTEX=gs_in[2].fTexCoords+gs_in[1].fTexCoords-gs_in[0].fTexCoords;
    vec4 otherCOL=gs_in[2].fColor+gs_in[1].fColor-gs_in[0].fColor;

    for(i = 0; i < gl_in.length(); i++)
    {
        gl_Position = gl_in[i].gl_Position;
        geo_out.gTexCoords = gs_in[i].fTexCoords;
        geo_out.gColor=gs_in[i].fColor;
        EmitVertex();
    }
    gl_Position = otherPOS;
    geo_out.gTexCoords =otherTEX;
    geo_out.gColor=otherCOL;
    EmitVertex();
    gl_Position = gl_in[2].gl_Position;
    geo_out.gTexCoords = gs_in[2].fTexCoords;
    geo_out.gColor=gs_in[2].fColor;
    EmitVertex();
    gl_Position = gl_in[1].gl_Position;
    geo_out.gTexCoords = gs_in[1].fTexCoords;
    geo_out.gColor=gs_in[1].fColor;
    EmitVertex();

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
