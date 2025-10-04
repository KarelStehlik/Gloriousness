package windowStuff.GraphicsOnly;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;

import GlobalUse.Constants;
import GlobalUse.Data;
import GlobalUse.Log;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.joml.Vector3f;
import windowStuff.GlBufferWrapper;
import windowStuff.GraphicsOnly.Sprite.SpriteBatching;

public final class Graphics {

  public static GlBufferWrapper vbo;
  private static ImageSet loadedImages;
  private final Collection<SpriteBatching> SpriteBatchings = new LinkedList<>();

  public static ImageSet getLoadedImages() {
    return loadedImages;
  }

  public static ImageData getImage(String name) {
    return loadedImages.getImage(name.toLowerCase(Locale.getDefault()));
  }

  public static List<ImageData> getAnimation(String name) {
    return loadedImages.getAnimation(name);
  }

  public static void setLoadedImages(ImageSet Images) {
    loadedImages = Images;
  }

  public void init() {
    Data.init();
    vbo = new GlBufferWrapper(GL_ARRAY_BUFFER);
    Log.write(Constants.screenSize); // this is needed to load Constants
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  public void addSpriteBatching(SpriteBatching bs) {
    SpriteBatchings.add(bs);
    bs.useCamera(new Camera(new Vector3f(0, 0, 20)));
  }

  public void redraw(double dt) {
    Data.updateShaders();

    for (SpriteBatching bs : SpriteBatchings) {
      bs.draw();
    }
  }
}
