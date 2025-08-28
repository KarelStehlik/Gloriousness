package general;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Util {

  // number of table entries per degree
  private static final int sinScale = 32;
  // float array that will store the sine values
  private static final float[] sin = new float[(90 * sinScale) + 1];
  // number of table entries
  private static final int arcSinScale = 2048;
  // float array that will store the sine values
  private static final float[] arcSin = new float[arcSinScale + 1];
  private static final float[] cycleColors = new float[]{
      0, 0, 3, 1,
      0, 1.5f, 1.5f, 1,
      3, 0, 0, 1,
      3, 0, 0, 1,
  };
  private static final float[] noColors = new float[]{
      0, 0, 0, 1,
      0, 0, 0, 1,
      0, 0, 0, 1,
      0, 0, 0, 1,
  };
  private static long id = 0;

  // static initializer block
  // fill the sine look-up table
  static {
    double toRadian = Math.PI / (180.0 * sinScale);
    for (int i = 0; i < sin.length; i++) {
      sin[i] = (float) Math.sin(i * toRadian);
    }
  }

  // static initializer block
  // fill the sine look-up table
  static {
    double toDeg = 180 / Math.PI;
    double step = 1.0 / arcSinScale;
    for (int i = 0; i < arcSin.length; i++) {
      arcSin[i] = (float) (Math.asin(i * step) * toDeg);
    }
  }

  private Util() {
  }

  public static float clamp(float value, float min, float max) {
    return value < min ? min : Math.min(value, max);
  }

  public static long getUid() {
    id++;
    return id;
  }

  public static <T> List<T> shuffle(List<T> input, Random rng){
    List<T> newList = new ArrayList<T>(input);
    for(int i=0;i<newList.size()-1; i++){
      T item = newList.get(i);
      int swapWith = rng.nextInt(i, newList.size());
      newList.set(i, newList.get(swapWith));
      newList.set(swapWith, item);
    }
    return newList;
  }

  public static float sin(float a) {
    // Limit range if needed.
    if (a > 360) {
      a %= 360;
    } else if (a < 0) {
      a += 360 * ((int) (Math.abs(a) / 360) + 1);
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

  public static float arcSin(float a) {
    if (a >= 0) {
      int index = (int) (a * arcSinScale);
      return arcSin[index];
    }
    int index = (int) (-a * arcSinScale);
    return -arcSin[index];
  }

  public static float get_rotation(float x, float y) {
    float inv_hypot = 1 / (float) Math.sqrt(x * x + y * y);
    float asin = arcSin(clamp(y * inv_hypot, -1, 1));
    if (x >= 0) {
      return asin;
    }
    if (asin > 0) {
      return 180 - asin;
    }
    return -180 - asin;
  }

  public static float[] getRandomColors() {
    return new float[]{
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
        Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), Data.unstableRng.nextFloat(), 1,
    };
  }

  public static float[] getColors(float r, float g, float b) {
    return new float[]{
        r, g, b, 1,
        r, g, b, 1,
        r, g, b, 1,
        r, g, b, 1,
    };
  }

  public static float[] getCycleColors() {
    return cycleColors;
  }

  public static float[] getCycleColors(float strength) {
    float[] result = cycleColors.clone();
    for (int i = 0; i < 12; i++) {
      result[i + i / 3] *= strength;
    }
    return result;
  }

  public static class Cycle2Colors{

    private float density=0.2f,strength=1,xOffset=0,yOffset=0,speed=1;

    public float getDensity() {
      return density;
    }

    public Cycle2Colors setDensity(float density) {
      this.density = density;
      return this;
    }

    public float getStrength() {
      return strength;
    }

    public Cycle2Colors setStrength(float strength) {
      this.strength = strength;
      return this;
    }

    public float getxOffset() {
      return xOffset;
    }

    public Cycle2Colors setxOffset(float xOffset) {
      this.xOffset = xOffset;
      return this;
    }

    public float getyOffset() {
      return yOffset;
    }

    public Cycle2Colors setyOffset(float yOffset) {
      this.yOffset = yOffset;
      return this;
    }

    public float getSpeed() {
      return speed;
    }

    public Cycle2Colors setSpeed(float speed) {
      this.speed = speed;
      return this;
    }

    public float[] get(){
      return new float[]{
          density,strength, speed, 1,
          xOffset,yOffset,0,1,
          0,0,0,1,
          0,0,0,1,
      };
    }
  }

  public static float square(float x) {
    return x * x;
  }

  public static float distanceSquared(float dx, float dy) {
    return dx * dx + dy * dy;
  }

  public static float[] getBaseColors(float opacity) {
    if (opacity == 1) {
      return noColors;
    }
    return new float[]{
        0, 0, 0, opacity,
        0, 0, 0, opacity,
        0, 0, 0, opacity,
        0, 0, 0, opacity
    };
  }
}

