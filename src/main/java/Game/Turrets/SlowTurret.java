package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import java.util.List;

public class SlowTurret extends Turret {


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

  public static final String image = "SlowTower";
  public final ExtraStats extraStats = new ExtraStats();

  public SlowTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Winter"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.slow);
    bulletLauncher.setSpread(10);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, "Ice",
        (x, y) -> new SlowTurret(world, x, y),
        image, 100);
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
      power = new RefFloat(0.005);
      range = new RefFloat(500);
      pierce = new RefFloat(100);
      cd = new RefFloat(1);
      projectileDuration = new RefFloat(2);
      bulletSize = new RefFloat(50);
      speed = new RefFloat(7);
    }
  }
  // end of generated stats
}
