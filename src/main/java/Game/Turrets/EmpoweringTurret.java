package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.UniqueBuff;
import Game.BulletLauncher;
import Game.Player.ExtraStats;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import java.util.List;

public class EmpoweringTurret extends Turret {

  public static final String image = "EmpoweringTower";

  public EmpoweringTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Buff"));
    onStatsUpdate();
    bulletLauncher.addProjectileCollide(this::collide);
    bulletLauncher.setSpread(45);
    bulletLauncher.setProjectileModifier(p -> p.addBuff(new UniqueBuff<>(id, p1 -> {
    })));
  }

  public static TurretGenerator generator(World world) {
    var stats = new BasicTurret.Stats();
    return new TurretGenerator(world, "Empowering",
        (x, y) -> new EmpoweringTurret(world, x, y),
        image, stats.cost.get(), stats.size.get(), stats.spritesize.get(), stats.range.get());
  }

  private static void addBuff(Projectile p2, float pow) {
    p2.addMobCollide((proj2, mob) -> BasicCollides.explodeFunc(proj2, pow));
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

  private boolean collide(Projectile p1, Projectile p2) {
    final float pow = p1.getPower();
    p2.addBuff(new UniqueBuff<>(id, proj2 -> addBuff(proj2, pow)));
    return true;
  }


  // generated stats
  @Override
  public void clearStats() {
      stats[Stats.power] = 100f;
      stats[Stats.range] = 500f;
      stats[Stats.pierce] = 100f;
      stats[Stats.cd] = 10f;
      stats[Stats.projectileDuration] = 2f;
      stats[Stats.bulletSize] = 50f;
      stats[Stats.speed] = 8f;
      stats[Stats.cost] = 100f;
      stats[Stats.size] = 50f;
      stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
