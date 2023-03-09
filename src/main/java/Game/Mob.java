package Game;

import general.Constants;
import general.Data;
import general.Util;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import windowStuff.AbstractSprite;
import windowStuff.Sprite;

public abstract class Mob extends GameObject implements TickDetect {

  public final List<StatusEffect> effects = new LinkedList<>();
  protected final AbstractSprite sprite;
  protected final float rotation;
  protected final SquareGrid<Mob> grid;
  final Map<String, Float> stats;
  final Map<String, Float> baseStats;
  final String name;
  private final Point offset;
  protected float health;
  protected boolean exists;
  protected float vx, vy;
  private int nextMapPoint = 1;
  private TrackProgress progress = new TrackProgress(0, 0);

  public Mob(World world, String name, String image) {
    super(world.getMapData().get(0).x + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread),
        world.getMapData().get(0).y + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread), 150, 150, world);
    baseStats = Data.getEntityStats("mob", name);
    stats = new HashMap<>(baseStats);
    health = stats.get("health");
    this.name = name;
    offset = new Point((int) x - world.getMapData().get(0).x,
        (int) y - world.getMapData().get(0).y);
    setSize((int) (2 * stats.get("size")), (int) (2 * stats.get("size")));
    grid = world.getMobsGrid();
    float rotationToNextPoint = Util.get_rotation(world.getMapData().get(nextMapPoint).x - x,
        world.getMapData().get(nextMapPoint).y - y);
    vx = stats.get("speed") * Util.cos(rotationToNextPoint);
    vy = stats.get("speed") * Util.sin(rotationToNextPoint);
    rotation = Data.gameMechanicsRng.nextFloat(5) - 2.5f;
    sprite = new Sprite(image, x, y, width, height, 1, "basic");
    //sprite = new NoSprite();
    sprite.addToBs(world.getBs());
    exists = true;
  }

  public void addStatusEffect(StatusEffect e) {
    effects.add(e);
    effects.sort(Comparator.comparingInt(eff -> eff.priority));
    updateStats();
  }

  private void updateStats() {
    float hpPart = health / stats.get("health");
    stats.putAll(baseStats);
    for (StatusEffect eff : effects) {
      eff.updateFunc.update(this);
    }
    health = stats.get("health") * hpPart;
  }

  public TrackProgress getProgress() {
    return progress;
  }

  public void takeDamage(float amount, DamageType type) {
    float resistance = stats.getOrDefault(type.resistanceName, 1f);
    health -= amount * resistance;
    if (health <= 0 && exists) {
      world.setMoney(world.getMoney() + stats.get("value"));
      onDeath();
      delete();
    }
  }

  public void onDeath() {
  }

  @Override
  public void onGameTick(int tick) {
    runAI();
    grid.add(this);
    miscTickActions();
  }

  private void miscTickActions() {
    sprite.setPosition(x, y);
  }

  private void handleCollisions() {
    grid.callForEach(this.getHitbox(), this::collide);
  }

  private void runAI() {
    Point nextPoint = world.getMapData().get(nextMapPoint);
    int approxDistance = (int) (Math.abs(nextPoint.x + offset.x - x) + Math.abs(
        nextPoint.y + offset.y - y));
    progress = new TrackProgress(nextMapPoint, approxDistance);
    if (approxDistance < stats.get("speed")) {
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
      sprite.setRotation(rotationToNextPoint - 90f);
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

  public static class StatusEffect {

    public final int priority;
    public final UpdateFunc updateFunc;

    public StatusEffect(int prio, UpdateFunc f) {
      priority = prio;
      updateFunc = f;
    }

    @FunctionalInterface
    public interface UpdateFunc {

      void update(Mob mob);
    }
  }

  public static class TrackProgress implements Comparable<TrackProgress> {

    private final int checkpoint;
    private final int distanceToNext;

    public TrackProgress(int newCheckpoint, int newDistance) {
      checkpoint = newCheckpoint;
      distanceToNext = newDistance;
    }

    @Override
    public String toString() {
      return checkpoint + ", " + distanceToNext;
    }

    @Override
    public int compareTo(TrackProgress o) {
      if (checkpoint == o.checkpoint) {
        return o.distanceToNext - distanceToNext;
      }
      return checkpoint - o.checkpoint;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof TrackProgress && compareTo((TrackProgress) o) == 0;
    }

    @Override
    public int hashCode() {
      int result = checkpoint;
      result = 31 * result + distanceToNext;
      return result;
    }
  }
}
