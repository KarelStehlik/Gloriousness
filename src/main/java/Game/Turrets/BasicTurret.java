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
      stats[Stats.power] = 100;
    stats[Stats.range] =  500;
    stats[Stats.pierce] = 100;
    stats[Stats.cd] = 1;
    stats[Stats.projectileDuration ]= 2;
    stats[Stats.bulletSize ]= 50;
    stats[Stats.speed] = 15;
    stats[Stats.cost ]= 100;
    stats[Stats.size]= 50;
    stats[Stats.spritesize ]= 150;
  }
  // end of generated stats
}
