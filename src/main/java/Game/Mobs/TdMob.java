package Game.Mobs;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import Game.Buffs.Modifier;
import Game.DamageType;
import Game.GameObject;
import Game.SquareGrid;
import Game.TickDetect;
import Game.World;
import general.Constants;
import general.Data;
import general.Util;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import windowStuff.AbstractSprite;
import windowStuff.Sprite;

public abstract class TdMob extends GameObject implements TickDetect {

  protected final AbstractSprite sprite;
  protected final SquareGrid<TdMob> grid;
  private final BuffHandler<TdMob> buffHandler;
  private final MoveAlongTrack<TdMob> movement;
  protected double healthPart;
  protected boolean exists;
  protected float vx, vy;

  public TdMob(World world, String image) {
    super(world.getMapData().get(0).x + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread),
        world.getMapData().get(0).y + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread), 0, 0, world);
    clearStats();
    healthPart = 1;
    setSize((int) (2 * stats[Stats.size]), (int) (2 * stats[Stats.size]));
    grid = world.getMobsGrid();
    sprite = new Sprite(image, x, y, width, height, 1, "basic");
    sprite.addToBs(world.getBs());
    exists = true;
    buffHandler = new BuffHandler<>(this);
    movement = new MoveAlongTrack<>(false, world.getMapData(),
        new Point((int) x - world.getMapData().get(0).x,
            (int) y - world.getMapData().get(0).y), stats, Stats.speed, TdMob::passed);
  }

  public TdMob(World world, String image, TdMob parent, int spread) {
    super(parent.x + Data.gameMechanicsRng.nextInt(-spread, spread),
        parent.y + Data.gameMechanicsRng.nextInt(-spread, spread),
        0, 0, world);
    clearStats();
    healthPart = 1;
    setSize((int) (2 * stats[Stats.size]), (int) (2 * stats[Stats.size]));
    grid = world.getMobsGrid();
    sprite = new Sprite(image, x, y, width, height, 1, "basic");
    sprite.addToBs(world.getBs());
    exists = true;
    buffHandler = parent.buffHandler.copyForChild(this);
    movement = new MoveAlongTrack<>(false, world.getMapData(),
        new Point((int) (x - parent.x + parent.movement.offset.x),
            (int) (y - parent.y + parent.movement.offset.y)), stats, Stats.speed, TdMob::passed,
        parent.movement.getProgress());
  }

  @Override
  protected int getStatsCount() {
    return 4;
  }

  @Override
  public abstract void clearStats();

  @Override
  public Rectangle getHitbox() {
    return new Rectangle((int) x - width / 2, (int) y + height / 2, width,
        height);
  }

  @Override
  public void setRotation(float f) {
    super.setRotation(f);
    sprite.setRotation(f+90);
  }

  public TrackProgress getProgress() {
    return movement.progress;
  }

  public boolean addBuff(Buff<TdMob> eff) {
    return buffHandler.add(eff);
  }

  public BuffHandler<TdMob> getBuffHandler() {
    return buffHandler;
  }

  public void takeDamage(float amount, DamageType type) {
    double resistance = 1;
    double eDamage = amount * resistance / stats[Stats.health];
    healthPart -= eDamage;
    if (healthPart <= 0.0000001 && exists) {
      world.setMoney(world.getMoney() + stats[Stats.value]);
      onDeath();

      spawnChildren((float)(-healthPart*stats[Stats.health]));

      delete();
    }
  }

  public void onDeath() {
  }

  @FunctionalInterface
  public interface ChildSpawner{
    TdMob spawn(TdMob parent);
  }

  protected abstract List<ChildSpawner> children();

  private void spawnChildren(float overkill){
    for(var spawner : children()){
      TdMob newBloon = spawner.spawn(this);
      newBloon.takeDamage(overkill,DamageType.TRUE);
      world.addEnemy(newBloon);
    }
  }

  @Override
  public void onGameTick(int tick) {
    buffHandler.tick();
    movement.tick(this);
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

  private void passed() {
    delete();
    world.changeHealth(-1);
  }

  public static class MoveAlongTrack<T extends GameObject> {

    private final boolean reverse;
    private final List<? extends Point> mapData;
    private final Point offset;
    private final float[] stats;
    private final int speedStat;
    private final Modifier<T> onFinish;
    private int nextMapPoint;
    private TrackProgress progress = new TrackProgress(1, 9999);

    public MoveAlongTrack(boolean reverse, List<? extends Point> mapData, Point offset,
        float[] stats, int speedStat, Modifier<T> end) {
      this.reverse = reverse;
      this.nextMapPoint = reverse ? mapData.size() - 1 : 0;
      this.mapData = mapData;
      this.offset = new Point((int) Util.clamp(offset.x, -Constants.MobSpread, Constants.MobSpread),
          (int) Util.clamp(offset.y, -Constants.MobSpread, Constants.MobSpread));
      this.speedStat = speedStat;
      this.stats = stats;
      onFinish = end;
    }

    public MoveAlongTrack(boolean reverse, List<? extends Point> mapData, Point offset,
        float[] stats, int speedStat, Modifier<T> end, int nextMapPoint) {
      this(reverse, mapData, offset, stats, speedStat, end);
      this.nextMapPoint = nextMapPoint;
    }

    public MoveAlongTrack(boolean reverse, List<? extends Point> mapData, Point offset,
        float[] stats, int speedStat, Modifier<T> end, TrackProgress progress) {
      this(reverse, mapData, offset, stats, speedStat, end);
      this.progress = progress;
      nextMapPoint = progress.checkpoint;
    }

    public TrackProgress getProgress() {
      return progress;
    }

    public boolean isDone() {
      return reverse ? nextMapPoint < 0 : nextMapPoint >= mapData.size();
    }

    public void tick(T target) {
      if (isDone()) {
        return;
      }
      Point nextPoint = mapData.get(nextMapPoint);
      int approxDistance = (int) (Math.abs(nextPoint.x + offset.x - target.getX()) + Math.abs(
          nextPoint.y + offset.y - target.getY()));
      progress = new TrackProgress(nextMapPoint, approxDistance);
      if (approxDistance < stats[speedStat]) {
        target.move(nextPoint.x + offset.x, nextPoint.y + offset.y);
        nextMapPoint += reverse ? -1 : 1;
        if (isDone()) {
          onFinish.mod(target);
        }
      } else {
        float rotationToNextPoint = Util.get_rotation(nextPoint.x + offset.x - target.getX(),
            nextPoint.y + offset.y - target.getY());
        float vx = stats[speedStat] * Util.cos(rotationToNextPoint);
        float vy = stats[speedStat] * Util.sin(rotationToNextPoint);
        target.move(target.getX() + vx, target.getY() + vy);
        target.setRotation(rotationToNextPoint - 90f);
      }
    }
  }

  public static final class Stats {

    public static final int size = 0;

    public static final int speed = 1;
    public static final int health = 2;
    public static final int value = 3;

    private Stats() {
    }
  }

  protected abstract int getChildrenSpread();

  public static class TrackProgress implements Comparable<TrackProgress> {

    private final int checkpoint;
    private final int distanceToNext;

    public TrackProgress(int newCheckpoint, int newDistance) {
      checkpoint = newCheckpoint;
      distanceToNext = newDistance;
    }

    public int getCheckpoint() {
      return checkpoint;
    }

    public int getDistanceToNext() {
      return distanceToNext;
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
