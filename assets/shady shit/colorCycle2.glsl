#type geometry
#version 450 core

const int subdivisionPerSide = 10;

layout (points) in;
layout (triangle_strip, max_vertices = subdivisionPerSide*8+1) out;

uniform vec2 sizeScale;

in VS_OUT {
    vec4 fColor[4];
    vec4 fTexCoords;
    vec2 fSize;
    float fRotation;
} gs_in[];

out GS_OUT {
    vec4 gColor;
    vec2 gTexCoords;
    float strength, speed;
} geo_out;

struct SpriteProperties{
    float w, h;
    float cosr, sinr;
    float tx1, tx2, ty1, ty2;
};

void emitPoint(vec2 relPosition, SpriteProperties props)
{
    gl_Position.xy = vec2(props.cosr*props.w*(2*relPosition.x-1) - props.sinr*props.h*(2*relPosition.y-1),
    props.cosr*props.h*(2*relPosition.y-1) + props.sinr*props.w*(2*relPosition.x-1))
    *sizeScale + gl_in[0].gl_Position.xy;

    geo_out.gTexCoords=vec2(props.tx1 + (props.tx2-props.tx1)*relPosition.x,
    props.ty1+(props.ty2-props.ty1)*relPosition.y);

    float dx = (relPosition.x-0.5 - gs_in[0].fColor[1][0])*props.w;

    float dy = (relPosition.y-0.5 - gs_in[0].fColor[1][1])*props.h;

    float dist = sqrt(dx*dx+dy*dy) * gs_in[0].fColor[0][0];

    geo_out.gColor=vec4(-dist, -dist, -dist, gs_in[0].fColor[0][3]);

    geo_out.strength = gs_in[0].fColor[0][1];
    geo_out.speed = gs_in[0].fColor[0][2];
    EmitVertex();
}

void main()
{
    SpriteProperties props;

    props.cosr = cos(gs_in[0].fRotation);
    props.sinr = sin(gs_in[0].fRotation);
    props.w = gs_in[0].fSize[0];
    props.h = gs_in[0].fSize[1];

    props.tx1 = gs_in[0].fTexCoords[2];
    props.ty1 = gs_in[0].fTexCoords[3];
    props.tx2 = gs_in[0].fTexCoords[0];
    props.ty2 = gs_in[0].fTexCoords[1];

    float xCentre = 0.5 + gs_in[0].fColor[1][0];
    float yCentre = 0.5 + gs_in[0].fColor[1][1];

    for(float i=0; i<subdivisionPerSide;i++){
        emitPoint(vec2(i/subdivisionPerSide, 0), props);
        emitPoint(vec2(xCentre, yCentre), props);
    }
    for(float i=0; i<subdivisionPerSide;i++){
        emitPoint(vec2(1, i/subdivisionPerSide), props);
        emitPoint(vec2(xCentre, yCentre), props);
    }
    for(float i=0; i<subdivisionPerSide;i++){
        emitPoint(vec2(1-i/subdivisionPerSide, 1), props);
        emitPoint(vec2(xCentre, yCentre), props);
    }
    for(float i=0; i<subdivisionPerSide;i++){
        emitPoint(vec2(0, 1-i/subdivisionPerSide), props);
        emitPoint(vec2(xCentre, yCentre), props);
    }
    emitPoint(vec2(0,0), props);

    EndPrimitive();
}



    #type fragment
#version 450 core

uniform sampler2D sampler;
uniform int time;

in GS_OUT {
    vec4 gColor;
    vec2 gTexCoords;
    float strength, speed;
} frag_in;

out vec4 color;

void main(){
    vec4 tex = texture(sampler, frag_in.gTexCoords);
    float opacity = tex[3] * frag_in.gColor[3];

    float t = float(time) / (1<<17) * frag_in.speed;
    vec3 col = 0.5 + 0.5 * cos(t + frag_in.gColor.xyx+vec3(0, 2, 4));

    color = vec4(frag_in.strength*col+(1-frag_in.strength)*tex.xyz, opacity);
}
