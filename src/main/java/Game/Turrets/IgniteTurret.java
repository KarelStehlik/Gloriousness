package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.Ignite;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Mobs.TdMob;
import Game.TurretGenerator;
import Game.World;

public class IgniteTurret extends Turret {

  public static final String image = "Flamethrower";

  public IgniteTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Fireball-0"));
    onStatsUpdate();
    bulletLauncher.addMobCollide((proj, mob) ->
    {
      mob.addBuff(new Ignite<>(proj.getPower(), this.getStats()[ExtraStats.igniteDuration]));
      return true;
    });
    bulletLauncher.setSpread(45);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Fire", () -> new IgniteTurret(world, -1000, -1000));
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("Button", () -> "ignites are permanent",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.igniteDuration, 300.3f)),
        10000);
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 11;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 0.2f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = 100f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 20f;
    stats[Stats.cost] = 500f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.igniteDuration] = 1000f;
  }

  public static final class ExtraStats {

    public static final int igniteDuration = 10;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
