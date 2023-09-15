package Game;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class GameObject {

  private static int idGen = Integer.MIN_VALUE;
  public final int id;
  public final Map<String, Float> stats;
  public final Map<String, Float> baseStats;
  private final Rectangle hitbox;
  public long lastChecked = -9223372036854775807L;
  protected float x, y;
  protected int width, height;
  protected World world;

  protected GameObject(float X, float Y, int W, int H, World w, Map<String, Float> stats) {
    x = X;
    y = Y;
    width = W;
    height = H;
    world = w;
    hitbox = new Rectangle((int) (x - width / 2), (int) (y + height / 2), width, height);
    id = idGen;
    idGen++;
    baseStats = stats;
    this.stats = baseStats == null ? null : new HashMap<>(baseStats);
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

  public void onStatsUpdate(){}
}
