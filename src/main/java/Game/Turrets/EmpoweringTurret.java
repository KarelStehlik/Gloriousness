package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.World;
import general.RefFloat;

public class EmpoweringTurret extends Turret {

  public final ExtraStats extraStats = new ExtraStats();

  public EmpoweringTurret(World world, int X, int Y) {
    super(world, X, Y, "Button",
        new BulletLauncher(world, "fire"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addProjectileCollide((p1, p2) -> p2.addMobCollide(BasicCollides.explode));
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
      power = new RefFloat(100);
      range = new RefFloat(500);
      pierce = new RefFloat(100);
      cd = new RefFloat(10);
      projectileDuration = new RefFloat(2);
      bulletSize = new RefFloat(50);
      speed = new RefFloat(10);
    }
  }
  // end of generated stats
}
