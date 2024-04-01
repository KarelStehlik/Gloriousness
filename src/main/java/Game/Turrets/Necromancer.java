package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.OnTickBuff;
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

  public Necromancer(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Zombie"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.fire);
    bulletLauncher.setSpread(45);
    bulletLauncher.addProjectileModifier(p -> {
      TrackPoint initPoint = spawnPoints.get(Data.gameMechanicsRng.nextInt(0, spawnPoints.size()));
      p.move(initPoint.x, initPoint.y);
      TdMob.MoveAlongTrack<Projectile> mover = new MoveAlongTrack<Projectile>(true,
          world.getMapData(), new Point(0, 0), stats, Stats.speed, Projectile::delete,
          Math.max(initPoint.node - 1, 0));
      p.addBuff(new OnTickBuff<Projectile>(mover::tick));
    });
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Necromancer",
        () -> new Necromancer(world, -1000, -1000));
  }

  @Override
  public void place() {
    super.place();
    updateRange();
  }

  @Override
  protected List<Upgrade> getUpgradePath1() {
    return List.of();
  }

  @Override
  protected List<Upgrade> getUpgradePath2() {
    return List.of();
  }

  @Override
  protected List<Upgrade> getUpgradePath3() {
    return List.of();
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
      if (Util.distanceSquared(p.x - x, p.y - y) < stats[Stats.range] * stats[Stats.range]) {
        spawnPoints.add(p);
      }
    }
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 1f;
    stats[Stats.cd] = 10f;
    stats[Stats.projectileDuration] = 20f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 10f;
    stats[Stats.cost] = 200f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
