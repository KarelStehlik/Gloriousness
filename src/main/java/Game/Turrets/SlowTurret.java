package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.Player.ExtraStats;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import java.util.List;

public class SlowTurret extends Turret {


  public static final String image = "SlowTower";

  public SlowTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Winter"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.slow);
    bulletLauncher.setSpread(10);
  }

  public static TurretGenerator generator(World world) {
    var stats = new Stats();
    return new TurretGenerator(world, "Ice",
        (x, y) -> new SlowTurret(world, x, y),
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



  // generated stats
  @Override
  public void clearStats() {
      stats[Stats.power] = 0.005f;
      stats[Stats.range] = 500f;
      stats[Stats.pierce] = 100f;
      stats[Stats.cd] = 1f;
      stats[Stats.projectileDuration] = 2f;
      stats[Stats.bulletSize] = 50f;
      stats[Stats.speed] = 7f;
      stats[Stats.cost] = 100f;
      stats[Stats.size] = 50f;
      stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
