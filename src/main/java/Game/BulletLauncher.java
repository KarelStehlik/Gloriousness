package Game;

import Game.Buffs.Modifier;
import Game.Mobs.TdMob;
import Game.Projectile.OnCollideComponent;
import general.Data;
import general.Util;
import java.util.ArrayList;
import java.util.Collection;

public class BulletLauncher {


  private final Collection<OnCollideComponent<Player>> playerCollides = new ArrayList<>(1);
  private final Collection<OnCollideComponent<TdMob>> mobCollides = new ArrayList<>(1);
  private final Collection<OnCollideComponent<Projectile>> projectileCollides = new ArrayList<>(1);
  private final World world;
  private String image;
  private float speed;
  private int width;
  private int height;
  private int pierce;
  private float size;
  private float power;
  private float duration;
  private float x, y;
  private float cooldown;
  private float spread = 0;
  private float remainingCooldown;
  private Modifier<Projectile> projectileModifier = p -> {
  };

  public BulletLauncher(World world, String projectileImage, float x, float y,
      float projectileSpeed,
      int projectileSpriteWidth, int projectileSpriteHeight, int pierce, int size, float duration,
      int power, float cooldownMs) {
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
    this.cooldown = cooldownMs;
    this.remainingCooldown = cooldownMs;
  }

  public BulletLauncher(World world, String projectileImage) {
    this(world, projectileImage, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  }

  public BulletLauncher(BulletLauncher og) {
    world = og.world;
    image = og.image;
    speed = og.speed;
    width = og.width;
    height = og.height;
    pierce = og.pierce;
    size = og.size;
    power = og.power;
    duration = og.duration;
    spread = og.spread;
    x = og.x;
    y = og.y;
    cooldown = og.cooldown;
    playerCollides.addAll(og.playerCollides);
    mobCollides.addAll(og.mobCollides);
    projectileCollides.addAll(og.projectileCollides);
  }

  public void setProjectileModifier(Modifier<Projectile> projectileModifier) {
    this.projectileModifier = projectileModifier;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public float getSpread() {
    return spread;
  }

  public void setSpread(float spread) {
    this.spread = spread;
  }

  public void tickCooldown() {
    if (remainingCooldown > 0) {
      remainingCooldown -= Game.tickIntervalMillis;
    }
  }

  public boolean canAttack() {
    return remainingCooldown <= 0;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public void setCooldown(float cooldown) {
    this.cooldown = cooldown;
  }

  public void setPierce(int pierce) {
    this.pierce = pierce;
  }

  public void setSize(float size) {
    width = (int) size;
    height = (int) size;
    this.size = size;
  }

  public void setPower(float power) {
    this.power = power;
  }

  public void setDuration(float duration) {
    this.duration = duration;
  }

  public void addPlayerCollide(OnCollideComponent<Player> component) {
    playerCollides.add(component);
  }

  public void addMobCollide(OnCollideComponent<TdMob> component) {
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
    float deviation = (Data.gameMechanicsRng.nextFloat() - .5f) * spread;
    Projectile p = new Projectile(world, image, x, y, speed, angle + deviation, width, height,
        pierce, size,
        duration, power);
    projectileModifier.mod(p);
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
    remainingCooldown += cooldown;
  }

  public void attack(float targetX, float targetY) {
    attack(Util.get_rotation(targetX - x, targetY - y));
  }
}
