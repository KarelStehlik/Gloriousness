package windowStuff;

import Game.Game;
import general.Constants;
import general.Data;
import general.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Sprite implements AbstractSprite {

  public static final Animation noAnim = new Animation() {
    @Override
    public void update(Sprite sprite) {

    }
  };
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
  private int lastGt;
  private boolean deleteOnAnimationEnd = true;

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
    animation = noAnim;
    lastGt = og.lastGt;
  }

  public Sprite(ImageData image, int layer, String shader) {
    this.shader = Data.getShader(shader);
    this.layer = layer;
    setImage(image);
    this.animation = noAnim;
    lastGt = Game.get().getTicks();
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

  public ImageData getImage() {
    return image;
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
  public void setNaturalHeight() {
    setSize(2 * width, 2 * width / (texCoords[4] - texCoords[2]) * (texCoords[3] - texCoords[1]));
  }

  @Override
  public void setNaturalWidth() {
    setSize(2 * height * (texCoords[4] - texCoords[2]) / (texCoords[3] - texCoords[1]), 2 * height);
  }

  @Override
  public Sprite setPosition(float X, float Y) {
    setX(X);
    setY(Y);
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
  public Sprite scale(float multiplierX, float multiplierY) {
    width *= multiplierX;
    height *= multiplierY;
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
    hasUnsavedChanges = true;
    return this;
  }

  @Override
  public float getY() {
    return y;
  }

  @Override
  public Sprite setY(float y) {
    this.y = y;
    hasUnsavedChanges = true;
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
    if (hidden == this.hidden) {
      return;
    }
    this.hidden = hidden;
    if (!hidden) {
      lastGt = Game.get().getTicks();
    }
  }

  public float getOpacity() {
    return opacity;
  }

  public Sprite setOpacity(float opacity) {
    this.opacity = opacity;
    this.setHidden(opacity<=0);
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

  protected void onAnimationEnd() {
    if (deleteOnAnimationEnd) {
      delete();
    }
  }

  private void setUV() {
    texCoords = image.textureCoordinates;
  }

  public synchronized void updateVertices() {
    if (hidden) {
      return;
    }
    {
      int ticks = Game.get().getTicks() - lastGt;
      lastGt += ticks;
      for (int i = 0; i < ticks; i++) {
        animation.update(this);
      }
    }
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

    //position
    vertices[0] = x;
    vertices[1] = y;
    vertices[2] = 1;  //this is like z coords, I made it exist but like it's whatever and unused

    //dunno if color shows up at correct corner
    for (int i = 0; i < 16; i++) {
      vertices[i + 3] = colors[i];
    }

    vertices[19] = texCoords[0];
    vertices[20] = texCoords[1];
    vertices[21] = texCoords[6];
    vertices[22] = texCoords[7];

    vertices[23] = width;
    vertices[24] = height;
    vertices[25] = 0;   //z size
    vertices[26] = 0;   //w size

    vertices[27] = rotation;


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

  public abstract static class Animation {

    private boolean ended = false;

    public abstract void update(Sprite sprite);

    public void end(Sprite sprite) {
      if (Objects.equals(sprite.animation, this)) {
        sprite.animation = noAnim;
        sprite.onAnimationEnd();
      }
      ended = true;
    }

    public boolean hasEnded() {
      return ended;
    }

    protected List<Animation> baseAnimations() {
      return List.of(this);
    }

    public final Animation and(Animation other) {
      var l = new ArrayList<Animation>(2);
      l.addAll(baseAnimations());
      l.addAll(other.baseAnimations());
      return new CompoundAnimation(l);
    }
  }

  public static class CompoundAnimation extends Animation {

    private final List<Animation> animations;

    public CompoundAnimation(List<Animation> anims) {
      animations = anims;
    }

    @Override
    public void update(Sprite sprite) {
      animations.removeIf(Animation::hasEnded);
      for (Animation a : animations) {
        a.update(sprite);
      }
      if (animations.isEmpty()) {
        end(sprite);
      }
    }

    @Override
    protected List<Animation> baseAnimations() {
      return animations;
    }
  }

  public static class FrameAnimation extends Animation {

    private final int length;
    private final float frameLengthGt;
    private final List<ImageData> images;
    private int lifetime = 0;
    private boolean loop = false;

    public FrameAnimation(String name, float duration) {
      this(Graphics.getAnimation(name), duration);
    }

    public FrameAnimation(ImageData img, float duration) {
      this(List.of(img), duration);
    }

    public FrameAnimation(List<ImageData> images, float duration) {
      length = images.size();
      frameLengthGt = 1000 * duration / (length * Game.tickIntervalMillis);
      this.images = images;
    }

    public FrameAnimation loop() {
      loop = true;
      return this;
    }

    @Override
    public void update(Sprite sprite) {
      lifetime++;
      int frame = (int) (lifetime / frameLengthGt);
      if (loop) {
        frame %= length;
      }
      //Log.write(length);
      sprite.hasUnsavedChanges = true;
      if (frame >= length) {
        sprite.image = images.get(length - 1);
        end(sprite);
      } else {
        sprite.image = images.get(frame);
      }
      sprite.setUV();
    }
  }
}
