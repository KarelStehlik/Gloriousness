package windowStuff;

import windowStuff.Sprite.Animation;

public class NoSprite implements AbstractSprite {

  boolean deleted = false;

  @Override
  public float getWidth() {
    return 0;
  }

  @Override
  public float getHeight() {
    return 0;
  }

  @Override
  public NoSprite playAnimation(Animation anim) {
    return this;
  }

  @Override
  public NoSprite setShader(String shader) {
    return this;
  }

  @Override
  public float getRotation() {
    return 0;
  }

  @Override
  public NoSprite setRotation(float r) {
    return this;
  }

  @Override
  public NoSprite setImage(String name) {
    return this;
  }

  @Override
  public NoSprite setPosition(float X, float Y) {
    return this;
  }

  @Override
  public NoSprite scale(float multiplier) {
    return this;
  }

  @Override
  public NoSprite setSize(float w, float h) {
    return this;
  }

  @Override
  public NoSprite setColors(float[] colors) {
    return this;
  }

  @Override
  public void delete() {
    deleted = true;
  }

  @Override
  public float getX() {
    return 0;
  }

  @Override
  public NoSprite setX(float x) {
    return this;
  }

  @Override
  public float getY() {
    return 0;
  }

  @Override
  public NoSprite setY(float y) {
    return this;
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
    return deleted;
  }

  @Override
  public NoSprite addToBs(BatchSystem bs) {
    return this;
  }

  @Override
  public void unBatch() {

  }
}
