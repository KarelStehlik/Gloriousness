package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.UniqueBuff;
import Game.BulletLauncher;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;

public class EmpoweringTurret extends Turret {

  public static final String image = "EmpoweringTower";
  public final ExtraStats extraStats = new ExtraStats();

  public EmpoweringTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Buff"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addProjectileCollide(this::collide);
    bulletLauncher.setSpread(45);
    bulletLauncher.setProjectileModifier(p -> p.addBuff(new UniqueBuff<>(id, p1 -> {
    })));
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, "Empowering",
        (x, y) -> new EmpoweringTurret(world, x, y),
        image, 100);
  }

  private boolean collide(Projectile p1, Projectile p2) {
    p2.addBuff(new UniqueBuff<>(id, proj -> proj.addMobCollide(BasicCollides.explode)));
    return true;
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
