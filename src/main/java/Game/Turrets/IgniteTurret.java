package Game.Turrets;

import Game.Ability;
import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.Ignite;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.Tag;
import Game.BulletLauncher;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Player;
import Game.Projectile;
import Game.Projectile.Stats;
import Game.TurretGenerator;
import Game.World;
import general.Data;
import general.RefFloat;
import general.Util;
import java.util.List;

public class IgniteTurret extends Turret {

  public static final String image = "Flamethrower";

  public IgniteTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Fireball-0"));
    onStatsUpdate();
    bulletLauncher.addMobCollide((proj, mob) ->
    {
      mob.addBuff(new Ignite<>(proj.getPower(), this.getStats()[ExtraStats.igniteDuration]));
      return true;
    });
    bulletLauncher.setSpread(45);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Fire", () -> new IgniteTurret(world, -1000, -1000));
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("Button", () -> "ignites last 3x longer",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.igniteDuration, 3)),
        1000);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Button", () -> "ignites last 69420x longer",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.igniteDuration, 69420)),
        2500);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("Button",
        () -> "30% increased attack speed and range for every second this has been continuously attacking",
        () -> {
          final float increasePerTick = 0.001f * 0.30f * Game.tickIntervalMillis;
          RefFloat currentIncrease = new RefFloat(0);
          addBuff(new OnTickBuff<Turret>(turret -> {
            addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.aspd, increasePerTick));
            addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.range, increasePerTick));
            currentIncrease.add(increasePerTick);
            if (turret.bulletLauncher.canAttack()) {
              addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.aspd, -currentIncrease.get()));
              addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.range, -currentIncrease.get()));
              currentIncrease.set(0);
            }
          }));
        },
        8000);
  }

  @Override
  protected Upgrade up400() {
    return new Upgrade("Button", () -> "1% increased damage for every bloon hit directly recently.",
        () -> {
          bulletLauncher.addMobCollide((proj, mob) -> addBuff(
              new StatBuff<Turret>(Type.INCREASED, 4000, Stats.power, 0.01f)));
        },
        30000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("Button", () -> "+19 pierce.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.pierce, 19));
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.speed, 1));
        },
        80000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Button", () -> "aoe",
        () -> bulletLauncher.addMobCollide((proj, mob) ->
        {
          world.getMobsGrid().callForEachCircle((int) proj.getX(), (int) proj.getY(),
              (int) stats[ExtraStats.aoe], m -> m.addBuff(
                  new Ignite<>(proj.getPower(), this.getStats()[ExtraStats.igniteDuration])));
          return true;
        }),
        1000);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Button", () -> "bigger aoe, but 60% less damage",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.aoe, 100));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, .4f));
        },
        2500);
  }

  private void makePuddle(TdMob mob) {
    var aggreg = mob.getBuffHandler().find(Ignite.class);
    if (aggreg == null) {
      return;
    }

    Ignite<TdMob>.Aggregator ignite = (Ignite<TdMob>.Aggregator) aggreg;

    int size = (int) mob.getStats()[TdMob.Stats.size];
    var puddle = new Projectile(world, "fire", mob.getX(), mob.getY(), 0,
        Data.gameMechanicsRng.nextFloat() * 360, size, size, Integer.MAX_VALUE, size,
        stats[ExtraStats.puddleDuration], ignite.getDpTick() * stats[ExtraStats.puddleDamage]);
    puddle.setMultihit(true);
    puddle.addMobCollide(BasicCollides.damage);
    world.getProjectilesList().add(puddle);
  }

  private static final long puddleId = Util.getUid();

  @Override
  protected Upgrade up003() {
    return new Upgrade("Zombie",
        () -> "Bloons hit directly leave behind a lava puddle on death based on the current ignite dps.",
        () -> bulletLauncher.addProjectileModifier(p -> {
              p.addMobCollide((zombie, bloon) -> {
                if (!bloon.addBuff(new Tag<TdMob>(puddleId))) {
                  return false;
                }
                bloon.addBuff(new DelayedTrigger<TdMob>(Float.POSITIVE_INFINITY, this::makePuddle, true,
                    stats[ExtraStats.puddleSpread] > 0));
                return true;
              }, 0);

            }
        ), 7000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("Zombie",
        () -> "lava puddles last 4x longer and do double damage.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.puddleDuration, 4));
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.puddleDamage, 2));
        }, 20000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("Zombie",
        () -> "children of hit bloons will also leave puddles.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.puddleSpread, 1));
        }, 60000);
  }

  private static final long projCollideId = Util.getUid();

  @Override
  protected Upgrade up020() {
    return new Upgrade("Button",
        () -> "also hits projectiles, causing them to do what this does (they cannot be hit by any flamethrower again)",
        () -> {
          bulletLauncher.addProjectileModifier(p -> p.addBuff(new Tag<Projectile>(projCollideId)));
          bulletLauncher.addProjectileCollide((thisProj, otherProj) -> {
            if (!otherProj.addBuff(new Tag<Projectile>(projCollideId))) {
              return false;
            }
            thisProj.getMobCollides().forEach(otherProj::addMobCollide);
            return true;
          });
        },
        3000);
  }


  @Override
  protected Upgrade up030() {
    return new Upgrade("Button",
        () -> "also hits the player to grant 1% increased attack speed for 5 seconds.",
        () -> {
          bulletLauncher.addPlayerCollide((thisProj, player) -> {
            player.addBuff(new StatBuff<Player>(Type.INCREASED, 5000, Player.Stats.aspd, 0.01f));
            return true;
          });
        },
        7500);
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("Button", () -> "adds 3 pierce",
        () -> addBuff(new StatBuff<Turret>(Type.ADDED, Stats.pierce, 3)),
        1500);
  }

  @Override
  protected Upgrade up040() {
    return new Upgrade("Button", () -> "also gives the player 1% increased damage for 5 seconds.",
        () -> {
          bulletLauncher.addPlayerCollide((thisProj, player) -> {
            player.addBuff(
                new StatBuff<Player>(Type.INCREASED, 5000, Player.Stats.projPower, 0.01f));
            return true;
          });
        },
        40000);
  }

  private static final long abilityId = Util.getUid();

  @Override
  protected Upgrade up050() {
    return new Upgrade("Button",
        () -> "Ability: every projectile that currently exists explodes, dealing aoe damage based on its power and pierce. Pierce above 2500 has no effect.",
        () -> {
          var a = Ability.add("Fireball-0", 10000, () -> "Boom",
              () -> {
                List<Projectile> list = world.getProjectilesList();
                for (int i = 0; i < list.size(); i++) {
                  Projectile proj = list.get(i);
                  BasicCollides.explodeFunc(
                      (int) proj.getX(), (int) proj.getY(),
                      proj.getStats()[Projectile.Stats.power] * Math.min(2500,
                          proj.getStats()[Projectile.Stats.pierce]),
                      proj.getStats()[Projectile.Stats.size] * 2.5f);
                  proj.delete();
                }
              }, abilityId);
          addBuff(new DelayedTrigger<Turret>(t -> a.delete(), true));
        },
        40000);
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 15;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 0.2f;
    stats[Stats.range] = 300f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = 70f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 20f;
    stats[Stats.cost] = 500f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.igniteDuration] = 1000f;
    stats[ExtraStats.aoe] = 100f;
    stats[ExtraStats.puddleDamage] = 1.5f;
    stats[ExtraStats.puddleDuration] = 6f;
    stats[ExtraStats.puddleSpread] = 0f;
  }

  public static final class ExtraStats {

    public static final int igniteDuration = 10;
    public static final int aoe = 11;
    public static final int puddleDamage = 12;
    public static final int puddleDuration = 13;
    public static final int puddleSpread = 14;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
