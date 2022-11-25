package windowStuff;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBufferSubData;

import general.Data;
import general.Util;
import java.util.Objects;

public class Sprite implements AbstractSprite {

  private final float[] vertices = new float[36];
  private final float[] positions = new float[12];
  protected boolean hasUnsavedChanges = true;
  protected String textureName;
  protected BatchSystem bsToJoin = null;
  protected int layer;
  protected Batch batch = null;
  protected Shader shader;
  protected int slotInBatch;
  protected boolean deleted = false;
  protected boolean mustBeRebatched = false;
  protected boolean rebuffer = false;
  private float x;
  private float y;
  private float[] colors = new float[16];
  private float[] texCoords = new float[8];
  private float rotation = 0;
  private float width, height;
  private String imageName;
  private Animation animation;

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
    this.layer = layer;
    setImage(imageName);
    this.animation = () -> {
    };
  }

  @Override
  public int getLayer() {
    return layer;
  }

  @Override
  public Batch getBatch() {
    return batch;
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

  @Override
  public void addToBs(BatchSystem bs) {
    bs.addSprite(this);
  }

  @Override
  public float getWidth() {
    return width;
  }

  @Override
  public float getHeight() {
    return height;
  }

  protected void onAnimationEnd() {
    animation = () -> {
    };
  }

  @Override
  public void playAnimation(Animation anim) {
    animation = anim;
  }

  @Override
  public void setShader(String shader) {
    this.shader = Data.getShader(shader);
    this.mustBeRebatched = true;
  }

  @Override
  public float getRotation() {
    return rotation;
  }

  @Override
  public void setRotation(float r) {
    rotation = r;
    hasUnsavedChanges = true;
  }

  @Override
  public void setImage(String name) {
    if (!Objects.equals(this.textureName, Data.getImageTexture(name))) {
      this.textureName = Data.getImageTexture(name);
      mustBeRebatched = (batch != null) || mustBeRebatched;
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

  @Override
  public synchronized void unBatch() {
    if (batch != null) {
      batch.removeSprite(this);
      batch = null;
    } else {
      bsToJoin = null;
    }
  }

  @Override
  public void setPosition(float X, float Y) {
    setX(X);
    setY(Y);
    hasUnsavedChanges = true;
  }

  @Override
  public void scale(float multiplier) {
    width *= multiplier;
    height *= multiplier;
    hasUnsavedChanges = true;
  }

  @Override
  public void setSize(float w, float h) {
    width = w / 2;
    height = h / 2;
    hasUnsavedChanges = true;
  }

  public synchronized void updateVertices() {
    animation.update();
    if (!hasUnsavedChanges) {
      return;
    }
    float rotationSin = Util.sin(rotation);
    float rotationCos = Util.cos(rotation);
    float XC = width * rotationCos, YC = height * rotationCos,
        XS = width * rotationSin, YS = height * rotationSin;

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
    rebuffer = true;
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

  @Override
  public void setColors(float[] colors) {
    assert colors.length == 16 : "expected 16 colors for sprite.";
    this.colors = colors;
  }

  protected synchronized void _delete() {
    unBatch();
  }

  @Override
  public void delete() {
    deleted = true;
  }

  @Override
  public float getX() {
    return x;
  }

  @Override
  public void setX(float x) {
    this.x = x;
  }

  @Override
  public float getY() {
    return y;
  }

  @Override
  public void setY(float y) {
    this.y = y;
  }

  @FunctionalInterface
  interface Animation {

    void update();
  }

  public class BasicAnimation implements Animation {

    private final int length;
    private final float frameLengthNano;
    private final double startTime;
    private final String end;
    private final String name;

    public BasicAnimation(String name, float duration) {
      this(name, duration, imageName);
    }

    public BasicAnimation(String name, float duration, String endImage) {
      length = Data.getAnimationLength(name);
      frameLengthNano = duration / length * 1000000000;
      startTime = System.nanoTime();
      end = endImage;
      this.name = name;
    }

    @Override
    public void update() {
      int frame = (int) ((System.nanoTime() - startTime) / frameLengthNano);
      hasUnsavedChanges = true;
      if (frame > length) {
        imageName = end;
        onAnimationEnd();
      } else {
        imageName = name + '-' + frame;
      }
      setUV();
    }
  }
}
