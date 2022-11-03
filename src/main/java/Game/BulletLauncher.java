package Game;

import Game.Projectile.OnCollideComponent;
import general.Util;
import java.util.Collection;
import java.util.LinkedList;

public class BulletLauncher {


  private final Collection<OnCollideComponent<Player>> playerCollides = new LinkedList<>();
  private final Collection<OnCollideComponent<Mob>> mobCollides = new LinkedList<>();
  private final Collection<OnCollideComponent<Projectile>> projectileCollides = new LinkedList<>();
  private final World world;
  private float x, y;
  private final String image;
  private final float speed;
  private final int width;
  private final int height;
  private final int pierce;
  private final int size;
  private final int power;
  private final float duration;
  public BulletLauncher(World world, String projectileImage, float x, float y,
      float projectileSpeed,
      int projectileSpriteWidth, int projectileSpriteHeight, int pierce, int size, float duration,
      int power) {
    this.world = world;
    this.image = projectileImage;
    this.speed = projectileSpeed;
    this.width = projectileSpriteWidth;
    this.height = projectileSpriteHeight;
    this.pierce = pierce;
    this.size = size;
    this.power = power;
    this.duration = duration;
    this.x = x;
    this.y = y;
  }

  public void addPlayerCollide(OnCollideComponent<Player> component) {
    playerCollides.add(component);
  }

  public void addMobCollide(OnCollideComponent<Mob> component) {
    mobCollides.add(component);
  }

  public void addProjectileCollide(OnCollideComponent<Projectile> component) {
    projectileCollides.add(component);
  }

  public void move(float newX, float newY) {
    x = newX;
    y = newY;
  }

  public void attack(float angle) {
    Projectile p = new Projectile(world, image, x, y, speed, angle, width, height, pierce, size,
        duration);
    world.getProjectilesList().add(p);
    for (var collide : playerCollides) {
      p.addPlayerCollide(collide);
    }
    for (var collide : mobCollides) {
      p.addMobCollide(collide);
    }
    for (var collide : projectileCollides) {
      p.addProjectileCollide(collide);
    }
  }

  public void attack(float targetX, float targetY) {
    attack(Util.get_rotation(targetX - x, targetY - y));
  }
}
