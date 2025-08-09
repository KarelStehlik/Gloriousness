package Game.Turrets;

import Game.*;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.StatBuff;
import Game.Mobs.TdMob;
import general.Log;
import general.Util;
import windowStuff.Graphics;
import windowStuff.ImageData;
import windowStuff.Sprite;

import static Game.Buffs.StatBuff.Type.ADDED;

public class EngiTurret8 extends Turret {

  @Override
  protected ImageData getImage(){
    String img="turret";
    if(path2Tier>0){
      img="turret2";
    }
    if(path3Tier>2){
      img="tureet";
    }
    if(path1Tier>1){
      img="greed";
    }
    if(path1Tier>1){
      bulletLauncher.setImage("greedbolt");
    }else if(path3Tier>2){
      bulletLauncher.setImage("explodrt");
    }
    if(baseSprite!=null) {
      baseSprite.setImage(img + "Base");
    }
    return Graphics.getImage(img+"Head");
  }
    public Sprite baseSprite;
  public EngiTurret8(TdWorld world, int X, int Y, BulletLauncher templateLauncher) {
    super(world, X, Y,
            new BulletLauncher(templateLauncher));
    resaleValue=0;
    baseSprite = new Sprite("turretBase", 1).setSize(sprite.getWidth()*1.5f*2,
            sprite.getHeight()*0.8f*2);
    baseSprite.setPosition(sprite.getX(),sprite.getY()-baseSprite.getHeight());
    world.getBs().addSprite(baseSprite);
    baseSprite.setShader("basic");
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
  }
  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    addBuff(new StatBuff<Turret>(ADDED, EngiTurret8.ExtraStats.duration,-Game.tickIntervalMillis));
    if(getStats()[ExtraStats.duration]<=0){
      delete();
      return;
    }
    bulletLauncher.tickCooldown();
    TdMob target = target();
    if (target != null) {
      setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
      while (bulletLauncher.canAttack()) {
        bulletLauncher.attack(Util.get_rotation(target.getX() - x, target.getY() - y));
      }
    }

    buffHandler.tick();
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
  public void scale(float _width,float _height){
    sprite.scale(_width,_height);
    baseSprite.scale(_width,_height);
    baseSprite.setPosition(sprite.getX(),sprite.getY()-baseSprite.getHeight());
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
    stats[Stats.spritesize] = 25f;
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
