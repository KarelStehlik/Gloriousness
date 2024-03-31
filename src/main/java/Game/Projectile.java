package Game;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import general.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import windowStuff.Sprite;

public class Projectile extends GameObject implements TickDetect {

  private final Sprite sprite;
  private final Collection<OnCollideComponent<Player>> playerCollides = new ArrayList<>(1);
  private final Collection<TdMob> alreadyHitMobs;
  private final Collection<OnCollideComponent<TdMob>> mobCollides = new ArrayList<>(1);
  private final Collection<Projectile> alreadyHitProjectiles;
  private final Collection<OnCollideComponent<Projectile>> projectileCollides = new ArrayList<>(1);
  private final BuffHandler<Projectile> bh = new BuffHandler<>(this);
  private float vx, vy;
  private boolean wasDeleted = false;
  private boolean active = true;
  private float rotation;
  private boolean alreadyHitPlayer = false;

  protected Projectile(World world, String image, float X, float Y, float speed, float rotation,
      int W, int H, int pierce, float size, float duration, float power) {
    super(X, Y, (int) size, (int) size, world);
    sprite = new Sprite(image, X, Y, W, H, 1, "basic");
    sprite.setRotation(rotation - 90);
    world.getBs().addSprite(sprite);
    stats[Stats.pierce] = pierce;
    stats[Stats.speed] = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
    stats[Stats.size] = size;
    stats[Stats.duration] = duration * 1024;
    this.rotation = rotation;
    alreadyHitMobs = new HashSet<>(pierce);
    alreadyHitProjectiles = new HashSet<>(pierce);
    stats[Stats.power] = power;
  }

  public static void bounce(Projectile p) {
    float s = p.stats[Stats.size] / 2;
    float minx = p.x - s;
    float miny = p.y - s;
    float maxx = p.x + s;
    float maxy = p.y + s;

    if (minx < 0) {
      p.move(p.x - 2 * minx, p.y);
      p.setRotation(180 - p.rotation);
    }
    if (miny < 0) {
      p.move(p.x, p.y - 2 * miny);
      p.setRotation(-p.rotation);
    }
    if (maxx > World.WIDTH) {
      p.move(2 * World.WIDTH - maxx - s, p.y);
      p.setRotation(180 - p.rotation);
    }
    if (maxy > World.HEIGHT) {
      p.move(p.x, 2 * World.HEIGHT - maxy - s);
      p.setRotation(-p.rotation);
    }
  }

  @Override
  protected int getStatsCount() {
    return 5;
  }

  public boolean addBuff(Buff<Projectile> buff) {
    return bh.add(buff);
  }

  public float getPower() {
    return stats[Stats.power];
  }

  protected void changePierce(int amount) {
    stats[Stats.pierce] += amount;
    if (stats[Stats.pierce] <= 0) {
      delete();
    }
  }

  public float getSpeed() {
    return stats[Stats.speed];
  }

  public void setSpeed(float speed) {
    stats[Stats.speed] = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
  }

  public float getRotation() {
    return rotation;
  }

  @Override
  public void setRotation(float rotation) {
    this.rotation = rotation;
    sprite.setRotation(rotation - 90);
    vx = Util.cos(rotation) * stats[Stats.speed];
    vy = Util.sin(rotation) * stats[Stats.speed];
  }

  @Override
  public void move(float _x, float _y) {
    super.move(_x, _y);
    sprite.setPosition(x, y);
  }

  @Override
  public void onGameTick(int tick) {
    if (!active) {
      return;
    }
    fly();
    bh.tick();
    handleCollisions();
    world.getProjectilesGrid().add(this);
    stats[Stats.duration] -= Game.tickIntervalMillis;
    if (stats[Stats.duration] <= 0) {
      delete();
    }
  }

  @Override
  public void delete() {
    onDelete();
    active = false;
    wasDeleted = true;
    sprite.delete();
  }

  @Override
  public final boolean WasDeleted() {
    return wasDeleted;
  }

  public void setActive(boolean a) {
    active = a;
    sprite.setHidden(!a);
  }

  private void fly() {
    move(x + vx, y + vy);
    handleCollisions();
    world.getProjectilesGrid().add(this);
  }

  private void handleCollisions() {
    if (!mobCollides.isEmpty()) {
      world.getMobsGrid()
          .callForEachCircle((int) x, (int) y, (int) (stats[Stats.size] / 2), this::collide);
    }

    if (!playerCollides.isEmpty()) {
      collide(world.getPlayer());
    }
  }

  protected void collide(Player e) {
    if (wasDeleted || e.WasDeleted() || alreadyHitPlayer
        || Util.distanceSquared(x - e.x, y - e.y) > Util.square(e.width + stats[Stats.size]) / 4) {
      return;
    }
    boolean collided = false;
    alreadyHitPlayer = true;
    for (var component : playerCollides) {
      collided |= component.collide(this, e);
    }
    if (collided) {
      changePierce(-1);
    }
  }

  public void addPlayerCollide(OnCollideComponent<Player> component) {
    playerCollides.add(component);
  }

  protected void collide(TdMob e) {
    if (wasDeleted || e.WasDeleted() || alreadyHitMobs.contains(e)) {
      return;
    }
    alreadyHitMobs.add(e);
    boolean collided = false;
    for (var component : mobCollides) {
      collided |= component.collide(this, e);
    }
    if (collided) {
      changePierce(-1);
    }
  }

  public void addMobCollide(OnCollideComponent<TdMob> component) {
    mobCollides.add(component);
  }

  public void handleProjectileCollision() {
    if (!projectileCollides.isEmpty()) {
      world.getProjectilesGrid()
          .callForEachCircle((int) x, (int) y, (int) (stats[Stats.size] / 2), this::collide);
    }
  }

  protected void collide(Projectile e) {
    if (!active || !e.active || e.equals(this) || alreadyHitProjectiles.contains(e)) {
      return;
    }
    alreadyHitProjectiles.add(e);
    boolean collided = false;
    for (var component : projectileCollides) {
      collided |= component.collide(this, e);
    }
    if (collided) {
      changePierce(-1);
    }
  }

  public void addProjectileCollide(OnCollideComponent<Projectile> component) {
    projectileCollides.add(component);
  }

  private void onDelete() {
    bh.delete();
  }

  @FunctionalInterface
  public interface OnCollideComponent<T extends GameObject> {

    boolean collide(Projectile proj, T target);
  }

  public static class Stats {

    public static final int pierce = 0;
    public static final int speed = 1;
    public static final int size = 2;
    public static final int duration = 3;
    public static final int power = 4;
  }
}
