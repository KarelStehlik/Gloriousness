package general;

import static org.lwjgl.opengl.GL11C.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11C.glGetIntegerv;

import java.nio.IntBuffer;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

public final class Constants {

  public static final int SpriteSizeFloats = 25;
  public static final Vector2i screenSize;

  //gameplay
  public static final int StartingHealth = 100;
  public static final int MobSpread = 10;

  static {
    IntBuffer b = BufferUtils.createIntBuffer(4);
    glGetIntegerv(GL_VIEWPORT, b);
    screenSize = new Vector2i(b.get(2), b.get(3));
  }

  private Constants() {
  }
}
