package general;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public final class Util {

  public static FloatBuffer buffer(float[] input) {
    FloatBuffer out = BufferUtils.createFloatBuffer(input.length);
    return out.put(input);
  }

  public static IntBuffer buffer(int[] input) {
    IntBuffer out = BufferUtils.createIntBuffer(input.length);
    return out.put(input);
  }
}
