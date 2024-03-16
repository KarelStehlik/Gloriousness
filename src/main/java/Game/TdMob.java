package Game;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import Game.Buffs.Modifier;
import general.Constants;
import general.Data;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import windowStuff.AbstractSprite;
import windowStuff.Sprite;

public abstract class TdMob extends GameObject implements TickDetect {

  public final BaseStats baseStats;
  protected final AbstractSprite sprite;
  protected final SquareGrid<TdMob> grid;
  protected final String name;
  private final BuffHandler<TdMob> buffHandler;
  private final MoveAlongTrack<TdMob> movement;
  protected double healthPart;
  protected boolean exists;
  protected float vx, vy;

  public TdMob(World world, String name, String image, BaseStats newStats) {
    super(world.getMapData().get(0).x + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread),
        world.getMapData().get(0).y + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread), 150, 150, world);
    baseStats = newStats;
    healthPart = 1;
    this.name = name;
    setSize((int) (2 * baseStats.size.get()), (int) (2 * baseStats.size.get()));
    grid = world.getMobsGrid();
    rotation = Data.gameMechanicsRng.nextFloat(5) - 2.5f;
    sprite = new Sprite(image, x, y, width, height, 1, "basic");
    //sprite = new NoSprite();
    sprite.addToBs(world.getBs());
    exists = true;
    buffHandler = new BuffHandler<>(this);
    movement = new MoveAlongTrack<TdMob>(false, world.getMapData(),
        new Point((int) x - world.getMapData().get(0).x,
            (int) y - world.getMapData().get(0).y), baseStats.speed, t -> t.passed());
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
    double eDamage = amount * resistance / baseStats.health.get();
    healthPart -= eDamage;
    if (healthPart <= 0 && exists) {
      world.setMoney(world.getMoney() + baseStats.value.get());
      onDeath();
      delete();
    }
  }

  public void onDeath() {
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

  @Override
  public Rectangle getHitbox() {
    return new Rectangle((int) x - width / 2, (int) y + height / 2, width,
        height);
  }

  @Override
  public void setRotation(float f) {
    super.setRotation(f);
    sprite.setRotation(f);
  }

  public static class MoveAlongTrack<T extends GameObject> {

    private final boolean reverse;
    private final List<? extends Point> mapData;
    private final Point offset;
    private final RefFloat speed;
    private final Modifier<T> onFinish;
    private int nextMapPoint;
    private TrackProgress progress = new TrackProgress(0, 0);

    public MoveAlongTrack(boolean reverse, List<? extends Point> mapData, Point offset,
        RefFloat speed, Modifier<T> end) {
      this.reverse = reverse;
      this.nextMapPoint = reverse ? mapData.size() - 1 : 0;
      this.mapData = mapData;
      this.offset = offset;
      this.speed = speed;
      onFinish = end;
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
      int approxDistance = (int) (Math.abs(nextPoint.x + offset.x - target.x) + Math.abs(
          nextPoint.y + offset.y - target.y));
      progress = new TrackProgress(nextMapPoint, approxDistance);
      if (approxDistance < speed.get()) {
        target.x = nextPoint.x + offset.x;
        target.y = nextPoint.y + offset.y;
        nextMapPoint += reverse ? -1 : 1;
        if (isDone()) {
          onFinish.mod(target);
        }
      } else {
        float rotationToNextPoint = Util.get_rotation(nextPoint.x + offset.x - target.x,
            nextPoint.y + offset.y - target.y);
        float vx = speed.get() * Util.cos(rotationToNextPoint);
        float vy = speed.get() * Util.sin(rotationToNextPoint);
        target.x = target.x + vx;
        target.y = target.y + vy;
        target.setRotation(rotationToNextPoint - 90f);
      }
    }
  }

  public static class BaseStats {

    public RefFloat size;
    public RefFloat speed;
    public RefFloat health;
    public RefFloat value;

    public BaseStats() {
      init();
    }

    public void init() {
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
    public int compareTo(TrackProgress o) {
      if (checkpoint == o.checkpoint) {
        return o.distanceToNext - distanceToNext;
      }
      return checkpoint - o.checkpoint;
    }
  }
}
