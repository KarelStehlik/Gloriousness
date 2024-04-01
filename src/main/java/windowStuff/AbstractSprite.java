package windowStuff;

public interface AbstractSprite {

  float getWidth();

  float getHeight();

  AbstractSprite playAnimation(Sprite.Animation anim);

  AbstractSprite setShader(String shader);

  float getRotation();

  AbstractSprite setRotation(float r);

  AbstractSprite setImage(String name);

  void setLayer(int layer);

  AbstractSprite setPosition(float X, float Y);

  AbstractSprite scale(float multiplier);

  AbstractSprite setSize(float w, float h);

  AbstractSprite setColors(float[] colors);

  void delete();

  float getX();

  AbstractSprite setX(float x);

  float getY();

  AbstractSprite setY(float y);

  int getLayer();

  boolean isDeleted();

  AbstractSprite addToBs(SpriteBatching bs);

  boolean isHidden();

  void setHidden(boolean hidden);
}
