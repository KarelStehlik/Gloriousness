package Game;

import Game.Buffs.Modifier;
import Game.Buffs.AttackEffect;
import Game.Mobs.TdMob;
import Game.Projectile.OnCollideComponent;
import general.Data;
import general.Util;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Graphics;
import windowStuff.ImageData;

public class BulletLauncher {
  private final List<AttackEffect> onAttackEffects = new ArrayList<>(1);
  private final List<OnCollideComponent<Player>> playerCollides = new ArrayList<>(1);
  private final List<OnCollideComponent<TdMob>> mobCollides = new ArrayList<>(1);
  private final List<OnCollideComponent<Projectile>> projectileCollides = new ArrayList<>(1);
  private final World world;
  private final List<Modifier<Projectile>> projectileModifiers = new ArrayList<>(0);
  private ImageData image;
  private float speed;
  private float aspectRatio=1; //
  public int radial = 1;

  public ImageData getImage() {
    return image;
  }

  public int getWidth() {
    return width;
  }

  protected void setWidth(int width) {
    this.width = width;
  }



  public void setAspectRatio(float newval){
    aspectRatio=newval;
  }

  private int width;
  private int pierce;
  private float size;
  private float power;
  private float duration;
  private float x, y;
  private float cooldown;
  private float spread = 0;

  public float getRemainingCooldown() {
    return remainingCooldown;
  }

  public void setRemainingCooldown(float remainingCooldown) {
    this.remainingCooldown = remainingCooldown;
  }

  private float remainingCooldown;

  public BulletLauncher(World world, String projectileImage, float x, float y,
      float projectileSpeed,
      int projectileSpriteWidth, int projectileSpriteHeight, int pierce, int size, float duration,
      int power, float cooldownMs) {
    this.world = world;
    this.image = Graphics.getImage(projectileImage);
    this.speed = projectileSpeed;
    this.width = projectileSpriteWidth;
    this.pierce = pierce;
    this.size = size;
    this.power = power;
    this.duration = duration;
    this.x = x;
    this.y = y;
    this.cooldown = cooldownMs;
    this.remainingCooldown = cooldownMs;
    launcher = Projectile::new;
  }

  public BulletLauncher(World world, String projectileImage) {
    this(world, projectileImage, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  }

  public BulletLauncher(World world, String projectileImage, ProjectileNewFunction f) {
    this(world, projectileImage);
    launcher = f;
  }

  public BulletLauncher(BulletLauncher og) {
    world = og.world;
    image = og.image;
    speed = og.speed;
    width = og.width;
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
    projectileModifiers.addAll(og.projectileModifiers);
    launcher = og.launcher;
  }

  public void addProjectileModifier(Modifier<Projectile> projectileModifier) {
    projectileModifiers.add(projectileModifier);
  }

  public void setImage(ImageData image) {
    this.image = image;
  }

  public void setImage(String image) {
    this.image = Graphics.getImage(image);
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
    this.size = size;
  }
  public void scale(float sizeMult) {
    this.size *= sizeMult;
    width=(int)size;
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

  public void addMobCollide(OnCollideComponent<TdMob> component, int index) {
    mobCollides.add(index, component);
  }

  public void addProjectileCollide(OnCollideComponent<Projectile> component) {
    projectileCollides.add(component);
  }
  public void addAttackEffect(AttackEffect component) {
    onAttackEffects.add(component);
  }
  public void removeAttackEffect(AttackEffect component) {
    onAttackEffects.remove(component);
  }
  public void removeProjectileModifier(Modifier<Projectile> projectileModifier) {
    projectileModifiers.remove(projectileModifier);
  }
  public void move(float newX, float newY) {
    x = newX;
    y = newY;
  }

  @FunctionalInterface
  public interface ProjectileNewFunction {

    Projectile make(World world, ImageData image, float X, float Y, float speed, float rotation,
        int W, float AR, int pierce, float size, float duration, float power);
  }

  public ProjectileNewFunction getLauncher() {
    return launcher;
  }

  public void setLauncher(ProjectileNewFunction launcher) {
    this.launcher = launcher;
  }

  private ProjectileNewFunction launcher;

  public Projectile attack(float angle) {
    return attack(angle, true);
  }

  public Projectile attack(float angle, boolean triggerCooldown) {
    for (AttackEffect effect:onAttackEffects){
      effect.mod(this,triggerCooldown,angle);
    }
    if (triggerCooldown) {
      remainingCooldown += cooldown;
    }
    float deviation = (Data.gameMechanicsRng.nextFloat() - .5f) * spread;

    for (int i = 0; i < radial; i++) {
      Projectile p = launcher.make(world, image, x, y, speed, angle + 360f * i / radial + deviation,
          width, aspectRatio,
          pierce, size,
          duration, power);

      for (var pm : projectileModifiers) {
        pm.mod(p);
      }
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
      if (i == radial - 1) {
        return p;
      }
    }
    return null;
  }

  public Projectile attack(float targetX, float targetY) {
    return attack(Util.get_rotation(targetX - x, targetY - y));
  }
}
