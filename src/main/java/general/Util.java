package general;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public final class Util {

  // number of table entries per degree
  private static final int sinScale = 100;
  // float array that will store the sine values
  private static final float[] sin = new float[(90 * sinScale) + 1];

  // static initializer block
  // fill the sine look-up table
  static {
    // since this table is in degrees but the Math.sin() wants
    // radians must have a convert coefficient in the loop
    // also, this coefficient scales the angle down
    // as per the "entries per degree"
    // A table could be build to accept radians similarly
    double toRadian = Math.PI / (180.0 * sinScale);
    for (int i = 0; i < sin.length; i++) {
      sin[i] = (float) Math.sin(i * toRadian);
    }
  }

  public static FloatBuffer buffer(float[] input) {
    FloatBuffer out = BufferUtils.createFloatBuffer(input.length);
    return out.put(input);
  }

  public static IntBuffer buffer(int[] input) {
    IntBuffer out = BufferUtils.createIntBuffer(input.length);
    return out.put(input);
  }

  public static float sin(float a) {
    // Limit range if needed.
    if (a > 360) {
      a %= 360;
    }
    // compute the index
    int angleIndex = (int) (a * sinScale);
    if (angleIndex < (180 * sinScale) + 1) {
      if (angleIndex < (90 * sinScale) + 1) {
        return sin[angleIndex];
      }
      return sin[(180 * sinScale) - angleIndex];
    }
    if (angleIndex < (270 * sinScale) + 1) {
      return -sin[angleIndex - (180 * sinScale)];
    }
    return -sin[(360 * sinScale) - angleIndex];
  }

  public static float cos(float a) {
    return sin(a + 90);
  }
}

