package windowStuff;

import static org.lwjgl.opengles.GLES20.*;

public class GlBufferWrapper {
    private final int id;
    private int size;
    private final int type;

    private int offset=0;
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
        glBufferSubData(type,0,data);
    }
    public void data(int[] data){
        glBufferSubData(type,0,data);
    }
    public void data(short[] data){
        glBufferSubData(type,0,data);
    }

    public void subDataAdvance(float[] data){
        glBufferSubData(type,offset,data);
        offset+=data.length*Float.BYTES;
    }
    public void subDataAdvance(int[] data){
        glBufferSubData(type,offset,data);
        offset+=data.length*Integer.BYTES;
    }
    public void subDataAdvance(short[] data){
        glBufferSubData(type,offset,data);
        offset+=data.length*Short.BYTES;
    }
    public void resetSubDataOffset(){
        offset=0;
    }
}
