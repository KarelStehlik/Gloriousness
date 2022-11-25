package windowStuff;

interface AbstractSprite {

  float getWidth();

  float getHeight();

  void playAnimation(Sprite.Animation anim);

  void setShader(String shader);

  float getRotation();

  void setRotation(float r);

  void setImage(String name);

  void setPosition(float X, float Y);

  void scale(float multiplier);

  void setSize(float w, float h);

  void setColors(float[] colors);

  void delete();

  float getX();

  void setX(float x);

  float getY();

  void setY(float y);

  Batch getBatch();

  int getLayer();

  boolean isDeleted();

  void addToBs(BatchSystem bs);

  void unBatch();
}
