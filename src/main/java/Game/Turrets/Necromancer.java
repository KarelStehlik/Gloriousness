package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.OnTickBuff;
import Game.BulletLauncher;
import Game.Projectile;
import Game.TdMob;
import Game.TdMob.MoveAlongTrack;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import java.awt.Point;

public class Necromancer extends Turret{
  public static final String image = "Necromancer";
  public final ExtraStats extraStats = new ExtraStats();

  public Necromancer(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Zombie"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.fire);
    bulletLauncher.setSpread(45);
    bulletLauncher.setProjectileModifier(p->{
      TdMob.MoveAlongTrack<Projectile> mover = new MoveAlongTrack<Projectile>(true,world.getMapData(),new Point(0,0),baseStats.speed,Projectile::delete);
      p.addBuff(new OnTickBuff<Projectile>(Float.POSITIVE_INFINITY, mover::tick));
    });
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.setDuration(baseStats.projectileDuration.get());
    bulletLauncher.setPierce((int) baseStats.pierce.get());
    bulletLauncher.setPower(baseStats.power.get());
    bulletLauncher.setSize(baseStats.bulletSize.get());
    bulletLauncher.setSpeed(0);
    bulletLauncher.setCooldown(baseStats.cd.get());
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, "Necromancer",
        (x, y) -> new Necromancer(world, x, y),
        image, 100);
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
      projectileDuration = new RefFloat(5);
      bulletSize = new RefFloat(50);
      speed = new RefFloat(20);
    }
  }
  // end of generated stats
}
