package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.TdWorld;
import Game.TurretGenerator;
import windowStuff.Graphics;
import windowStuff.ImageData;

public class SlowTurret extends Turret {

  @Override
  protected ImageData getImageUpdate(){
    return Graphics.getImage("SlowTower");
  }

  public SlowTurret(TdWorld world, int X, int Y) {
    super(world, X, Y, new BulletLauncher(world, "Winter"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.slow);
    bulletLauncher.setSpread(10);
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, "SlowTower", "Slowing", () -> new SlowTurret(world, -1000, -1000));
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
