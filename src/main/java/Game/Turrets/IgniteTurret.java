package Game.Turrets;

import Game.BasicCollides;
import Game.BulletLauncher;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import java.util.List;

public class IgniteTurret extends Turret {

  public static final String image = "Flamethrower";

  public IgniteTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Fireball-0"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.fire);
    bulletLauncher.setSpread(45);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world,image, "Fire",()->new IgniteTurret(world,-1000,-1000));
  }

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


  // generated stats
  @Override
  public void clearStats() {
      stats[Stats.power] = 100f;
      stats[Stats.range] = 500f;
      stats[Stats.pierce] = 100f;
      stats[Stats.cd] = 1f;
      stats[Stats.projectileDuration] = 2f;
      stats[Stats.bulletSize] = 50f;
      stats[Stats.speed] = 20f;
      stats[Stats.cost] = 100f;
      stats[Stats.size] = 50f;
      stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
