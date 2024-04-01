package Game.Turrets;

import Game.Animation;
import Game.Buffs.Ignite;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.DamageType;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Projectile;
import Game.Projectile.Stats;
import Game.TickDetect;
import Game.TurretGenerator;
import Game.World;
import general.Log;
import general.RefFloat;
import general.Util;
import java.util.List;
import org.joml.Vector2f;
import windowStuff.Sprite;
import Game.BasicCollides;

public class Druid extends Turret {

  public static final String image = "ggg";

  public Druid(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Shockwave"));
    onStatsUpdate();
    bulletLauncher.setSpread(45);
    bulletLauncher.addProjectileModifier(this::modProjectile);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Druid", () -> new Druid(world, -1000, -1000));
  }

  private Upgrade up100() {
    return new Upgrade("Button", () -> "bounces off walls",
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addBuff(new OnTickBuff<Projectile>(Float.POSITIVE_INFINITY, Projectile::bounce)));
        }, 200);
  }

  private Upgrade up200() {
    return new Upgrade("Button", () -> "regrows 4 more times",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED,Float.POSITIVE_INFINITY,ExtraStats.respawns,4));
        }, 800);
  }

  private Upgrade up300() {
    return new Upgrade("Button", () -> "gains bonus duration when regrowing",
        () -> addBuff(new StatBuff<Turret>(Type.ADDED,Float.POSITIVE_INFINITY,ExtraStats.bonusDuration,6)), 1500);
  }

  private Upgrade up400() {
    return new Upgrade("Button", () -> "attacks way faster",
        () -> addBuff(new StatBuff<Turret>(Type.MORE,Float.POSITIVE_INFINITY,Stats.cd,0.3f)), 10000);
  }

  private Upgrade up500() {
    return new Upgrade("Button", () -> "keeps regrowing basically forever",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED,Float.POSITIVE_INFINITY,ExtraStats.respawns,20));
        }, 30000);
  }

  private Upgrade up010() {
    return new Upgrade("Button", () -> "increases pierce when regrowing",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED,Float.POSITIVE_INFINITY,ExtraStats.pierceScaling,.4f));
        }, 150);
  }

  private Upgrade up020() {
    return new Upgrade("Button", () -> "can hit the same bloon many times",
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addBuff(new OnTickBuff<Projectile>(Float.POSITIVE_INFINITY, Projectile::clearCollisions)));
        }, 500);
  }

  private Upgrade up030() {
    return new Upgrade("Button", () -> "Roots bloons",
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addMobCollide((proj,mob)->mob.addBuff(
              new StatBuff<TdMob>(Type.MORE, p.getPower()/ mob.getStats()[TdMob.Stats.health]*1000,TdMob.Stats.speed, 0.001f)
          ),0));
        }, 2500);
  }

  private Upgrade up040() {
    return new Upgrade("Button", () -> "slows everything in a large area",
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addMobCollide((proj,mob)->{
            world.getMobsGrid().callForEachCircle((int) mob.getX(), (int) mob.getY(),
                (int) (proj.getStats()[Projectile.Stats.size]*1.5f),
                enemy->enemy.addBuff(new StatBuff<TdMob>(
                    Type.MORE, proj.getPower()/ enemy.getStats()[TdMob.Stats.health]*10000,TdMob.Stats.speed,0.90f
                    )
                ));
            return true;
          },0));
        }, 20000);
  }

  private Upgrade up050() {
    return new Upgrade("Button", () -> "Bloons in a large area temporarily take more damage",
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addMobCollide((proj,mob)->{
            world.getMobsGrid().callForEachCircle((int) mob.getX(), (int) mob.getY(),
                (int) (proj.getStats()[Projectile.Stats.size]*1.5f),
                enemy->enemy.addBuff(new StatBuff<TdMob>(
                        Type.MORE, 500,TdMob.Stats.health,0.94f
                    )
                ));
            return true;
          },0));
        }, 50000);
  }

  private Upgrade up004() {
    return new Upgrade("Button", () -> "Also sets shit on fire",
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addMobCollide((proj,mob)->{
            world.getMobsGrid().callForEachCircle((int)mob.getX(), (int)mob.getY(),
                (int)(p.getStats()[Projectile.Stats.size]*.6f),
                enemy->enemy.addBuff(new Ignite<>(p.getPower()*0.1f,3000)));
            return true;
          }));
        }, 20000);
  }

  private Upgrade up005() {
    return new Upgrade("Button", () -> "Projectiles have more speed and piercs",
        () -> {
          bulletLauncher.addProjectileModifier(p->{
            p.addBuff(new StatBuff<Projectile>(Type.MORE,Float.POSITIVE_INFINITY,Projectile.Stats.speed, 7));
            p.addBuff(new StatBuff<Projectile>(Type.MORE,Float.POSITIVE_INFINITY,Projectile.Stats.pierce, 10));
          });
        }, 50000);
  }

  private Upgrade up003() {
    return new Upgrade("Button", () -> "Enemies hit explode",
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addMobCollide((proj,mob)->{
            BasicCollides.explodeFunc(
                (int) mob.getX(),
                (int) mob.getY(),
                proj.getPower(),
                proj.getStats()[Projectile.Stats.size]*.4f,
                "Explosion2-0");
            return true;
          }));
        }, 20000);
  }

  private Upgrade up002() {
    return new Upgrade("Button", () -> "increases power and size when regrowing",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED,Float.POSITIVE_INFINITY,ExtraStats.powScaling,.4f));
          addBuff(new StatBuff<Turret>(Type.ADDED,Float.POSITIVE_INFINITY,ExtraStats.sizeScaling,.15f));
        }, 2000);
  }

  private Upgrade up001() {
    return new Upgrade("Button", () -> "triple damage",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED,Float.POSITIVE_INFINITY,Stats.power,2f));
        }, 500);
  }

  @Override
  protected List<Upgrade> getUpgradePath1() {
    return List.of(up100(), up200(), up300(), up400(), up500());
  }

  @Override
  protected List<Upgrade> getUpgradePath2() {
    return List.of(up010(), up020(), up030(), up040(), up050());
  }

  @Override
  protected List<Upgrade> getUpgradePath3() {
    return List.of(up001(), up002(), up003(), up004(), up005());
  }

  private void regrow(RefFloat respawnsLeft, Projectile proj){
    if(respawnsLeft.get()<1){
      return;
    }
    respawnsLeft.add(-1);
    proj.setActive(false);
    proj.addBuff(
        new StatBuff<Projectile>(Type.MORE, Float.POSITIVE_INFINITY, Projectile.Stats.power,
            stats[ExtraStats.powScaling] ));
    proj.addBuff(
        new StatBuff<Projectile>(Type.INCREASED, Float.POSITIVE_INFINITY, Projectile.Stats.size,
            stats[ExtraStats.sizeScaling] ));
    proj.addBuff(
        new StatBuff<Projectile>(Type.MORE, Float.POSITIVE_INFINITY, Projectile.Stats.pierce,
            stats[ExtraStats.pierceScaling] ));
    proj.addBuff(
        new StatBuff<Projectile>(Type.ADDED, Float.POSITIVE_INFINITY, Projectile.Stats.pierce,
            stats[ExtraStats.bonusPierce]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.ADDED, Float.POSITIVE_INFINITY, Projectile.Stats.duration,
            stats[ExtraStats.bonusDuration]));
    proj.clearCollisions();
    Game.get().addTickable(new RespawningProjectile(proj));
  }

  private void modProjectile(Projectile p) {
    var respawnsLeft = new RefFloat(stats[ExtraStats.respawns]);
    p.addMobCollide(BasicCollides.damage);
    p.addBeforeDeath(proj->{
      regrow(respawnsLeft,proj);
    });
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 16;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 5f;
    stats[Stats.cd] = 2500f;
    stats[Stats.projectileDuration] = 8f;
    stats[Stats.bulletSize] = 220f;
    stats[Stats.speed] = 3.5f;
    stats[Stats.cost] = 200f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.respawns] = 2f;
    stats[ExtraStats.sizeScaling] = 0f;
    stats[ExtraStats.powScaling] = 1f;
    stats[ExtraStats.bonusPierce] = 5f;
    stats[ExtraStats.pierceScaling] = 1f;
    stats[ExtraStats.bonusDuration] = 0f;
  }

  public static final class ExtraStats {

    public static final int respawns = 10;
    public static final int sizeScaling = 11;
    public static final int powScaling = 12;
    public static final int bonusPierce = 13;
    public static final int pierceScaling = 14;
    public static final int bonusDuration = 15;

    private ExtraStats() {
    }
  }
  // end of generated stats

  class RespawningProjectile implements TickDetect {

    Animation sprite;
    Projectile p;

    RespawningProjectile(Projectile proj) {
      p = proj;
      float size = proj.getStats()[Projectile.Stats.size];

      this.sprite = new Animation(
          new Sprite("Shockwave", proj.getX(), proj.getY(), 0, 0, 3, "colorCycle2")
              .addToBs(Game.get().getSpriteBatching("main"))
              .setOpacity(0.0f)
              .setRotation(proj.getRotation() - 90).
              setColors(Util.getCycle2colors(1f)), 1
      ).setLinearScaling(new Vector2f(size * .015f, size * .015f)).setOpacityScaling(0.015f);
    }

    @Override
    public void onGameTick(int tick) {
      sprite.onGameTick(tick);
      if (sprite.WasDeleted()) {
        delete();
      }
    }

    @Override
    public void delete() {
      p.setActive(true);
    }

    @Override
    public boolean WasDeleted() {
      return sprite.WasDeleted();
    }
  }
}
