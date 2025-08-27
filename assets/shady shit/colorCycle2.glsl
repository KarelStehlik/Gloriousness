
#type fragment
#version 450 core

uniform sampler2D sampler;
uniform int time;

in GS_OUT {
    vec4 gColor;
    vec2 gTexCoords;
} frag_in;

out vec4 color;

void main(){
    float avg = (frag_in.gColor[0] + frag_in.gColor[1] + frag_in.gColor[2])*.333;
    vec4 tex = texture(sampler, frag_in.gTexCoords);
    float opacity = tex[3]*frag_in.gColor[3];
    if(avg>1){
        opacity*=avg;
        avg=1;
    }
    float t = float(time) / (1<<17);
    vec3 col = 0.5 + 0.5*cos(t + frag_in.gColor.xyx+vec3(0,2,4));
    color = vec4(avg*col+(1-avg)*tex.xyz,opacity);
}
