package Game;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import general.Constants;
import general.Data;
import general.Util;
import java.awt.Point;
import java.awt.Rectangle;
import windowStuff.AbstractSprite;
import windowStuff.Sprite;

public abstract class TdMob extends GameObject implements TickDetect {

  protected final AbstractSprite sprite;
  protected final float rotation;
  protected final SquareGrid<TdMob> grid;
  protected final String name;
  private final BuffHandler<TdMob> buffHandler;
  private final Point offset;
  protected double healthPart;
  protected boolean exists;
  protected float vx, vy;
  private int nextMapPoint = 1;
  private TrackProgress progress = new TrackProgress(0, 0);

  public TdMob(World world, String name, String image) {
    super(world.getMapData().get(0).x + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread),
        world.getMapData().get(0).y + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread), 150, 150, world, Data.getEntityStats("mob", name));
    healthPart = 1;
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
    buffHandler = new BuffHandler<>(this);
  }

  public TrackProgress getProgress() {
    return progress;
  }

  public void addBuff(Buff<TdMob> eff) {
    buffHandler.add(eff);
  }

  public void takeDamage(float amount, DamageType type) {
    float resistance = stats.getOrDefault(type.resistanceName, 1f);
    healthPart -= amount * resistance / stats.get("health");
    if (healthPart <= 0 && exists) {
      world.setMoney(world.getMoney() + stats.get("value"));
      onDeath();
      delete();
    }
  }

  public void onDeath() {
  }

  @Override
  public void onGameTick(int tick) {
    buffHandler.tick();
    runAI();
    grid.add(this);
    miscTickActions();
    sprite.setPosition(x, y);

  }

  @Override
  public void delete() {
    sprite.delete();
    exists = false;
    buffHandler.delete();
  }

  @Override
  public boolean WasDeleted() {
    return !exists;
  }

  private void miscTickActions() {
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

  @Override
  public Rectangle getHitbox() {
    return new Rectangle((int) x - width / 2, (int) y + height / 2, width,
        height);
  }

  public static class TrackProgress implements Comparable<TrackProgress> {

    private final int checkpoint;
    private final int distanceToNext;

    public TrackProgress(int newCheckpoint, int newDistance) {
      checkpoint = newCheckpoint;
      distanceToNext = newDistance;
    }

    @Override
    public int compareTo(TrackProgress o) {
      if (checkpoint == o.checkpoint) {
        return o.distanceToNext - distanceToNext;
      }
      return checkpoint - o.checkpoint;
    }
  }
}
