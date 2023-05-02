package windowStuff;

import static org.lwjgl.opengl.GL15C.*;

public class GlBufferWrapper {
    private final int id;
    private int size;
    private final int type;
    private long offset=0;

    public GlBufferWrapper(int size, int type){
        this.id=glGenBuffers();
        this.size=size;
        this.type=type;
    }
    public GlBufferWrapper(int type){
        this(1024, type);
    }

    public void alloc(int newSize, int usage){
        while(size<newSize){
            size*=2;
        }
        bind();
        glBufferData(type,size,usage);
    }

    public void bind(){
        glBindBuffer(type, id);
    }

    public void data(float[] data){
        alloc(data.length*Float.BYTES, GL_STREAM_DRAW);
        glBufferSubData(type,0,data);
    }
    public void data(int[] data){
        alloc(data.length*Integer.BYTES, GL_STREAM_DRAW);
        glBufferSubData(type,0,data);
    }
    public void data(short[] data){
        alloc(data.length*Short.BYTES, GL_STREAM_DRAW);
        glBufferSubData(type,0,data);
    }

    public void subDataAdvance(float[] data){
        glBufferSubData(type,offset,data);
        offset+= (long) data.length *Float.BYTES;
    }
    public void subDataAdvance(int[] data){
        glBufferSubData(type,offset,data);
        offset+= (long) data.length *Integer.BYTES;
    }
    public void subDataAdvance(short[] data){
        glBufferSubData(type,offset,data);
        offset+= (long) data.length *Short.BYTES;
    }
    public void resetSubDataOffset(){
        offset=0;
    }
}
