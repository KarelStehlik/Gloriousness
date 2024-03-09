package Game;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import general.Constants;
import general.Data;
import general.Util;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import windowStuff.AbstractSprite;
import windowStuff.Graphics;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public abstract class TdMob extends GameObject implements TickDetect {

  public final BaseStats baseStats;
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

  public BuffHandler<TdMob> getBuffHandler(){
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

  public final IgniteSet ignite;

  public class IgniteSet{
    private static class Ignite{
      protected float damagePerTick, duration;
      protected Ignite(float dmg, float dur){
        damagePerTick=dmg;
        duration=dur;
      }
    }
    private final List<Ignite> ignites = new ArrayList<>(1);
    private final Sprite sprite;

    protected IgniteSet(SpriteBatching bs){
      sprite = new Sprite("Fireball-0", 1).addToBs(bs).setSize(50, 50).setPosition(x,y);
      sprite.setRotation(180);

      /*int imageId = Graphics.getLoadedImages().getImageId("Explosion1-0");
      String newTexture = Graphics.getLoadedImages().getImageTexture(imageId);
      var coo = Graphics.getLoadedImages().getImageCoordinates(imageId);
      System.out.println(imageId);
      System.out.println(newTexture);
      System.out.println(Arrays.toString(coo));*/
      sprite.playAnimation(sprite.new BasicAnimation("Fireball-0",1).loop());
      sprite.setHidden(true);
    }

    private void tick(){
      sprite.setPosition(x,y);
      float damage = 0;
      for (Ignite ig: ignites) {
        damage+=ig.damagePerTick;
        ig.duration-=Game.tickIntervalMillis;
      }
      float power = damage / baseStats.health * 1000;
      sprite.setSize(power* baseStats.size, power* baseStats.size);
      takeDamage(damage, DamageType.TRUE);
      ignites.removeIf(ig->ig.duration<=0);
      if(ignites.isEmpty()){
        sprite.setHidden(true);
      }
    }
    public void add(float damagePerTick, float duration){
      ignites.add(new Ignite(damagePerTick,duration));
      sprite.setHidden(false);
    }
    private void delete(){
      sprite.delete();
      ignites.clear();
    }
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
}
