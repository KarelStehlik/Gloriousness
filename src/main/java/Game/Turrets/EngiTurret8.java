package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.BulletLauncher;
import Game.TdWorld;
import general.Data;
import windowStuff.Graphics;
import windowStuff.ImageData;

public class EngiTurret8 extends Turret {

  @Override
  protected ImageData getImage(){
    return Graphics.getImage("turret");
  }

  public EngiTurret8(TdWorld world, int X, int Y, BulletLauncher templateLauncher) {
    super(world, X, Y, new BulletLauncher(templateLauncher));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    addBuff(
        new DelayedTrigger<Turret>(stats[EngiTurret.ExtraStats.duration], Turret::delete, false));
  }

  @Override
  public boolean blocksPlacement() {
    return false;
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 12;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 250f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.45f, 1.2f);
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = Data.gameMechanicsRng.nextFloat(1f, 19f);
    stats[Stats.cost] = 25f;
    stats[Stats.size] = 15f;
    stats[Stats.spritesize] = 70f;
    stats[ExtraStats.duration] = 5000f;
    stats[ExtraStats.maxTargets] = 1f;
  }

  public static final class ExtraStats {

    public static final int duration = 10;
    public static final int maxTargets = 11;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
