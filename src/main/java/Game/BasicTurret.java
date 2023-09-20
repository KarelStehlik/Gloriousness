package Game;

public class BasicTurret extends Turret {

  Stats stats = new Stats();

  protected BasicTurret(World world, int X, int Y, BulletLauncher launcher) {
    super(world, X, Y, "none", launcher, new Stats());
    onStatsUpdate();
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
  } // end of generated stats
}
