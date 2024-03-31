package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.TurretGenerator;
import Game.World;
import general.Log;
import java.util.List;

public class BasicTurret extends Turret {

  public static final String image = "BasicTower";

  public BasicTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Dart"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.setSpread(45);
  }

  public static TurretGenerator generator(World world) {
    var stats = new Stats();
    return new TurretGenerator(world, "Basic",
        (x, y) -> new BasicTurret(world, x, y),
        image, stats.cost.get(), stats.size.get(), stats.spritesize.get(), stats.range.get());
  }

  private Upgrade up100() {
    return new Upgrade("Meteor", () -> "fuck",
        () -> {
          addBuff(
              new StatBuff<Turret>(Type.INCREASED, Float.POSITIVE_INFINITY, baseStats.bulletSize,
                  1));
          Log.write("ff");
        }, 1000);
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
  @Override
  public void clearStats() {
      stats[Stats.power] = 100f;
      stats[Stats.range] = 500f;
      stats[Stats.pierce] = 100f;
      stats[Stats.cd] = 1f;
      stats[Stats.projectileDuration] = 2f;
      stats[Stats.bulletSize] = 50f;
      stats[Stats.speed] = 15f;
      stats[Stats.cost] = 100f;
      stats[Stats.size] = 50f;
      stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
