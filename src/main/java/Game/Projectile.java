package Game;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import general.RefFloat;
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
  public RefFloat power;
  protected int pierce;
  protected float size;
  private float speed;
  private float vx, vy;
  private float duration;
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
    this.pierce = pierce;
    this.speed = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
    this.size = size;
    this.duration = duration * 1024;
    this.rotation = rotation;
    alreadyHitMobs = new HashSet<>(pierce);
    alreadyHitProjectiles = new HashSet<>(pierce);
    this.power = new RefFloat(power);
  }

  public static void bounce(Projectile p) {
    float s = p.size / 2;
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

  public boolean addBuff(Buff<Projectile> buff) {
    return bh.add(buff);
  }

  public float getPower() {
    return power.get();
  }

  protected void changePierce(int amount) {
    pierce += amount;
    if (pierce <= 0) {
      delete();
    }
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
    sprite.setRotation(rotation - 90);
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
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
    duration -= Game.tickIntervalMillis;
    if (duration <= 0) {
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

  @Override
  public void move(float _x, float _y) {
    super.move(_x, _y);
    sprite.setPosition(x, y);
  }

  private void handleCollisions() {
    if (!mobCollides.isEmpty()) {
      world.getMobsGrid().callForEachCircle((int) x, (int) y, (int) (size / 2), this::collide);
    }

    if (!playerCollides.isEmpty()) {
      collide(world.getPlayer());
    }
  }

  protected void collide(Player e) {
    if (wasDeleted || e.WasDeleted() || alreadyHitPlayer
        || Util.distanceSquared(x - e.x, y - e.y) > Util.square(e.width + size) / 4) {
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
          .callForEachCircle((int) x, (int) y, (int) (size / 2), this::collide);
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
}
