package Game;

import general.Log;
import general.Util;
import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import windowStuff.Sprite;

public class Projectile extends GameObject implements TickDetect {

  private final Sprite sprite;
  private final Collection<OnCollideComponent<Player>> playerCollides = new LinkedList<>();
  private final Collection<TdMob> alreadyHitMobs;
  private final Collection<OnCollideComponent<TdMob>> mobCollides = new LinkedList<>();
  private final Collection<Projectile> alreadyHitProjectiles;
  private final Collection<OnCollideComponent<Projectile>> projectileCollides = new LinkedList<>();
  protected int pierce;
  protected float size;
  private float speed;
  private float vx, vy;
  private float duration;
  private boolean wasDeleted = false;
  private float rotation;
  private boolean alreadyHitPlayer = false;
  private float power;

  protected Projectile(World world, String image, float X, float Y, float speed, float rotation,
      int W, int H, int pierce, float size, float duration, float power) {
    super(X, Y, (int) size, (int) size, world);
    sprite = new Sprite(image, X, Y, W, H, 1, "basic");
    sprite.setRotation(rotation);
    world.getBs().addSprite(sprite);
    this.pierce = pierce;
    this.speed = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
    this.size = size;
    this.duration = duration*1024;
    this.rotation = rotation;
    alreadyHitMobs = new HashSet<>(pierce);
    alreadyHitProjectiles = new HashSet<>(pierce);
    this.power = power;
  }

  public void multiplyPower(float mult) {
    power *= mult;
  }

  public float getPower() {
    return power;
  }

  protected void changePierce(int amount) {
    pierce += amount;
    if (pierce < 0) {
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
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
  }

  @Override
  public void onGameTick(int tick) {
    fly();
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
    wasDeleted = true;
    sprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return wasDeleted;
  }

  private void fly() {
    move(x + vx, y + vy);
    handleCollisions();
    world.getProjectilesGrid().add(this);
    sprite.setPosition(x, y);
  }

  private void handleCollisions() {
    if (!mobCollides.isEmpty()) {
      world.getMobsGrid().callForEachCircle((int) x, (int) y, (int) (size/2), this::collide);
    }
    if (!projectileCollides.isEmpty()) {
      world.getProjectilesGrid().callForEachCircle((int) x, (int) y, (int) (size/2), this::collide);
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
    alreadyHitPlayer = true;
    for (var component : playerCollides) {
      component.collide(this, e);
    }
    changePierce(-1);
  }

  public void addPlayerCollide(OnCollideComponent<Player> component) {
    playerCollides.add(component);
  }

  protected void collide(TdMob e) {
    if (wasDeleted || e.WasDeleted() || alreadyHitMobs.contains(e)) {
      return;
    }
    alreadyHitMobs.add(e);
    for (var component : mobCollides) {
      component.collide(this, e);
    }
    changePierce(-1);
  }

  public void addMobCollide(OnCollideComponent<TdMob> component) {
    mobCollides.add(component);
  }

  protected void collide(Projectile e) {
    if (wasDeleted || e.WasDeleted() || e.equals(this) || alreadyHitProjectiles.contains(e)) {
      return;
    }
    alreadyHitProjectiles.add(e);
    for (var component : projectileCollides) {
      component.collide(this, e);
    }
    changePierce(-1);
  }

  public void addProjectileCollide(OnCollideComponent<Projectile> component) {
    projectileCollides.add(component);
  }

  private void onDelete() {

  }

  @FunctionalInterface
  protected interface OnCollideComponent<T extends GameObject> {

    void collide(Projectile proj, T target);
  }
}
