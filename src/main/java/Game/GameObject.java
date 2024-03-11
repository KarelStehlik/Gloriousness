package Game;

import java.awt.Rectangle;

public class GameObject {

  private static int idGen = Integer.MIN_VALUE;
  public final int id;
  private final Rectangle hitbox;
  public long lastChecked = -9223372036854775807L;
  protected float x, y;
  protected int width, height;
  protected World world;

  protected GameObject(float X, float Y, int W, int H, World w) {
    x = X;
    y = Y;
    width = W;
    height = H;
    world = w;
    hitbox = new Rectangle((int) (x - width / 2), (int) (y + height / 2), width, height);
    id = idGen;
    idGen++;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  Rectangle getHitbox() {
    return hitbox;
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

  public void clearStats() {
  }
}
