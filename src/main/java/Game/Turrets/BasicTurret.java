package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.World;

public class BasicTurret extends Turret {

  public final ExtraStats extraStats = new ExtraStats();

  public BasicTurret(World world, int X, int Y) {
    super(world, X, Y, "none",
        new BulletLauncher(world, "Egg"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.setSpread(45);
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
      power = 100f;
      range = 500f;
      pierce = 100f;
      cd = 1f;
      projectileDuration = 2f;
      bulletSize = 50f;
      speed = 10f;
    }
  }
  // end of generated stats
}
