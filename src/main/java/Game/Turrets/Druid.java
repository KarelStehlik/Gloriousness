package Game.Turrets;

import Game.Ability;
import Game.BasicCollides;
import Game.Buffs.Ignite;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Mobs.TdMob;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import general.Util;

public class Druid extends Turret {

  public static final String image = "Druid";
  private final String ballImage = "DruidBall";

  public Druid(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "", DruidBall::new));
    bulletLauncher.setImage(ballImage);
    onStatsUpdate();
    bulletLauncher.addProjectileModifier(this::modProjectile);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Druid", () -> new Druid(world, -1000, -1000));
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("Button", () -> "bounces off walls",
        () -> {
          sprite.setImage("Druid1");
          bulletLauncher.addProjectileModifier(p -> p.addBuff(
              new OnTickBuff<Projectile>(Projectile::bounce)));
        }, 200);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Button", () -> "regrows 4 more times",
        () -> {
          sprite.setImage("Druid2");
          addBuff(
              new StatBuff<Turret>(Type.ADDED, ExtraStats.respawns, 4));
        }, 800);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("Button", () -> "gains bonus duration when regrowing",
        () -> addBuff(
            new StatBuff<Turret>(Type.ADDED, ExtraStats.bonusDuration, 6000)),
        1500);
  }

  @Override
  protected Upgrade up400() {
    return new Upgrade("Button", () -> "attacks way faster",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 3.3f)),
        10000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("Button", () -> "keeps regrowing basically forever",
        () -> addBuff(
            new StatBuff<Turret>(Type.ADDED, ExtraStats.respawns, 30)), 30000);
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("Button", () -> "increases pierce when regrowing",
        () -> addBuff(
            new StatBuff<Turret>(Type.ADDED, ExtraStats.pierceScaling,
                .6f)), 150);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Button", () -> "can hit the same bloon many times",
        () -> bulletLauncher.addProjectileModifier(p -> p.setMultihit(true)), 500);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("Button", () -> "Roots bloons",
        () -> bulletLauncher.addProjectileModifier(p -> p.addMobCollide((proj, mob) -> mob.addBuff(
            new StatBuff<TdMob>(Type.MORE,
                p.getPower() / mob.getStats()[TdMob.Stats.health] * 5000, TdMob.Stats.speed,
                0.001f)
        ), 0)), 2500);
  }

  private static final float[] blueColors = Util.getColors(.3f, 1.05f, 1.65f);

  @Override
  protected Upgrade up040() {
    return new Upgrade("Button", () -> "slows everything in a large area",
        () -> bulletLauncher.addProjectileModifier(p -> {
          p.getSprite().setColors(blueColors);
          p.addMobCollide((proj, mob) -> {
                world.getMobsGrid().callForEachCircle((int) mob.getX(), (int) mob.getY(),
                    (int) (proj.getStats()[Projectile.Stats.size] * 1.5f),
                    enemy -> enemy.addBuff(new StatBuff<TdMob>(
                            Type.MORE, proj.getPower() / enemy.getStats()[TdMob.Stats.health] * 10000,
                            TdMob.Stats.speed, 0.80f
                        )
                    ));
                return true;
              }
              , 0);
        }), 20000);
  }

  private static final long weakenId = Util.getUid();

  @Override
  protected Upgrade up050() {
    return new Upgrade("Button", () -> "Ability: bloons temporarily take 100% increased damage",
        () -> Ability.add("Freeze", 60000,
            () -> "Enemies take 100% increased damage for 15 seconds",
            () -> world.getMobsList().forEach(mob -> mob.addBuff(
                new StatBuff<TdMob>(Type.INCREASED, 15000, TdMob.Stats.damageTaken, 1))),
            weakenId)
        , 20000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("Button", () -> "Also sets shit on fire",
        () -> bulletLauncher.addProjectileModifier(p -> p.addMobCollide((proj, mob) -> {
          world.getMobsGrid().callForEachCircle((int) mob.getX(), (int) mob.getY(),
              (int) (p.getStats()[Projectile.Stats.size] * .6f),
              enemy -> enemy.addBuff(new Ignite<>(p.getPower(), 3000)));
          return true;
        })), 20000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("Button", () -> "Projectiles have more speed and piercs",
        () -> {
          addBuff(
              new StatBuff<Turret>(Type.MORE, Stats.speed, 7));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 10));
        }, 50000);
  }

  private static final float[] redColors = Util.getColors(2, 0.7f, 0.3f);

  @Override
  protected Upgrade up003() {
    return new Upgrade("Button", () -> "Enemies hit explode",
        () -> bulletLauncher.addProjectileModifier(p -> {
          p.getSprite().setColors(redColors);
          p.addMobCollide((proj, mob) -> {
            BasicCollides.explodeFunc(
                (int) mob.getX(),
                (int) mob.getY(),
                proj.getPower(),
                proj.getStats()[Projectile.Stats.size] * .4f,
                "Explosion2-0");
            return true;
          });
        }), 20000);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Button", () -> "increases power and size when regrowing",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.powScaling,
              .4f));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.sizeScaling,
              .7f));
        }, 2000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Button", () -> "double damage",
        () -> addBuff(new StatBuff<Turret>(Type.ADDED, Stats.power, 3f)), 500);
  }

  private void regrow(RefFloat respawnsLeft, Projectile proj) {
    if (respawnsLeft.get() < 1) {
      return;
    }
    respawnsLeft.add(-1);
    proj.setActive(false);
    proj.addBuff(
        new StatBuff<Projectile>(Type.MORE, Projectile.Stats.power,
            stats[ExtraStats.powScaling]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.INCREASED, Projectile.Stats.size,
            stats[ExtraStats.sizeScaling]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.MORE, Projectile.Stats.pierce,
            stats[ExtraStats.pierceScaling]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.pierce,
            stats[ExtraStats.bonusPierce]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.duration,
            stats[ExtraStats.bonusDuration]));
    proj.special(0);
  }

  private void modProjectile(Projectile p) {
    var respawnsLeft = new RefFloat(stats[ExtraStats.respawns]);
    p.addMobCollide(BasicCollides.damage);
    p.addBeforeDeath(proj -> regrow(respawnsLeft, proj));
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 16;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 3f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 5f;
    stats[Stats.aspd] = .4f;
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
}
