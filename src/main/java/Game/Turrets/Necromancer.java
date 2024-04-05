package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.Ignite;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Mobs.TdMob;
import Game.Mobs.TdMob.MoveAlongTrack;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import Game.World.TrackPoint;
import general.Data;
import general.Log;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Necromancer extends Turret {

  public static final String image = "Necromancer";
  private final List<TrackPoint> spawnPoints = new ArrayList<>(1);
  private boolean walking = true;

  public Necromancer(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Zombie"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.addProjectileModifier(p -> {
      TrackPoint initPoint = spawnPoints.get(Data.gameMechanicsRng.nextInt(0, spawnPoints.size()));
      Point offset = new Point(Data.gameMechanicsRng.nextInt(-10, 10),
          Data.gameMechanicsRng.nextInt(-10, 10));
      p.move(initPoint.getX() + offset.x, initPoint.getY() + offset.y);
      p.setRotation(Data.unstableRng.nextFloat() * 360);
      p.setMultihit(true);
      if (!walking) {
        return;
      }
      TdMob.MoveAlongTrack<Projectile> mover = new MoveAlongTrack<Projectile>(true,
          world.getMapData(), offset, stats, Stats.speed, Projectile::delete,
          Math.max(initPoint.getNode() - 1, 0));
      p.addBuff(new OnTickBuff<Projectile>(mover::tick));
    });
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Necromancer",
        () -> new Necromancer(world, -1000, -1000));
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("Zombie", () -> "zombies are dead. The don't move but are more powerful.",
        () -> {
          walking = false;
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.bulletSize, 1));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 5f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 2f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 3f));
        }, 500);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Zombie",
        () -> "zombies spin in their graves. this causes nearby non-MOAB bloons to slow down",
        () -> bulletLauncher.addProjectileModifier(p -> {
              p.addBuff(new OnTickBuff<Projectile>(proj -> {
                proj.setRotation(proj.getRotation() + 5);
                world.getMobsGrid()
                    .callForEachCircle((int) proj.getX(), (int) proj.getY(), 200, mob -> {
                      if (!mob.isMoab()) {
                        mob.addBuff(new StatBuff<TdMob>(Type.MORE, 20, TdMob.Stats.speed, 0.9f));
                      }
                    });
              }));
            }
        ), 500);
  }

  private static final float respawnChance = .75f;

  @Override
  protected Upgrade up030() {
    return new Upgrade("Zombie",
        () -> "The first bloon that a zombie touches will spawn a new zombie on death.",
        () -> bulletLauncher.addProjectileModifier(p -> {
              RefFloat alreadyHit = new RefFloat(0);
              p.addMobCollide((zombie, bloon) -> {
                if (alreadyHit.get() > 0) {
                  return false;
                }
                alreadyHit.set(1);
                bloon.addBuff(new DelayedTrigger<TdMob>(mob -> {
                  float cd = bulletLauncher.getRemainingCooldown();
                  var newZombie = bulletLauncher.attack(0);
                  bulletLauncher.setRemainingCooldown(cd);
                  newZombie.move(mob.getX(), mob.getY());
                }, true));
                return true;
              }, 0);
            }
        ), 500);
  }

  private boolean ignites = false;

  private void explode(Projectile proj, float power, float radius, String img) {
    BasicCollides.explodeFunc((int) proj.getX(), (int) proj.getY(), proj.getPower() * power,
        radius, img);
    if (ignites) {
      world.getMobsGrid().callForEachCircle((int) proj.getX(), (int) proj.getY(),
          (int) (radius * 1.5f), m -> m.addBuff(new Ignite<>(proj.getPower() * power, 3000)));
    }
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Zombie", () -> "zombies explode when destroyed.",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            p.addBeforeDeath(proj -> {
              explode(proj, 5, 150, "Explosion1-0");
            });
          });
        }, 500);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Zombie", () -> "zombies also explode on contact.",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            p.addMobCollide((proj, mob) -> {
              explode(proj, 7, 100, "Explosion2-0");
              return true;
            });
          });
        }, 2000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("Zombie",
        () -> "zombies also explode all the time. this is mostly for show. They get more pierce.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Turret.Stats.pierce, 2));
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new OnTickBuff<Projectile>(proj -> {
              explode(proj, 20, 50, "Explosion2-0");
            }));
          });
        }, 7000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("Zombie", () -> "explosions also ignite things",
        () -> {
          ignites = true;
        }, 20000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("Zombie", () -> "constantly ignites everything",
        () -> {
          addBuff(new OnTickBuff<Turret>(t -> world.getMobsList().forEach(
              mob -> mob.addBuff(new Ignite<>(stats[Stats.power] * stats[Stats.pierce], 3000)))));
        }, 50000);
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("Zombie", () -> "produces zombies faster.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 0.6f));
        }, 500);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Zombie", () -> "zombies have more pierce",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 2));
        }, 500);
  }

  @Override
  public void place() {
    super.place();
    updateRange();
  }


  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced || spawnPoints.isEmpty()) {
      return;
    }
    bulletLauncher.tickCooldown();
    TdMob target = world.getMobsGrid()
        .getFirst(new Point((int) x, (int) y), (int) stats[Turret.Stats.range]);
    if (target != null) {
      setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
    }
    while (bulletLauncher.canAttack()) {
      bulletLauncher.attack(rotation);
    }

    buffHandler.tick();
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.setDuration(stats[Stats.projectileDuration]);
    bulletLauncher.setPierce((int) stats[Stats.pierce]);
    bulletLauncher.setPower(stats[Stats.power]);
    bulletLauncher.setSize(stats[Stats.bulletSize]);
    bulletLauncher.setSpeed(0);
    bulletLauncher.setCooldown(stats[Stats.cd]);
  }

  private void updateRange() {
    spawnPoints.clear();
    Log.write(stats[Stats.range] * stats[Stats.range]);
    for (TrackPoint p : world.spacPoints) {
      if (Util.distanceSquared(p.getX() - x, p.getY() - y)
          < stats[Stats.range] * stats[Stats.range]) {
        spawnPoints.add(p);
      }
    }
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 200f;
    stats[Stats.pierce] = 3f;
    stats[Stats.cd] = 1000f;
    stats[Stats.projectileDuration] = 20f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 10f;
    stats[Stats.cost] = 300f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
