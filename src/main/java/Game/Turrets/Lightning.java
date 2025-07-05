package Game.Turrets;

import Game.Enums.TargetingOption;
import Game.Game;
import Game.Projectile;
import Game.TdWorld;
import general.Data;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import windowStuff.ImageData;
import windowStuff.Sprite;
import Game.CallAfterDuration;

public class Lightning extends Projectile {

  private ImageData img;
  private List<Sprite> sprites = new ArrayList<>(5);
  private float lastStruckX, lastStruckY;

  // pierce == chains
  // speed == chaining range
  public Lightning(TdWorld world, ImageData image, float X, float Y, float speed,
      float rotation, int width, float aspectRatio, int pierce, float size, float duration,
      float power) {
    super(world, image, X, Y, speed, rotation, width, aspectRatio, pierce, size, duration, power);
    sprite.setHidden(true);
    img=image;
    lastStruckX = x;
    lastStruckY = y;
  }

  private void snapToEnemy(){
    targetedMob = world.getMobsGrid().search(new Point((int)x,(int)y), (int)stats[Stats.speed],
        TargetingOption.STRONG, mob->!(alreadyHitMobs.contains(mob) || mob.WasDeleted()));
    if(targetedMob == null){
      return;
    }
    move(targetedMob.getX(), targetedMob.getY());
    collide(targetedMob);
  }

  @Override
  public void move(float _x, float _y){
    x=_x;
    y=_y;
    Sprite s = new Sprite(img,sprite.getLayer());
    s.addToBs(world.getBs());
    s.setPosition((x+lastStruckX)/2, (y+lastStruckY)/2);
    s.setSize((float)Math.sqrt(Util.distanceSquared(x-lastStruckX, y-lastStruckY)), width);
    s.setRotation(Util.get_rotation(x-lastStruckX, y-lastStruckY));
    sprites.add(s);
    lastStruckX=x;
    lastStruckY=y;
  }

  @Override
  public void onGameTick(int tick) {
  }

  @Override
  public void onGameTickP2() {
    bh.tick();
    for(int i=0;i<5;i++){
      snapToEnemy();
      x+= (Data.gameMechanicsRng.nextFloat()-0.5f) * 80;
      y+= (Data.gameMechanicsRng.nextFloat()-0.5f) * 80;
      move(x,y);
    }
    for (var eff : beforeDeath) {
      eff.mod(this);
    }
    delete();
  }

  @Override
  public void delete() {
    super.delete();
    Game.get().addTickable(new CallAfterDuration(()->{
      for(var sprite : sprites){
        sprite.delete();
      }
    }, 100));
  }
}
