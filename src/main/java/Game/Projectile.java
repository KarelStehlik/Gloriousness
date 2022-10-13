package Game;

import general.Util;
import windowStuff.Sprite;

public class Projectile extends GameObject implements TickDetect {

  private final Sprite sprite;
  protected int pierce;
  protected float size;
  protected boolean collidesWithEnemies = false, collidesWithPlayer = false, collidesWithProjectiles = false;
  private float speed;
  private float vx, vy;
  private float duration;
  private boolean wasDeleted = false;
  private float rotation;

  protected Projectile(World world, String image, float X, float Y, float speed, float rotation,
      int W, int H, int pierce, float size, float duration,
      boolean enemies, boolean players, boolean projectiles) {
    super(X, Y, W, H, world);
    world.getProjectilesList().add(this);
    collidesWithEnemies = enemies;
    collidesWithPlayer = players;
    collidesWithProjectiles = projectiles;
    sprite = new Sprite(image, X, Y, W, H, 1, "basic");
    sprite.setRotation(rotation);
    world.getBs().addSprite(sprite);
    this.pierce = pierce;
    this.speed = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
    this.size = size;
    this.duration = duration;
    this.rotation = rotation;
  }

  public void changePierce(int amount) {
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
    duration -= Game.tickInterval / 1024f;
    if (duration <= 0) {
      delete();
    }
  }

  private void fly() {
    move(x + vx, y + vy);
    handleCollisions();
    world.getProjectilesGrid().add(this);
    sprite.setPosition(x, y);
  }

  private void handleCollisions() {
    if (collidesWithEnemies) {
      world.getMobsGrid().callForEach(getHitbox(), this::collide);
    }
    if (collidesWithProjectiles) {
      world.getProjectilesGrid().callForEach(getHitbox(), this::collide);
    }
    if (collidesWithPlayer) {
      collide(world.getPlayer());
    }
  }

  protected void collide(Player e) {

  }

  protected void collide(Mob e) {

  }

  protected void collide(Projectile e) {

  }

  private void onDelete() {

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
}
