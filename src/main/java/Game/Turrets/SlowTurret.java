package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.TdWorld;
import Game.TurretGenerator;

public class SlowTurret extends Turret {


  public static final String image = "SlowTower";

  public SlowTurret(TdWorld world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Winter"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.slow);
    bulletLauncher.setSpread(10);
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, image, "Slowing", () -> new SlowTurret(world, -1000, -1000));
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 0.004f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = 0.2f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 7f;
    stats[Stats.cost] = 200f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
