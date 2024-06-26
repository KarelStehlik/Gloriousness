package windowStuff;

import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL15C.nglBufferSubData;
import static org.lwjgl.system.MemoryUtil.memAddress;

import general.Constants;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;

public class GlBufferWrapper {

  private static final int CPU_BUFFER_SIZE = Constants.SpriteSizeFloats * Float.BYTES * 1000;

  private final int id;
  private final int type;
  private final ByteBuffer buffer;
  private int size;
  private long offset = 0;

  public GlBufferWrapper(int size, int type) {
    this.id = glGenBuffers();
    this.size = size;
    this.type = type;
    buffer = BufferUtils.createByteBuffer(CPU_BUFFER_SIZE);
  }

  public GlBufferWrapper(int type) {
    this(1, type);
  }

  public void alloc(int newSize, int usage) {
    bind();
    while (size < newSize) {
      size *= 2;
    }
    glBufferData(type, size, usage);
  }

  private void passBuffer() {
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
    passBuffer();
    offset = 0;
  }
}
