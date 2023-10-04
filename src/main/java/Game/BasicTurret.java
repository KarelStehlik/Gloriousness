package Game;

import general.Log;

public class BasicTurret extends Turret {

  public final ExtraStats extraStats = new ExtraStats();


  protected BasicTurret(World world, int X, int Y, BulletLauncher launcher) {
    super(world, X, Y, "none", launcher, new Stats());
    onStatsUpdate();
  }


  public static void create(World world, int X, int Y, BulletLauncher launcher,  BaseStats newStats){
    Log.write("Warning: non-overridden turret create");
  }

  // generated stats
  public static final class ExtraStats {


    public void init() {
      
    }

    public ExtraStats() {init();}
  }

  public static final class Stats extends BaseStats {
    @Override
    public void init() {
      power=100f;
      range=500f;
      pierce=100f;
      cd=1f;
      projectileDuration=2f;
      bulletSize=50f;
      speed=10f;
    }
    public Stats(){init();}
  }
  // end of generated stats
}