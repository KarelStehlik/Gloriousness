package Game;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class GameObject implements Comparable<GameObject> {

  private static int idGen = Integer.MIN_VALUE;
  public final int id;
  private final Rectangle hitbox;
  protected float x, y;
  protected int width, height;
  protected boolean canCollide = true;
  protected World world;
  public long lastChecked = -9223372036854775807L;
  public final Map<String, Float> stats;
  protected final Map<String, Float> baseStats;

  protected GameObject(float X, float Y, int W, int H, World w, Map<String, Float> stats) {
    x = X;
    y = Y;
    width = W;
    height = H;
    world = w;
    hitbox = new Rectangle((int) (x - width / 2), (int) (y + height / 2), width, height);
    id = idGen;
    idGen++;
    baseStats=stats;
    this.stats= baseStats==null? null : new HashMap<>(baseStats);
  }

  Rectangle getHitbox() {
    return hitbox;
  }

  void move(float _x, float _y) {
    x = _x;
    y = _y;
    hitbox.setLocation((int) (x - width / 2), (int) (y + height / 2));
  }

  void setSize(int _width, int _height) {
    width = _width;
    height = _height;
    hitbox.setSize(width, height);
    hitbox.setLocation((int) (x - width / 2), (int) (y + height / 2));
  }

  @Override
  public int compareTo(GameObject o) {
    return id - o.id;
  }
}
