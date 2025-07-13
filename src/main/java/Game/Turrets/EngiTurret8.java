package Game.Turrets;

import Game.Ability;
import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.Tag;
import Game.BulletLauncher;
import Game.Mobs.TdMob;
import Game.Projectile;
import Game.TdWorld;
import general.Constants;
import general.Data;
import general.Description;
import general.Util;
import windowStuff.Sprite;

public class EngiTurret8 extends Turret {

    public static final String image = "turret"; //never used
    public Sprite baseSprite;
  public EngiTurret8(TdWorld world, int X, int Y, BulletLauncher templateLauncher,String img) {
    super(world, X, Y, img+"Head",
            new BulletLauncher(templateLauncher));
    baseSprite = new Sprite(img+"Base", 1).setSize(stats[Turret.Stats.spritesize]*0.75f,
            stats[Turret.Stats.spritesize]*0.75f);
    baseSprite.setPosition(x, y);
    baseSprite.setShader("basic");
    sprite.setNaturalWidth();
    sprite.setY(sprite.getY()+baseSprite.getHeight()/2+sprite.getHeight()/2);
    world.getBs().addSprite(baseSprite);

    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    addBuff(
            new DelayedTrigger<Turret>(stats[EngiTurret.ExtraStats.duration], Turret::delete, false));
  }
  @Override
  public void delete() {
    sprite.delete();
    baseSprite.delete();
    buffHandler.delete();
    rangeDisplay.delete();
  }

  @Override
  public boolean WasDeleted() {
    return sprite.isDeleted()&&baseSprite.isDeleted();
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
    stats[Stats.aspd] = 1f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = 1f;
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
