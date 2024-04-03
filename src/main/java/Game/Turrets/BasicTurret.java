package Game.Turrets;

import Game.Ability;
import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.UniqueBuff;
import Game.BulletLauncher;
import Game.Game;
import Game.Projectile;
import Game.Projectile.Guided;
import Game.TurretGenerator;
import Game.World;
import general.Util;
import windowStuff.Sprite;

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

  @Override
  protected Upgrade up010() {
    return new Upgrade("Meteor", () -> "beefy darts",
        () -> {
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.bulletSize, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, .8f));
        }, 1000);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("Meteor", () -> "stronger arms grant faster, stronger darts and more range",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.range, 10));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, 2));
        }, 5000);
  }

  @Override
  protected Upgrade up040() {
    return new Upgrade("Meteor", () -> "Beefier darts",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 10));
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.bulletSize, 10f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 100f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 100f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, 0.3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 2f));
        }, 20000);
  }

  @Override
  protected Upgrade up050() {
    return new Upgrade("Meteor", () -> "adds a cool new ability",
        () -> {
          var a = new Ability("Dart", 30000,
              () -> "Dart monkey immediately throws the beefiest dart ever", () -> {
            addBuff(new StatBuff<Turret>(Type.MORE, 5000, Stats.bulletSize, 5f));
            addBuff(new StatBuff<Turret>(Type.MORE, 5000, Stats.pierce, 1048576));
            addBuff(new StatBuff<Turret>(Type.MORE, 5000, Stats.power, 200));
            addBuff(new StatBuff<Turret>(Type.MORE, 5000, Stats.cd, 0.5f));
            bulletLauncher.setRemainingCooldown(0);
            onStatsUpdate();
          });
          addBuff(new OnTickBuff<Turret>(t -> a.onGameTick(Game.get().getTicks())));
          addBuff(new DelayedTrigger<Turret>(t -> a.delete(), true));
        }, 20000);
  }

  private static final long dartEatId = Util.getUid();

  @Override
  protected Upgrade up020() {
    return new Upgrade("Meteor", () -> "darts combine to become stronger",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            Sprite s = new Sprite("DruidBall", 2).
                setSize(100, 100).
                setPosition(p.getX(), p.getY()).
                addToBs(world.getBs()).
                setOpacity(0.1f);
            p.addBuff(new UniqueBuff<>(dartEatId, t -> {
            }));
            p.addBuff(new DelayedTrigger<Projectile>(proj -> s.delete(), true));
            p.addBuff(new OnTickBuff<Projectile>(proj -> s.setPosition(proj.getX(), proj.getY())));
            p.addProjectileCollide((p1, p2) -> {
              if (!p2.addBuff(UniqueBuff.Test(dartEatId)) && p2.getPower() <= p1.getPower()) {
                s.setOpacity(s.getOpacity() + 0.005f * p2.getPower());
                p1.addBuff(new StatBuff<Projectile>(Type.FINALLY_ADDED, Projectile.Stats.power,
                    p2.getPower() * 2));
                p1.addBuff(new StatBuff<Projectile>(Type.FINALLY_ADDED, Projectile.Stats.pierce,
                    p2.getStats()[Projectile.Stats.pierce]));
                if (!p2.addBuff(UniqueBuff.Test(EatingTurret.EatImmuneTag))) {
                  p1.addBuff(new UniqueBuff<>(EatingTurret.EatImmuneTag,
                      ppp -> ppp.addBuff(new OnTickBuff<Projectile>(Projectile::bounce))));
                }
                p2.delete();
              }
              return false;
            });
          });
        }, 1000);
  }

  private final Guided g = new Projectile.Guided(1000, 7);

  @Override
  protected Upgrade up100() {
    return new Upgrade("Meteor",
        () -> "projectiles seek, last slightly longer and have infinite pierce",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.pierce, Float.POSITIVE_INFINITY));
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.projectileDuration, 5000));
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new OnTickBuff<Projectile>(g::tick));
          });
        }, 1000);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("Meteor", () -> "projectiles have double duration and are faster",
        () -> {
          g.setStrength(15);
          g.setRange(1500);
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 2));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, 2));
        }, 10000);
  }

  @Override
  protected Upgrade up400() {
    return new Upgrade("Meteor", () -> "more damage",
        () -> {
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.power, 5));
        }, 10000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("Meteor",
        () -> "projectiles last literally forever (i'm sure this isn't game breaking)",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.projectileDuration,
              Float.POSITIVE_INFINITY));
        }, 1000000);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Meteor", () -> "darts can hit the same enemy multiple times",
        () -> {
          bulletLauncher.addProjectileModifier(p -> p.setMultihit(true));
        }, 1000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Meteor", () -> "shoots 2x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1 / 2f)), 200);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Meteor", () -> "shoots 3x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1 / 3f)), 1000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("Meteor", () -> "shoots 4x faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1 / 4f)), 5000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("Meteor", () -> "shoots 5x faster.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1 / 5f));
          bulletLauncher.setSpread(10);
        }, 20000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("Meteor", () -> "shoots 10x faster.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 1 / 10f));
          bulletLauncher.setSpread(30);
        }, 100000);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 350f;
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
