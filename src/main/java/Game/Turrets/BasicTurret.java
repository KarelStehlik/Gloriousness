package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import java.util.List;

public class BasicTurret extends Turret {

  public static final String image = "BasicTower";

  public BasicTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Dart"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Basic", () -> new BasicTurret(world, -1000, -1000));
  }

  private Upgrade up100() {
    return new Upgrade("Meteor", () -> "fuck",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            var g = new Projectile.Guided(1000, 7);
            p.addBuff(new OnTickBuff<Projectile>(g::tick));
            p.addBuff(new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.pierce, 10000));
            p.addBuff(new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.duration, 10000));
          });
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
    stats[Stats.power] = 1f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 2f;
    stats[Stats.cd] = 1400f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = 15f;
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
