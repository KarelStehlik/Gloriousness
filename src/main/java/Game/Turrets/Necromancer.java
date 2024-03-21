package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.OnTickBuff;
import Game.BulletLauncher;
import Game.Projectile;
import Game.TdMob;
import Game.TdMob.MoveAlongTrack;
import Game.TurretGenerator;
import Game.World;
import Game.World.TrackPoint;
import general.Data;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Necromancer extends Turret {

  public static final String image = "Necromancer";
  public final ExtraStats extraStats = new ExtraStats();
  private final List<TrackPoint> spawnPoints = new ArrayList<>(1);

  public Necromancer(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Zombie"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.fire);
    bulletLauncher.setSpread(45);
    updateRange();
    bulletLauncher.setProjectileModifier(p -> {
      TrackPoint initPoint = spawnPoints.get(Data.gameMechanicsRng.nextInt(0, spawnPoints.size()));
      p.move(initPoint.x, initPoint.y);
      TdMob.MoveAlongTrack<Projectile> mover = new MoveAlongTrack<Projectile>(true,
          world.getMapData(), new Point(0, 0), baseStats.speed, Projectile::delete,
          Math.max(initPoint.node - 1, 0));
      p.addBuff(new OnTickBuff<Projectile>(Float.POSITIVE_INFINITY, mover::tick));
    });
  }

  public static TurretGenerator generator(World world) {
    var stats = new BasicTurret.Stats();
    return new TurretGenerator(world, "Necromancer",
        (x, y) -> new Necromancer(world, x, y),
        image, stats.cost.get(), stats.size.get(), stats.spritesize.get(), stats.range.get());
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
  public void onStatsUpdate() {
    bulletLauncher.setDuration(baseStats.projectileDuration.get());
    bulletLauncher.setPierce((int) baseStats.pierce.get());
    bulletLauncher.setPower(baseStats.power.get());
    bulletLauncher.setSize(baseStats.bulletSize.get());
    bulletLauncher.setSpeed(0);
    bulletLauncher.setCooldown(baseStats.cd.get());
  }

  private void updateRange() {
    spawnPoints.clear();
    for (TrackPoint p : world.spacPoints) {
      if (Util.distanceSquared(p.x - x, p.y - y) < baseStats.range.get() * baseStats.range.get()) {
        spawnPoints.add(p);
      }
    }
  }

  // generated stats
  public static final class ExtraStats {

    public ExtraStats() {
      init();
    }

    public void init() {

    }
  }

  public static final class Stats extends BaseStats {

    public Stats() {
      init();
    }

    @Override
    public void init() {
      power = new RefFloat(100);
      range = new RefFloat(500);
      pierce = new RefFloat(1000);
      cd = new RefFloat(1);
      projectileDuration = new RefFloat(20);
      bulletSize = new RefFloat(60);
      speed = new RefFloat(10);
      cost = new RefFloat(100);
      size = new RefFloat(50);
      spritesize = new RefFloat(150);
    }
  }
  // end of generated stats
}
