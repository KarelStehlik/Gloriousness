package Game.Turrets;

import Game.Ability;
import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.Tag;
import Game.BulletLauncher;
import Game.Projectile;
import Game.Projectile.Guided;
import Game.TurretGenerator;
import Game.World;
import general.*;

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
    return new Upgrade("Dart", new Description("beefy darts"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.bulletSize, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, .8f));
        }, 1000);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("FastDart",
            new Description("stronger arms grant faster, stronger darts and more range"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.range, 10));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 20f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, 2));
        }, 25000);
  }

  @Override
  protected Upgrade up040() {
    return new Upgrade("BeefyDart",  new Description("Beefier darts"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 0.1f));
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.bulletSize, 10f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 100f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 150f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, 0.3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 2f));
        }, 64000);
  }

  private static final long bigDart = Util.getUid();

  @Override
  protected Upgrade up050() {
    return new Upgrade("BeefierDart",  new Description( "adds a cool new ability"),
        () -> {
          var a = Ability.add("Dart", 30000,
              () -> "Dart monkey immediately throws the beefiest dart ever", () -> {
                addBuff(new StatBuff<Turret>(Type.MORE, 1, Stats.bulletSize, 5f));
                addBuff(new StatBuff<Turret>(Type.MORE, 1, Stats.pierce, 1048576));
                addBuff(new StatBuff<Turret>(Type.MORE, 1, Stats.power, 200));
                bulletLauncher.attack(Util.get_rotation(Constants.screenSize.x / 2f - x,
                        Constants.screenSize.y / 2f - y), false).
                    addBuff(new Tag<Projectile>(EatingTurret.EatImmuneTag, p -> {
                    }));
                onStatsUpdate();
              }, bigDart);
          addBuff(new DelayedTrigger<Turret>(t -> {a.delete();
            Log.write("dd");}, true));
        }, 200000);
  }

  private static final long dartEatId = Util.getUid();

  @Override
  protected Upgrade up300() {
    return new Upgrade("MagnetDart",  new Description( "darts combine to become stronger"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 3f));
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new Tag<Projectile>(dartEatId, t -> {
            }));
            p.addProjectileCollide((p1, p2) -> {
              if (!p2.addBuff(Tag.Test(dartEatId)) && p2.getStats()[Projectile.Stats.duration]
                  <= p1.getStats()[Projectile.Stats.duration]) {
                float buffDur = p2.getStats()[Projectile.Stats.duration];

                float otherPosWeight = p2.getPower() / (p1.getPower() + p2.getPower());
                p1.move(p1.getX() * (1 - otherPosWeight) + p2.getX() * otherPosWeight,
                    p1.getY() * (1 - otherPosWeight) + p2.getY() * otherPosWeight);

                p1.addBuff(
                    new StatBuff<Projectile>(Type.FINALLY_ADDED, buffDur, Projectile.Stats.power,
                        p2.getPower()));
                p1.addBuff(
                    new StatBuff<Projectile>(Type.FINALLY_ADDED, buffDur, Projectile.Stats.pierce,
                        p2.getStats()[Projectile.Stats.pierce]));
                p1.addBuff(
                    new StatBuff<Projectile>(Type.FINALLY_ADDED, buffDur, Projectile.Stats.size,
                        p2.getStats()[Projectile.Stats.size] * .3f));

                if (!p2.addBuff(Tag.Test(EatingTurret.EatImmuneTag))) {
                  p1.addBuff(new Tag<Projectile>(EatingTurret.EatImmuneTag, ppp -> {
                  }));
                }
                p2.delete();
              }
              return false;
            });
          });
        }, 4000);
  }

  private final Guided g = new Projectile.Guided(1000, 3);

  @Override
  protected Upgrade up100() {
    return new Upgrade("Radar",
         new Description( "projectiles seek and have infinite pierce, but last less long"),
        () -> {
          bulletLauncher.setImage("Laser");
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.pierce, Float.POSITIVE_INFINITY));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, .4f));
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new OnTickBuff<Projectile>(g::tick));
          });
        }, 350);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("MoreRadar",  new Description( "projectiles have double duration and are faster"),
        () -> {
          g.setRange(1500);
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 2));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, 2));
        }, 800);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Meteor",  new Description( "3x more damage"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.power, 3));
        }, 5000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("InfiniDart",
         new Description( "projectiles last literally forever (i'm sure this isn't game breaking)"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.projectileDuration,
              Float.POSITIVE_INFINITY));
        }, 1000000);
  }

  @Override
  protected Upgrade up400() {
    return new Upgrade("Goldfish",  new Description( "darts can hit the same enemy multiple times"),
        () -> {
          bulletLauncher.addProjectileModifier(p -> p.setMultihit(true));
        }, 10000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("DoubleDart",  new Description( "shoots 2x faster."),
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 2f)), 100);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("TripleDart",  new Description( "shoots 3x faster."),
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 3f)), 1000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("QuadDart",  new Description( "shoots 4x faster, with full map range"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 4f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.range, 4f));
        }, 8000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("QuinDart",  new Description( "shoots 5x faster."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 5f));
          bulletLauncher.setSpread(10);
        }, 40000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("TenDart",  new Description( "shoots 5x faster with double damage."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 5f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 2f));
          bulletLauncher.setSpread(30);
        }, 150000);
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 20;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = Data.gameMechanicsRng.nextFloat(50f,500f);
    stats[Stats.pierce] = 2f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(1.1f,10f);
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(5f,100f);
    stats[Stats.speed] = 15f;
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.power] = 0f;
    stats[ExtraStats.range] = Data.gameMechanicsRng.nextFloat(5000f,50000f);
    stats[ExtraStats.pierce] = 2f;
    stats[ExtraStats.aspd] = Data.gameMechanicsRng.nextFloat(10f,30f);
    stats[ExtraStats.projectileDuration] = 2f;
    stats[ExtraStats.bulletSize] = Data.gameMechanicsRng.nextFloat(5f,100f);
    stats[ExtraStats.speed] = 55f;
    stats[ExtraStats.cost] = 100f;
    stats[ExtraStats.size] = 50f;
    stats[ExtraStats.spritesize] = 150f;
  }

  public static final class ExtraStats {

    public static final int power = 10;
    public static final int range = 11;
    public static final int pierce = 12;
    public static final int aspd = 13;
    public static final int projectileDuration = 14;
    public static final int bulletSize = 15;
    public static final int speed = 16;
    public static final int cost = 17;
    public static final int size = 18;
    public static final int spritesize = 19;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
