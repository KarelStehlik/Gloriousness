package Game;

import general.Constants;
import general.Data;
import general.Util;
import java.awt.Point;
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
  private int nextMapPoint = 1;
  private final Point offset;

  public Mob(World world, String name, String image) {
    super(world.getMapData().get(0).x + Data.gameMechanicsRng.nextInt(-Constants.MobSpread, Constants.MobSpread), world.getMapData().get(0).y + Data.gameMechanicsRng.nextInt(-Constants.MobSpread, Constants.MobSpread), 150, 150, world);
    baseStats = Data.getEntityStats("mob", name);
    stats = new HashMap<>(baseStats);
    health = stats.get("health");
    this.name = name;
    offset = new Point((int)x-world.getMapData().get(0).x, (int)y-world.getMapData().get(0).y);
    setSize((int) (2 * stats.get("size")), (int) (2 * stats.get("size")));
    grid = world.getMobsGrid();
    float rotationToNextPoint = Util.get_rotation(world.getMapData().get(nextMapPoint).x - x,
        world.getMapData().get(nextMapPoint).y - y);
    vx = stats.get("speed") * Util.cos(rotationToNextPoint);
    vy = stats.get("speed") * Util.sin(rotationToNextPoint);
    rotation = Data.gameMechanicsRng.nextFloat(5) - 2.5f;
    sprite = new Sprite(image, x, y, width, height, 1, "basic");
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
    // handleCollisions();
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
    Point nextPoint = world.getMapData().get(nextMapPoint);
    if (Math.abs(nextPoint.x + offset.x - x) + Math.abs(nextPoint.y + offset.y - y) < stats.get("speed")) {
      x = nextPoint.x + offset.x;
      y = nextPoint.y + offset.y;
      nextMapPoint += 1;
      if (nextMapPoint >= world.getMapData().size()) {
        passed();
      }
    } else {
      float rotationToNextPoint = Util.get_rotation(nextPoint.x + offset.x - x,
          nextPoint.y + offset.y - y);
      vx = stats.get("speed") * Util.cos(rotationToNextPoint);
      vy = stats.get("speed") * Util.sin(rotationToNextPoint);
      x = x + vx;
      y = y + vy;
    }
  }

  private void passed() {
    delete();
    world.changeHealth(-1);
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
