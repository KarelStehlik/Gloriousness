package Game.Common;

import Game.Common.Buffs.Modifier.Modifier;
import Game.Misc.Game;
import Game.Misc.Player;
import Game.Misc.TdWorld;
import Game.Mobs.TdMob;
import Game.Common.Projectile.OnCollideComponent;
import Game.Common.Turrets.Turret;
import GlobalUse.Data;
import GlobalUse.Util;
import java.util.ArrayList;
import java.util.List;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;

public class BulletLauncher {

  private final List<OnCollideComponent<Player>> playerCollides = new ArrayList<>(1);
  private final List<OnCollideComponent<TdMob>> mobCollides = new ArrayList<>(1);
  private final List<OnCollideComponent<Projectile>> projectileCollides = new ArrayList<>(1);
  private final TdWorld world;
  private final List<Modifier<Projectile>> projectileModifiers = new ArrayList<>(0);
  private final List<Modifier<BulletLauncher>> attackEffects = new ArrayList<>(0);
  private ImageData image;
  private float speed;
  private float aspectRatio = 1; //
  public ArrayList<Cannon> cannons = new ArrayList<>(1);

  public static class Cannon {

    public final float xOffset, yOffset, angle;

    public Cannon(float xOffset, float yOffset) {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.angle = 0;
    }

    public Cannon(float xOffset, float yOffset, float angle) {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.angle = angle;
    }
  }

  public static ArrayList<Cannon> radial(int number) {
    var re = new ArrayList<Cannon>(number);
    for (int i = 0; i < number; i++) {
      re.add(new BulletLauncher.Cannon(0, 0, 360f * i / number));
    }
    return re;
  }

  public void updateStats(float[] stats) {
    setDuration(stats[Turret.Stats.projectileDuration]);
    setPierce((int) stats[Turret.Stats.pierce]);
    setPower(stats[Turret.Stats.power]);
    setSize(stats[Turret.Stats.bulletSize]);
    setSpeed(stats[Turret.Stats.speed]);
    setCooldown(1000f / stats[Turret.Stats.aspd]);
  }

  public ImageData getImage() {
    return image;
  }

  public int getWidth() {
    return width;
  }

  protected void setWidth(int width) {
    this.width = width;
  }


  public void setAspectRatio(float newval) {
    aspectRatio = newval;
  }

  private int width;
  private int pierce;
  private float size;
  private float power;
  private float duration;

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  private float x, y;

  public float getCooldown() {
    return cooldown;
  }

  private float cooldown;
  private float spread = 0;

  public float getRemainingCooldown() {
    return remainingCooldown;
  }

  public void setRemainingCooldown(float remainingCooldown) {
    this.remainingCooldown = remainingCooldown;
  }

  private float remainingCooldown;

  public BulletLauncher(TdWorld world, String projectileImage, float x, float y,
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
    cannons.add(new Cannon(0, 0));
    aspectRatio =
        (image.textureCoordinates[1] - image.textureCoordinates[3]) / (image.textureCoordinates[2]
            - image.textureCoordinates[0]);
  }

  public BulletLauncher(TdWorld world, String projectileImage) {
    this(world, projectileImage, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  }

  public BulletLauncher(TdWorld world, String projectileImage, ProjectileNewFunction f) {
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
    remainingCooldown=og.getRemainingCooldown();
    aspectRatio=og.aspectRatio;
    x = og.x;
    attackEffects.addAll(og.attackEffects);
    y = og.y;
    cooldown = og.cooldown;
    playerCollides.addAll(og.playerCollides);
    mobCollides.addAll(og.mobCollides);
    projectileCollides.addAll(og.projectileCollides);
    projectileModifiers.addAll(og.projectileModifiers);
    launcher = og.launcher;
    cannons = new ArrayList<>(og.cannons);
  }

  public void addProjectileModifier(Modifier<Projectile> projectileModifier) {
    projectileModifiers.add(projectileModifier);
  }
  public void addAttackEffect(Modifier<BulletLauncher> projectileModifier) {
    attackEffects.add(projectileModifier);
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
    width = (int) size;
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

  public void removeMobCollide(OnCollideComponent<TdMob> component) {
    mobCollides.remove(component);
  }

  public void addProjectileCollide(OnCollideComponent<Projectile> component) {
    projectileCollides.add(component);
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

    Projectile make(TdWorld world, ImageData image, float X, float Y, float speed, float rotation,
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

  private Projectile fire(float xOffset, float yOffset, float angle) {
    float deviation = (Data.gameMechanicsRng.nextFloat() - .5f) * spread;
    Projectile p = launcher.make(world, image, x + xOffset, y + yOffset, speed,
        angle + deviation, width, aspectRatio, pierce, size, duration, power);

    for (var pm : projectileModifiers) {
      pm.mod(p);
    }
    world.getProjectilesList().add(p);
    p.getPlayerCollides().addAll(playerCollides);
    p.getMobCollides().addAll(mobCollides);
    p.getProjectileCollides().addAll(projectileCollides);
    return p;
  }

  public Projectile attack(float angle, boolean triggerCooldown) {

    if (triggerCooldown) {
      for(Modifier<BulletLauncher> mod: attackEffects){
        mod.mod(this);
      }
      remainingCooldown += cooldown;
    }

    Projectile p = null;

    for (var ac : cannons) {
      float sin = (float) Math.sin(2 * Math.PI * angle / 360);
      float cos = (float) Math.cos(2 * Math.PI * angle / 360);
      float displaceX = ac.xOffset * sin + ac.yOffset * cos;
      float displaceY = ac.yOffset * sin - ac.xOffset * cos;
      var newProj = fire(displaceX, displaceY, angle + ac.angle);
      if (p == null) {
        p = newProj;
      }
    }

    return p;
  }

  public Projectile attack(float targetX, float targetY, boolean triggerCooldown) {

    return attack(Util.get_rotation(targetX - x, targetY - y),triggerCooldown);
  }
}
