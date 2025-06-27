package windowStuff;

import general.Constants;
import general.Data;
import general.Log;
import general.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Sprite implements AbstractSprite {

  private final float[] positions = new float[8];
  protected boolean hasUnsavedChanges = true;
  protected String textureName;
  protected int layer;
  protected Shader shader;
  protected boolean deleted = false;
  protected boolean mustBeRebatched = false;
  protected boolean rebufferStatic = true;
  protected float opacity = 1;
  private boolean hidden = false;
  private float x;
  private float y;
  private float[] colors = Util.getBaseColors(1);
  private float[] texCoords = new float[8];
  private float rotation = 0;
  private float width, height;
  private ImageData image;
  private Animation animation;

  public Sprite(Sprite og) {
    textureName = og.textureName;
    layer = og.layer;
    shader = og.shader;
    opacity = og.opacity;
    hidden = og.hidden;
    x = og.x;
    y = og.y;
    colors = og.colors;
    texCoords = og.texCoords;
    rotation = og.rotation;
    width = og.width;
    height = og.height;
    image = og.image;
    animation = () -> {
    };
  }

  public Sprite(ImageData image, int layer, String shader) {
    this.shader = Data.getShader(shader);
    this.layer = layer;
    setImage(image);
    this.animation = () -> {
    };
  }

  public Sprite(ImageData image, int layer) {
    this(image, layer, "basic");
  }

  public Sprite(String image, int layer) {
    this(Graphics.getImage(image), layer, "basic");
  }

  public Sprite(String image, int layer, String shader) {
    this(Graphics.getImage(image), layer, shader);
  }


  public Shader getShader() {
    return shader;
  }

  @Override
  public Sprite setShader(String shader) {
    this.shader = Data.getShader(shader);
    this.mustBeRebatched = true;
    return this;
  }

  @Override
  public float getRotation() {
    return rotation;
  }

  @Override
  public Sprite setRotation(float r) {
    rotation = r;
    hasUnsavedChanges = true;
    return this;
  }

  @Override
  public Sprite setImage(ImageData image) {
    this.image = image;
    String newTexture = image.textureName;

    if (!Objects.equals(this.textureName, newTexture)) {
      this.textureName = newTexture;
      mustBeRebatched = true;
    }
    setUV();
    return this;
  }

  @Override
  public Sprite setImage(String name) {
    return setImage(Graphics.getImage(name));
  }

  @Override
  public Sprite setPosition(float X, float Y) {
    setX(X);
    setY(Y);
    hasUnsavedChanges = true;
    return this;
  }

  @Override
  public Sprite scale(float multiplier) {
    width *= multiplier;
    height *= multiplier;
    hasUnsavedChanges = true;
    return this;
  }

  @Override
  public Sprite setSize(float w, float h) {
    width = w / 2;
    height = h / 2;
    hasUnsavedChanges = true;
    return this;
  }

  public float[] getColors() {
    return colors;
  }

  @Override
  public Sprite setColors(float[] colors) {
    assert colors.length == 16 : "expected 16 colors for sprite.";
    this.colors = colors;
    rebufferStatic = true;
    return this;
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
  public Sprite setX(float x) {
    this.x = x;
    return this;
  }

  @Override
  public float getY() {
    return y;
  }

  @Override
  public Sprite setY(float y) {
    this.y = y;
    return this;
  }

  @Override
  public int getLayer() {
    return layer;
  }

  @Override
  public AbstractSprite setLayer(int layer) {
    mustBeRebatched = true;
    this.layer = layer;
    return this;
  }

  @Override
  public boolean isDeleted() {
    return deleted;
  }

  @Override
  public Sprite addToBs(SpriteBatching bs) {
    bs.addSprite(this);
    return this;
  }

  @Override
  public boolean isHidden() {
    return hidden;
  }

  @Override
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public float getOpacity() {
    return opacity;
  }

  public Sprite setOpacity(float opacity) {
    this.opacity = opacity;
    return this;
  }

  @Override
  public float getWidth() {
    return width;
  }

  @Override
  public float getHeight() {
    return height;
  }

  @Override
  public Sprite playAnimation(Animation anim) {
    animation = anim;
    return this;
  }

  public boolean isDeleteOnAnimationEnd() {
    return deleteOnAnimationEnd;
  }

  public Sprite setDeleteOnAnimationEnd(boolean deleteOnAnimationEnd) {
    this.deleteOnAnimationEnd = deleteOnAnimationEnd;
    return this;
  }

  private boolean deleteOnAnimationEnd = false;

  protected void onAnimationEnd() {
    if (deleteOnAnimationEnd) {
      delete();
    }
  }

  private void setUV() {
    texCoords = image.textureCoordinates;
  }

  @Override
  public void setNaturalHeight(){
    setSize(2*width, 2*width / (texCoords[4]-texCoords[2]) * (texCoords[3]-texCoords[1]));
  }

  public synchronized void updateVertices() {
    animation.update();
    if (!hasUnsavedChanges || hidden) {
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
    positions[2] = getX() - XC + YS;
    positions[3] = getY() - XS - YC;
    //++
    positions[4] = getX() + XC + YS;
    positions[5] = getY() + XS - YC;
    //--
    positions[6] = getX() - XC - YS;
    positions[7] = getY() - XS + YC;

    hasUnsavedChanges = false;
  }

  // returns 1 if not buffered
  protected synchronized int buffer(GlBufferWrapper buffer) {
    if (hidden) {
      return 1;
    }
    float[] vertices = new float[Constants.SpriteSizeFloats];
    for (int i = 0; i < 4; i++) {
      int off = 8 * i;
      vertices[off] = positions[2 * i];
      vertices[off + 1] = positions[2 * i + 1];

      vertices[off + 2] = colors[4 * i];
      vertices[off + 3] = colors[4 * i + 1];
      vertices[off + 4] = colors[4 * i + 2];
      vertices[off + 5] = colors[4 * i + 3] * opacity;

      vertices[off + 6] = texCoords[2 * i];
      vertices[off + 7] = texCoords[2 * i + 1];
    }
    buffer.subDataAdvance(vertices);
    return 0;
  }

  @Override
  public String toString() {
    return "Sprite{"
        + "positions=" + Arrays.toString(positions)
        + ", textureName='" + textureName + '\''
        + ", layer=" + layer
        + ", shader=" + shader
        + ", texture=" + image.textureName
        + '}';
  }

  @FunctionalInterface
  interface Animation {

    void update();
  }

  public class BasicAnimation implements Animation {

    private final int length;
    private final float frameLengthNano;
    private final double startTime;
    private final List<ImageData> images;
    private boolean loop = false;

    public BasicAnimation(String name, float duration) {
      this(Graphics.getAnimation(name), duration);
    }

    public BasicAnimation(ImageData img, float duration) {
      this(List.of(img), duration);
    }

    public BasicAnimation(List<ImageData> images, float duration) {
      length = images.size();
      frameLengthNano = duration / length * 1000000000;
      startTime = System.nanoTime();
      this.images = images;
    }

    public BasicAnimation loop() {
      loop = true;
      return this;
    }

    @Override
    public void update() {
      int frame = (int) ((System.nanoTime() - startTime) / frameLengthNano);
      if (loop) {
        frame %= length;
      }
      //Log.write(length);
      hasUnsavedChanges = true;
      if (frame >= length) {
        image = images.get(length-1);
        animation = () -> {
        };
        onAnimationEnd();
      } else {
        image = images.get(frame);
      }
      setUV();
    }
  }
}
