package windowStuff;

import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL15C.nglBufferSubData;
import static org.lwjgl.system.MemoryUtil.memAddress;

import general.Constants;
import general.Log;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;

public class GlBufferWrapper {

  private static final int CPU_BUFFER_SIZE = Constants.SpriteSizeFloats * Float.BYTES * 1024;

  private final int id;
  private final int type;
  private final ByteBuffer buffer;
  private int size;
  private long offset = 0;
  private boolean rebind = true;
  private int recentSmallAllocs=0;

  public GlBufferWrapper(int size, int type) {
    this.id = glGenBuffers();
    this.size = size;
    this.type = type;
    buffer = BufferUtils.createByteBuffer(CPU_BUFFER_SIZE);
  }

  public GlBufferWrapper(int type) {
    this(CPU_BUFFER_SIZE, type);
  }

  public void alloc(int newSize, int usage) {
    bind();
    if (size < newSize) {
      size = (int) (newSize * 1.3f);
      Log.write(id+"ALLOC^: " + newSize);
      rebind = true;
    }
    if (size > newSize * 4 + CPU_BUFFER_SIZE) {
      recentSmallAllocs+=1;
      if(recentSmallAllocs>16) {
        size = (int) (newSize * 2.5f);
        Log.write(id + "ALLOCv: " + newSize);
        rebind = true;
      }
    }else{
      recentSmallAllocs=0;
    }
    if (rebind) {
      rebind = false;
      size = Math.ceilDiv(size, CPU_BUFFER_SIZE) * CPU_BUFFER_SIZE;
      Log.write(id+"ALLOC: " + size);
      glBufferData(type, size, usage);
    }
  }

  private void passBuffer() {
    if (offset > size - CPU_BUFFER_SIZE) {
      Log.write("FUCK");
      return;
    }
    bind();
    buffer.rewind();
    nglBufferSubData(type, offset, Math.min(buffer.remaining(), size - offset), memAddress(buffer));
    offset += CPU_BUFFER_SIZE;
  }

  public void bind() {
    glBindBuffer(type, id);
  }

  public void subDataAdvance(float[] data) {
    buffer.asFloatBuffer().put(data);
    buffer.position(buffer.position() + data.length * Float.BYTES);
    if (buffer.position() >= CPU_BUFFER_SIZE) {
      passBuffer();
    }
  }

  public void subDataAdvance(int[] data) {
    buffer.asIntBuffer().put(data);
    buffer.position(buffer.position() + data.length * Integer.BYTES);
    if (buffer.position() >= CPU_BUFFER_SIZE) {
      passBuffer();
    }
  }

  public void subDataAdvance(short[] data) {
    buffer.asShortBuffer().put(data);
    buffer.position(buffer.position() + data.length * Short.BYTES);
    if (buffer.position() >= CPU_BUFFER_SIZE) {
      passBuffer();
    }
  }

  public void subDataAdvance(byte[] data) {
    buffer.put(data);
    buffer.position(buffer.position() + data.length);
    if (buffer.position() >= CPU_BUFFER_SIZE) {
      passBuffer();
    }
  }

  public void doneBuffering() {
    if (buffer.position() > 0) {
      passBuffer();
    }
    offset = 0;
  }
}
