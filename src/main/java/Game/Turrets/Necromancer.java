package Game.Turrets;

import Game.BasicCollides;
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
      p.move(initPoint.getX(), initPoint.getY());
      p.setRotation(Data.unstableRng.nextFloat() * 360);
      if (!walking) {
        return;
      }
      TdMob.MoveAlongTrack<Projectile> mover = new MoveAlongTrack<Projectile>(true,
          world.getMapData(), new Point(0, 0), stats, Stats.speed, Projectile::delete,
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
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 6f));
        }, 500);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Zombie", () -> "zombies explode.",
        () -> {
          walking = false;
          bulletLauncher.addProjectileModifier(p -> {
            p.addMobCollide((proj, mob) -> {
              BasicCollides.explodeFunc((int) mob.getX(), (int) mob.getY(), proj.getPower() * 2,
                  200);
              return true;
            });
          });
        }, 2000);
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("Zombie", () -> "makes zombies faster.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.cd, 0.4f));
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
