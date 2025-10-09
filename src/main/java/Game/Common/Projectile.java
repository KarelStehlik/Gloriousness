package Game.Common;

import Game.Common.Buffs.Buff.Buff;
import Game.Common.Buffs.Buff.BuffHandler;
import Game.Common.Buffs.Modifier.Modifier;
import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Enums.TargetingOption;
import Game.Misc.*;
import Game.Mobs.TdMob;
import GlobalUse.Log;
import GlobalUse.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;

public class Projectile extends GameObject implements TickDetect {

  public Sprite getSprite() {
    return sprite;
  }

  protected final Sprite sprite;

  public List<OnCollideComponent<Player>> getPlayerCollides() {
    return playerCollides;
  }

  public List<OnCollideComponent<TdMob>> getMobCollides() {
    return mobCollides;
  }

  public List<OnCollideComponent<Projectile>> getProjectileCollides() {
    return projectileCollides;
  }

  protected final List<OnCollideComponent<Player>> playerCollides = new ArrayList<>(0);
  protected final HashSet<TdMob> alreadyHitMobs;
  protected final List<OnCollideComponent<TdMob>> mobCollides = new ArrayList<>(0);
  protected final HashSet<Projectile> alreadyHitProjectiles;
  protected final List<OnCollideComponent<Projectile>> projectileCollides = new ArrayList<>(0);
  protected final List<Modifier<? super Projectile>> beforeDeath = new ArrayList<>(0);
  protected final BuffHandler<Projectile> bh = new BuffHandler<>(this);
  public float vx;
  private float aspectRatio;
  public float vy;
  private boolean wasDeleted = false;
  protected boolean active = true;
  protected boolean alreadyHitPlayer = false;

  public boolean isMultihit() {
    return multihit;
  }

  public void setMultihit(boolean multihit) {
    this.multihit = multihit;
    if (multihit) {
      alreadyHitPlayer = false;
      alreadyHitProjectiles.clear();
      alreadyHitMobs.clear();
    }
  }

  private boolean multihit = false;

  public Projectile(TdWorld world, ImageData image, float X, float Y, float speed, float rotation,
                    int width, float aspectRatio, int pierce, float size, float duration, float power) {
    super(X, Y, (int) size, (int) size, world);
    this.aspectRatio = aspectRatio;
    sprite = new Sprite(image, 5, "basic").setPosition(X, Y).setSize(width, width * aspectRatio);
    sprite.setRotation(rotation - 90);
    world.getBs().addSprite(sprite);
    stats[Stats.pierce] = pierce;
    stats[Stats.speed] = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
    stats[Stats.size] = size;
    stats[Stats.duration] = duration*1000;
    this.rotation = rotation;
    alreadyHitMobs = new HashSet<>(Math.min(pierce, 500));
    alreadyHitProjectiles = new HashSet<>(Math.min(pierce, 500));
    stats[Stats.power] = power;
  }

  public static boolean bounce(Projectile p) {
    float s = p.stats[Stats.size] / 2;
    float minx = p.x - s;
    float miny = p.y - s;
    float maxx = p.x + s;
    float maxy = p.y + s;
    float x = p.getX();
    float y = p.getY();

    if (minx < 0) {
      x = p.x - 2 * minx;
      p.setRotation(180 - p.rotation);
    }
    if (miny < 0) {
      y = p.y - 2 * miny;
      p.setRotation(-p.rotation);
    }
    if (maxx > TdWorld.WIDTH) {
      x = 2 * TdWorld.WIDTH - maxx - s;
      p.setRotation(180 - p.rotation);
    }
    if (maxy > TdWorld.HEIGHT) {
      y = 2 * TdWorld.HEIGHT - maxy - s;
      p.setRotation(-p.rotation);
    }
    x = Util.clamp(x, minx, maxx);
    y = Util.clamp(y, miny, maxy);
    if (x != p.getX() || y != p.getY()) {
      p.move(x, y);
      return true;
    }
    return false;
  }

  public static class LimitedBounce implements Modifier<Projectile> {

    private int bounces;

    public LimitedBounce(int bounces) {
      this.bounces = bounces;
    }

    @Override
    public void mod(Projectile proj) {
      if (bounces > 0 && bounce(proj)) {
        bounces -= 1;
      }
    }
  }

