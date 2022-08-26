package windowStuff;

import org.joml.Vector2f;

public class Sprite {

  protected float[] vertices;
  protected boolean hasChanged;
  protected String textureName;
  private final float rotation;
  protected int layer;
  protected Batch batch;

  public Sprite(String textureName, float x, float y, float size, int layer) {
    batch = null;
    Vector2f BL = new Vector2f(x - size, y - size);
    Vector2f TR = new Vector2f(x + size, y + size);
    vertices = new float[]{
        // x y z r g b a u v
        TR.x, BL.y, 1, 1, 1, 1, 1, 1, 0,// +-
        BL.x, TR.y, 1, 1, 1, 1, 1, 0, 1,// -+
        TR.x, TR.y, 1, 1, 1, 1, 1, 1, 1,// ++
        BL.x, BL.y, 1, 1, 1, 1, 1, 0, 0// --
    };
    this.textureName = textureName;
    rotation = 0;
    this.layer = layer;
    hasChanged = true;
  }

  public void setPosition(Vector2f pos) {

  }
}
