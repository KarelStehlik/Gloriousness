package windowStuff;

import general.Constants;
import general.Data;
import general.Util;
import org.joml.Vector2f;

public class Sprite {

  protected float[] vertices;
  protected boolean hasChanged;
  protected String textureName;
  private float rotation = 0;
  protected int layer;
  protected Batch batch = null;
  protected Shader shader;
  protected int slotInBatch;
  public float x, y;
  private float rotationSin = 0, rotationCos = 1;
  private float width, height;

  public Sprite(String textureName, float x, float y, float sizeX, float sizeY, int layer,
      String shader) {
    this.x = x;
    this.y = y;
    width = sizeX / 2;
    height = sizeY / 2;
    this.shader = Data.getShader(shader);
    Vector2f BL = new Vector2f(x - width, y - height);
    Vector2f TR = new Vector2f(x + width, y + height);
    vertices = new float[]{
        // x     y     z  r  g  b  a  u  v
        TR.x, BL.y, 1, 0, 0, 0, 1, 1, 0,// +-
        BL.x, TR.y, 1, 0, 0, 0, 1, 0, 1,// -+
        TR.x, TR.y, 1, 0, 0, 0, 1, 1, 1,// ++
        BL.x, BL.y, 1, 0, 0, 0, 1, 0, 0// --
    };
    this.textureName = textureName;
    this.layer = layer;
    hasChanged = true;
  }

  protected void getBatched(Batch newBatch, int slot) {
    batch = newBatch;
    slotInBatch = slot;
  }

  protected void unBatch() {
    batch.removeSprite(this);
    batch.group.removeSprite(this);
  }

  public void setPosition(float X, float Y) {
    x = X;
    y = Y;
    hasChanged = true;
  }

  public void setRotation(float r) {
    rotation = r;
    rotationSin = Util.sin(r);
    rotationCos = Util.cos(r);
    hasChanged = true;
  }

  public void scale(float multiplier) {
    width *= multiplier;
    height *= multiplier;
    hasChanged = true;
  }

  public void updateVertices() {
    float XC = width * rotationCos, YC = height * rotationCos,
        XS = width * rotationSin, YS = height * rotationSin;
    //+-
    vertices[0] = x + XC - YS;
    vertices[1] = y + XS + YC;
    //-+
    vertices[Constants.VertexSizeFloats] = x - XC + YS;
    vertices[1 + Constants.VertexSizeFloats] = y - XS - YC;
    //++
    vertices[2 * Constants.VertexSizeFloats] = x + XC + YS;
    vertices[1 + 2 * Constants.VertexSizeFloats] = y + XS - YC;
    //--
    vertices[3 * Constants.VertexSizeFloats] = x - XC - YS;
    vertices[1 + 3 * Constants.VertexSizeFloats] = y - XS + YC;
  }

  public void setColors(float[] colors) {
    assert colors.length == 16 : "expected 16 colors for sprite.";
    for (int i = 0; i < 16; i++) {
      vertices[3 + i + (int) ((float) i / 4.0) * 5] = colors[i];
    }
  }

  public void delete() {
    unBatch();
  }
}
