package Game;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import general.Constants;
import general.Data;
import general.Util;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.TreeSet;
import windowStuff.AbstractSprite;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public abstract class TdMob extends GameObject implements TickDetect {

  public final BaseStats baseStats;
  public final IgniteSet ignite;
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

  public TdMob(World world, String name, String image, BaseStats newStats) {
    super(world.getMapData().get(0).x + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread),
        world.getMapData().get(0).y + Data.gameMechanicsRng.nextInt(-Constants.MobSpread,
            Constants.MobSpread), 150, 150, world);
    baseStats = newStats;
    healthPart = 1;
    this.name = name;
    offset = new Point((int) x - world.getMapData().get(0).x,
        (int) y - world.getMapData().get(0).y);
    setSize((int) (2 * baseStats.size), (int) (2 * baseStats.size));
    grid = world.getMobsGrid();
    float rotationToNextPoint = Util.get_rotation(world.getMapData().get(nextMapPoint).x - x,
        world.getMapData().get(nextMapPoint).y - y);
    vx = baseStats.speed * Util.cos(rotationToNextPoint);
    vy = baseStats.speed * Util.sin(rotationToNextPoint);
    rotation = Data.gameMechanicsRng.nextFloat(5) - 2.5f;
    sprite = new Sprite(image, x, y, width, height, 1, "basic");
    //sprite = new NoSprite();
    sprite.addToBs(world.getBs());
    exists = true;
    buffHandler = new BuffHandler<>(this);
    ignite = new IgniteSet(world.getBs());
  }

  public TrackProgress getProgress() {
    return progress;
  }

  public void addBuff(Buff<TdMob> eff) {
    buffHandler.add(eff);
  }

  public BuffHandler<TdMob> getBuffHandler() {
    return buffHandler;
  }

  public void takeDamage(float amount, DamageType type) {
    double resistance = 1;
    double eDamage = amount * resistance / baseStats.health;
    healthPart -= eDamage;
    if (healthPart <= 0 && exists) {
      world.setMoney(world.getMoney() + baseStats.value);
      onDeath();
      delete();
    }
  }

  public void onDeath() {
  }

  @Override
  public void onGameTick(int tick) {
    buffHandler.tick();
    ignite.tick();
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
    ignite.delete();
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
    if (approxDistance < baseStats.speed) {
      x = nextPoint.x + offset.x;
      y = nextPoint.y + offset.y;
      nextMapPoint += 1;
      if (nextMapPoint >= world.getMapData().size()) {
        passed();
      }
    } else {
      float rotationToNextPoint = Util.get_rotation(nextPoint.x + offset.x - x,
          nextPoint.y + offset.y - y);
      vx = baseStats.speed * Util.cos(rotationToNextPoint);
      vy = baseStats.speed * Util.sin(rotationToNextPoint);
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

  public static class BaseStats {

    public float size;
    public float speed;
    public float health;
    public float value;

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

  public class IgniteSet {

    private final TreeSet<Ignite> ignites = new TreeSet<>();
    private final Sprite fireSprite;
    private float age = 0;
    private float dpTick = 0;
    protected IgniteSet(SpriteBatching bs) {
      fireSprite = new Sprite("Fireball-0", 1).addToBs(bs).setSize(50, 50).setPosition(x, y);
      fireSprite.setRotation(180);
      fireSprite.playAnimation(fireSprite.new BasicAnimation("Fireball-0", 1).loop());
      fireSprite.setHidden(true);
    }

    private void tick() {
      age += Game.tickIntervalMillis;

      for (Iterator<Ignite> iterator = ignites.iterator(); iterator.hasNext(); ) {
        Ignite ig = iterator.next();
        if (ig.expiryTime > age) {
          break;
        }
        iterator.remove();
        dpTick -= ig.damagePerTick;
      }

      float power = dpTick / baseStats.health * 1000;
      fireSprite.setSize(power * baseStats.size, power * baseStats.size);
      fireSprite.setPosition(x, y + power * baseStats.size * .4f);
      takeDamage(dpTick, DamageType.TRUE);
      if (ignites.isEmpty()) {
        fireSprite.setHidden(true);
      }
    }

    public void add(float damagePerTick, float duration) {
      ignites.add(new Ignite(damagePerTick, age + duration));
      fireSprite.setHidden(false);
      this.dpTick += damagePerTick;
    }

    private void delete() {
      fireSprite.delete();
      ignites.clear();
    }

    private static class Ignite implements Comparable<Ignite> {

      private static long staticId = 0;
      protected final float damagePerTick, expiryTime;
      private final long id;

      protected Ignite(float dmg, float dur) {
        damagePerTick = dmg;
        expiryTime = dur;
        id = staticId;
        staticId++;
      }

      @Override
      public int compareTo(Ignite o) {
        int floatComp = Float.compare(expiryTime, o.expiryTime);
        if (floatComp != 0) {
          return floatComp;
        }
        return Long.compare(id, o.id);
      }
    }
  }
}
