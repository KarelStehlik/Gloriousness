package windowStuff;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;

import general.Data;
import general.Util;
import java.util.Objects;
import org.joml.Vector2f;

public class Sprite {

  private final float[] vertices = new float[36];
  private final float[] positions = new float[12];
  protected boolean hasUnsavedChanges = true;
  protected String textureName;
  protected int layer;
  protected Batch batch = null;
  protected Shader shader;
  protected int slotInBatch;
  protected boolean deleteThis = false;
  protected boolean mustBeRebatched = false;
  protected boolean rebuffer = false;
  private float x;
  private float y;
  private float[] colors = new float[16];
  private float[] texCoords = new float[8];
  private float rotation = 0;
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
    this.setX(x);
    this.setY(y);
    width = sizeX / 2;
    height = sizeY / 2;
    this.shader = Data.getShader(shader);
    Vector2f BL = new Vector2f(x - width, y - height);
    Vector2f TR = new Vector2f(x + width, y + height);
    //vertices = new float[]{
    //    // x     y     z  r  g  b  a  u  v
    //    TR.x, BL.y, 1, 0, 0, 0, 1, 1, 0,// +-
    //    BL.x, TR.y, 1, 0, 0, 0, 1, 0, 1,// -+
    //    TR.x, TR.y, 1, 0, 0, 0, 1, 1, 1,// ++
    //    BL.x, BL.y, 1, 0, 0, 0, 1, 0, 0// --
    //};
    setImage(imageName);
    this.layer = layer;
  }

  public void setShader(String shader) {
    this.shader = Data.getShader(shader);
    this.mustBeRebatched = true;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float r) {
    rotation = r;
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
    texCoords = Data.getImageCoordinates(this.imageName);
  }

  protected synchronized void getBatched(Batch newBatch, int slot) {
    batch = newBatch;
    slotInBatch = slot;
  }

  protected synchronized void unBatch() {
    batch.removeSprite(this);
  }

  public void setPosition(float X, float Y) {
    setX(X);
    setY(Y);
    hasUnsavedChanges = true;
  }

  public void scale(float multiplier) {
    width *= multiplier;
    height *= multiplier;
    hasUnsavedChanges = true;
  }

  public void setSize(float w, float h) {
    width = w / 2;
    height = h / 2;
    hasUnsavedChanges = true;
  }

  public synchronized void updateVertices() {
    if (!hasUnsavedChanges) {
      return;
    }
    float rotationSin = Util.sin(rotation);
    float rotationCos = Util.cos(rotation);
    float XC = width * rotationCos, YC = height * rotationCos,
        XS = width * rotationSin, YS = height * rotationSin;
    ////+-
    //vertices[0] = x + XC - YS;
    //vertices[1] = y + XS + YC;
    ////-+
    //vertices[Constants.VertexSizeFloats] = x - XC + YS;
    //vertices[1 + Constants.VertexSizeFloats] = y - XS - YC;
    ////++
    //vertices[2 * Constants.VertexSizeFloats] = x + XC + YS;
    //vertices[1 + 2 * Constants.VertexSizeFloats] = y + XS - YC;
    ////--
    //vertices[3 * Constants.VertexSizeFloats] = x - XC - YS;
    //vertices[1 + 3 * Constants.VertexSizeFloats] = y - XS + YC;

    //+-
    positions[0] = getX() + XC - YS;
    positions[1] = getY() + XS + YC;
    //-+
    positions[3] = getX() - XC + YS;
    positions[4] = getY() - XS - YC;
    //++
    positions[6] = getX() + XC + YS;
    positions[7] = getY() + XS - YC;
    //--
    positions[9] = getX() - XC - YS;
    positions[10] = getY() - XS + YC;

    hasUnsavedChanges = false;
  }

  protected synchronized void bufferVertices(long offset) {
    for (int i = 0; i < 4; i++) {
      vertices[9 * i] = positions[3 * i];
      vertices[9 * i + 1] = positions[3 * i + 1];
      vertices[9 * i + 2] = positions[3 * i + 2];

      vertices[9 * i + 3] = colors[4 * i];
      vertices[9 * i + 4] = colors[4 * i + 1];
      vertices[9 * i + 5] = colors[4 * i + 2];
      vertices[9 * i + 6] = colors[4 * i + 3];

      vertices[9 * i + 7] = texCoords[2 * i];
      vertices[9 * i + 8] = texCoords[2 * i + 1];
    }

    glBufferSubData(GL_ARRAY_BUFFER, offset, vertices);
    rebuffer = false;
  }

  public void setColors(float[] colors) {
    assert colors.length == 16 : "expected 16 colors for sprite.";
    this.colors = colors;
  }

  protected synchronized void _delete() {
    unBatch();
    //vertices = null;
  }

  public void delete() {
    deleteThis = true;
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }
}
