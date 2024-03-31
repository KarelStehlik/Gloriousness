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
        new BulletLauncher(world, "Dart"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.setSpread(45);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Basic", () -> new BasicTurret(world, -1000, -1000));
  }

  private Upgrade up100() {
    return new Upgrade("Meteor", () -> "fuck",
        () -> {
          addBuff(
              new StatBuff<Turret>(Type.INCREASED, Float.POSITIVE_INFINITY, Stats.bulletSize,
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
