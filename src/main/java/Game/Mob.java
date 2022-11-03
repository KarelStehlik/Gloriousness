package Game;

import general.Constants;
import general.Data;
import general.Util;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import windowStuff.Sprite;

public abstract class Mob extends GameObject implements TickDetect {

  protected final Sprite sprite;
  protected final float rotation;
  protected final SquareGrid<Mob> grid;
  final Map<String, Float> stats;
  final Map<String, Float> baseStats;
  final String name;
  protected float health;
  protected boolean exists;
  protected float vx, vy;
  protected float currentAngle = 0;

  public Mob(World world, String name, String image) {
    super(150, 150, 150, 150, world);
    baseStats = Data.getEntityStats("mob", name);
    stats = new HashMap<>(baseStats);
    health = stats.get("health");
    this.name = name;
    setSize((int) (2 * stats.get("size")), (int) (2 * stats.get("size")));
    grid = world.getMobsGrid();
    vx = Data.gameMechanicsRng.nextFloat(stats.get("speed"));
    vy = Data.gameMechanicsRng.nextFloat(stats.get("speed"));
    rotation = Data.gameMechanicsRng.nextFloat(5) - 2.5f;
    sprite = new Sprite(image, x, y, width, height, 0, "basic");
    world.getBs().addSprite(sprite);
    exists = true;
  }

  public void takeDamage(float amount, DamageType type) {
    float resistance = stats.getOrDefault(type.resistanceName, 1f);
    health -= amount * resistance;
    if (health <= 0 && exists) {
      delete();
    }
  }

  @Override
  public void onGameTick(int tick) {
    runAI();
    handleCollisions();
    grid.add(this);
    miscTickActions();
  }

  private void miscTickActions() {
    sprite.setRotation(currentAngle);
    sprite.setPosition(x, y);
  }

  private void handleCollisions() {
    grid.callForEach(this.getHitbox(), this::collide);
  }

  private void runAI() {
    x = x + vx;
    y = y + vy;
    if (x < 0) {
      x = -x;
      vx = -vx;
    } else if (x > Constants.screenSize.x) {
      x = 2 * Constants.screenSize.x - x;
      vx = -vx;
    }
    if (y < 0) {
      y = -y;
      vy = -vy;
    } else if (y > Constants.screenSize.y) {
      y = 2 * Constants.screenSize.y - y;
      vy = -vy;
    }
    currentAngle += rotation;
  }

  private void collide(Mob other) {
    if (!other.canCollide) {
      return;
    }
    float distanceSq = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
    int minDistance = (int) Util.square(stats.get("size") + other.stats.get("size"));
    if (distanceSq < minDistance) {
      float dir = Util.get_rotation(x - other.x, y - other.y);
      float overlap =
          ((stats.get("size") + other.stats.get("size")) - (float) Math.sqrt(distanceSq)) / 2;
      float sin = Util.sin(dir), cos = Util.cos(dir);
      x += overlap * cos;
      y += overlap * sin;
      other.x -= overlap * cos;
      other.y -= overlap * sin;
    }
  }

  @Override
  public void delete() {
    sprite.delete();
    exists = false;
  }

  @Override
  public boolean WasDeleted() {
    return !exists;
  }

  @Override
  public Rectangle getHitbox() {
    return new Rectangle((int) x - width / 2, (int) y + height / 2, width,
        height);
  }
}
