package windowStuff;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;

import general.Constants;
import general.Data;
import general.Util;
import java.util.List;
import java.util.Objects;
import org.joml.Vector2f;

public class Sprite {

  public float x, y;
  protected float[] vertices;
  protected boolean hasUnsavedChanges = true;
  protected String textureName;
  protected int layer;
  protected Batch batch = null;
  protected Shader shader;
  protected int slotInBatch;
  protected boolean deleteThis = false;
  protected boolean mustBeRebatched = false;
  private float rotation = 0;
  private float rotationSin = 0, rotationCos = 1;
  private float width, height;
  private String imageName;

  public Sprite(String imageName, float sizeX, float sizeY, int layer,
      String shader) {
    this(imageName, 0, 0, sizeX, sizeY, layer, shader);
  }

  public Sprite(String imageName, float sizeX, float sizeY, int layer) {
    this(imageName, 0, 0, sizeX, sizeY, layer, "basic");
  }

  public Sprite(String imageName, float x, float y, float sizeX, float sizeY, int layer,
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
    setImage(imageName);
    this.layer = layer;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float r) {
    rotation = r;
    rotationSin = Util.sin(r);
    rotationCos = Util.cos(r);
    hasUnsavedChanges = true;
  }

  public void setImage(String name) {
    if (!Objects.equals(this.textureName, Data.getImageTexture(name))) {
      this.textureName = Data.getImageTexture(name);
      if (batch != null) {
        mustBeRebatched = true;
      }
    }
    imageName = name;
    setUV();
  }

  private void setUV() {
    List<Float> uv = Data.getImageCoordinates(this.imageName);
    for (int i = 0; i < 8; i++) {
      vertices[i + (i / 2) * 7 + 7] = uv.get(i);
    }
  }

  protected synchronized void getBatched(Batch newBatch, int slot) {
    batch = newBatch;
    slotInBatch = slot;
  }

  protected synchronized void unBatch() {
    batch.removeSprite(this);
  }

  public void setPosition(float X, float Y) {
    x = X;
    y = Y;
    hasUnsavedChanges = true;
  }

  public void scale(float multiplier) {
    width *= multiplier;
    height *= multiplier;
    hasUnsavedChanges = true;
  }

  public synchronized void updateVertices() {
    if (!hasUnsavedChanges) {
      return;
    }
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
    hasUnsavedChanges = false;
  }

  protected synchronized void bufferVertices(long offset) {
    glBufferSubData(GL_ARRAY_BUFFER, offset, vertices);
  }

  public void setColors(float[] colors) {
    assert colors.length == 16 : "expected 16 colors for sprite.";
    for (int i = 0; i < 16; i++) {
      vertices[3 + i + (int) ((float) i / 4.0) * 5] = colors[i];
    }
  }

  protected synchronized void _delete() {
    unBatch();
    vertices = null;
  }

  public void delete() {
    deleteThis = true;
  }
}