  public void clearCollisions() {
    alreadyHitPlayer = false;
    alreadyHitProjectiles.clear();
    alreadyHitMobs.clear();
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

  public void changePierce(int amount) {
    addBuff(new StatBuff<Projectile>(Type.FINALLY_ADDED, Stats.pierce, amount));
    if (stats[Stats.pierce] <= 0) {
      for (var eff : beforeDeath) {
        eff.mod(this);
      }
      if (stats[Stats.duration] <= 0 || stats[Stats.pierce] <= 0) {
        delete();
      }
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

  @Override
  public float getRotation() {
    return rotation;
  }

  @Override
  public void setRotation(float f) {
    super.setRotation(f);
    sprite.setRotation(rotation - 90);
    vx = Util.cos(rotation) * stats[Stats.speed];
    vy = Util.sin(rotation) * stats[Stats.speed];
  }

  @Override
  public void move(float _x, float _y) {
    super.move(_x, _y);
    sprite.setPosition(x, y);
  }

  public void moveRelative(float addX, float addY) {
    super.move(x + addX, y + addY);
    sprite.setPosition(x, y);
  }

  public void setAspectRatio(float newval) {
    aspectRatio = newval;
  }

  public float getAspectRatio() {
    return aspectRatio;
  }

  @Override
  public void onStatsUpdate() {
    sprite.setSize(stats[Stats.size], stats[Stats.size] * aspectRatio);
    vx = Util.cos(rotation) * stats[Stats.speed];
    vy = Util.sin(rotation) * stats[Stats.speed];
  }
  @Override
  public void onGameTick(int tick) {
    if (!active) {
      return;
    }
    fly();
    world.getProjectilesGrid().add(this);
    addBuff(new StatBuff<Projectile>(Type.FINALLY_ADDED, Stats.duration, -Game.tickIntervalMillis));
    if (stats[Stats.duration] <= 0) {
      for (var eff : beforeDeath) {
        eff.mod(this);
      }
      if (stats[Stats.duration] <= 0 || stats[Stats.pierce] <= 0) {
        delete();
      }
    }
  }

  public void onGameTickP2() {
    bh.tick();
    handleCollisions();
    handleProjectileCollision();
  }

  @Override
  public void delete() {
    bh.delete();
    alreadyHitMobs.clear();
    alreadyHitProjectiles.clear();
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
    if (!active || wasDeleted || e.WasDeleted() || alreadyHitPlayer
        || Util.distanceSquared(x - e.getX(), y - e.getY()) > Util.square(e.height + stats[Stats.size]) / 4) {
      return;
    }
    boolean collided = false;
    alreadyHitPlayer = !multihit;
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

  public void addBeforeDeath(Modifier<? super Projectile> component) {
    beforeDeath.add(component);
  }

  protected void collide(TdMob e) {
    if (!active || wasDeleted || e.WasDeleted() || alreadyHitMobs.contains(e)) {
      return;
    }
    if (!multihit) {
      alreadyHitMobs.add(e);
    }
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

  public void addMobCollide(OnCollideComponent<TdMob> component, int index) {
    mobCollides.add(index, component);
  }

  public void handleProjectileCollision() {
    if (!projectileCollides.isEmpty()) {
      world.getProjectilesGrid()
          .callForEachCircle((int) x, (int) y, (int) (stats[Stats.size] / 2), this::collide);
    }
  }

  protected void collide(Projectile e) {
    if (!active || !e.active || e == this || alreadyHitProjectiles.contains(e)) {
      return;
    }
    if (!multihit) {
      alreadyHitProjectiles.add(e);
    }
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

  protected TdMob targetedMob;

  public static class Guided {

    public int getRange() {
      return range;
    }

    public void setRange(int range) {
      this.range = range;
    }

    public float getStrength() {
      return strength;
    }

    public void setStrength(float strength) {
      this.strength = strength;
    }

    private int range;
    private float strength;

    public Guided(int range, float strength) {
      this.range = range;
      this.strength = strength;
    }

    public void tick(Projectile target) {
      if (target.targetedMob == null || target.alreadyHitMobs.contains(target.targetedMob)
          || target.targetedMob.WasDeleted()) {
        target.targetedMob = null;
      }
      if (target.targetedMob == null) {
        target.targetedMob = target.world.getMobsGrid()
            .search(new Point((int) target.x, (int) target.y), range, TargetingOption.FIRST,
                mob -> !(target.alreadyHitMobs.contains(mob) || mob.WasDeleted()));
      }
      if (target.targetedMob == null) {
        return;
      }

      target.turnTowards(target.targetedMob.getX(), target.targetedMob.getY(),
          strength * target.getSpeed() * .1f);

    }
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
