package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Projectile;
import Game.Projectile.Guided;
import Game.Projectile.Stats;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import java.util.List;

public class BasicTurret extends Turret {

  public static final String image = "BasicTower";

  public BasicTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Dart"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Basic", () -> new BasicTurret(world, -1000, -1000));
  }

  private Guided g = new Projectile.Guided(500, 7);
  @Override
  protected Upgrade up100() {
    return new Upgrade("Meteor", () -> "projectiles seek, last slightly longer and have infinite pierce",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new OnTickBuff<Projectile>(g::tick));
            p.addBuff(new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.pierce, Float.POSITIVE_INFINITY));
            p.addBuff(new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.duration, 5000));
          });
        }, 1000);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("Meteor", () -> "projectiles have double duration and are faster",
        () -> {
          g.setStrength(15);
          g.setRange(1500);
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.duration, 2));
            p.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.speed, 2));

          });
        }, 10000);
  }

  @Override
  protected Upgrade up400() {
    return new Upgrade("Meteor", () -> "projectiles are bigger and do more damage",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new StatBuff<Projectile>(Type.INCREASED, Projectile.Stats.size, 1.5f));
            p.addBuff(new StatBuff<Projectile>(Type.INCREASED, Projectile.Stats.power, 5));
          });
        }, 10000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("Meteor", () -> "projectiles last literally forever (i'm sure this isn't game breaking)",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.duration, Float.POSITIVE_INFINITY));
          });
        }, 1000000);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Meteor", () -> "Every second, projectiles forget what they've already hit",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            RefFloat timer = new RefFloat(0);
            p.addBuff(new OnTickBuff<Projectile>(proj->{
              timer.add(1);
              if(timer.get() >= 60){
                proj.clearCollisions();
                timer.set(0);
              }
            }));
          });
        }, 1000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Meteor", () -> "shoots 2x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1/2f)), 200);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Meteor", () -> "shoots 3x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1/3f)), 1000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("Meteor", () -> "shoots 4x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1/4f)), 5000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("Meteor", () -> "shoots 5x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1/5f)), 20000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("Meteor", () -> "shoots 10x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1/10f)), 100000);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 2f;
    stats[Stats.cd] = 1400f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = 15f;
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
