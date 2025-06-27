package windowStuff;

public interface AbstractSprite {

  float getWidth();

  float getHeight();

  AbstractSprite playAnimation(Sprite.Animation anim);

  AbstractSprite setShader(String shader);

  float getRotation();

  AbstractSprite setRotation(float r);

  AbstractSprite setImage(ImageData imageId);

  AbstractSprite setImage(String name);

  void setNaturalHeight();

  AbstractSprite setPosition(float X, float Y);

  AbstractSprite scale(float multiplier);

  AbstractSprite scale(float multiplierX, float multiplierY);

  AbstractSprite setSize(float w, float h);

  AbstractSprite setColors(float[] colors);

  void delete();

  float getX();

  AbstractSprite setX(float x);

  float getY();

  AbstractSprite setY(float y);

  int getLayer();

  AbstractSprite setLayer(int layer);

  boolean isDeleted();

  AbstractSprite addToBs(SpriteBatching bs);

  boolean isHidden();

  void setHidden(boolean hidden);
}
