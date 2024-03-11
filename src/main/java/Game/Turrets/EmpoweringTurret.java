package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.World;

public class EmpoweringTurret extends Turret {

  public final ExtraStats extraStats = new ExtraStats();

  public EmpoweringTurret(World world, int X, int Y) {
    super(world, X, Y, "Button",
        new BulletLauncher(world, "fire"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addProjectileCollide((p1, p2) -> p2.addMobCollide(BasicCollides.fire));
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
