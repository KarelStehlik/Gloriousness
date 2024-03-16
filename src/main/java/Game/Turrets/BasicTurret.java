package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import java.util.List;

public class BasicTurret extends Turret {

  public static final String image = "BasicTower";
  public final ExtraStats extraStats = new ExtraStats();

  public BasicTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Dart"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.setSpread(45);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, "Basic",
        (x, y) -> new BasicTurret(world, x, y),
        image, 100);
  }

  private Upgrade up100() {
    return new Upgrade("Meteor", () -> "fuck",
        () -> addBuff(new StatBuff<Turret>(Type.INCREASED, 5000, baseStats.bulletSize, 1)), 1000);
  }

  @Override
  protected List<Upgrade> getUpgradePath1() {
    return List.of(up100());
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
      cd = new RefFloat(1);
      projectileDuration = new RefFloat(2);
      bulletSize = new RefFloat(50);
      speed = new RefFloat(15);
    }
  }
  // end of generated stats
}
