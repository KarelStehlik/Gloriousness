package windowStuff;

import windowStuff.Sprite.Animation;

public class NoSprite implements AbstractSprite {

  @Override
  public float getWidth() {
    return 0;
  }

  @Override
  public float getHeight() {
    return 0;
  }

  @Override
  public void playAnimation(Animation anim) {

  }

  @Override
  public void setShader(String shader) {

  }

  @Override
  public float getRotation() {
    return 0;
  }

  @Override
  public void setRotation(float r) {

  }

  @Override
  public void setImage(String name) {

  }

  @Override
  public void setPosition(float X, float Y) {

  }

  @Override
  public void scale(float multiplier) {

  }

  @Override
  public void setSize(float w, float h) {

  }

  @Override
  public void setColors(float[] colors) {

  }

  @Override
  public void delete() {

  }

  @Override
  public float getX() {
    return 0;
  }

  @Override
  public void setX(float x) {

  }

  @Override
  public float getY() {
    return 0;
  }

  @Override
  public void setY(float y) {

  }

  @Override
  public Batch getBatch() {
    return null;
  }

  @Override
  public int getLayer() {
    return 0;
  }

  @Override
  public boolean isDeleted() {
    return false;
  }

  @Override
  public void addToBs(BatchSystem bs) {

  }

  @Override
  public void unBatch() {

  }
}
