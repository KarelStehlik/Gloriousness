package Game;

import general.Util;
import java.awt.Rectangle;

public class GameObject {

  public final long id;
  public final World world;
  protected final float[] stats = new float[getStatsCount()];
  private final Rectangle hitbox;
  public long lastChecked = -9223372036854775807L;
  protected float x, y;
  protected int width, height;
  protected float rotation;

  protected GameObject(float X, float Y, int W, int H, World w) {
    x = X;
    y = Y;
    width = W;
    height = H;
    world = w;
    hitbox = new Rectangle((int) (x - width / 2), (int) (y + height / 2), width, height);
    id = Util.getUid();
  }

  protected int getStatsCount() {
    return 0;
  }

  public float[] getStats() {
    return stats;
  }

  public void clearStats() {
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  protected Rectangle getHitbox() {
    return hitbox;
  }

  public void setRotation(float f) {
    rotation = f;
  }

  public void move(float _x, float _y) {
    x = _x;
    y = _y;
    hitbox.setLocation((int) (x - width / 2), (int) (y + height / 2));
  }

  public void setSize(int _width, int _height) {
    width = _width;
    height = _height;
    hitbox.setSize(width, height);
    hitbox.setLocation((int) (x - width / 2), (int) (y + height / 2));
  }

  public void onStatsUpdate() {
  }
}
