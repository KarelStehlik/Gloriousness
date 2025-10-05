package Game.Common.Buffs.Buff;

import Game.Misc.GameObject;
import GlobalUse.Data;
import GlobalUse.Util;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.Sprite.SpriteBatching;
import windowStuff.GraphicsOnly.TransformAnimation;

public class Trail {

  public Trail(SpriteBatching batch, SpriteGenerator sprite, float interval, float spread) {
    this.batch = batch;
    this.sprite=sprite;
    this.interval = interval;
    this.spread = spread;
    lastX=0;
    lastY=0;
  }

  public Trail(Trail og, float x, float y){
    this.batch = og.batch;
    this.sprite=og.sprite;
    this.interval = og.interval;
    this.spread = og.spread;
    lastX=x;
    lastY=y;
  }

  private final SpriteBatching batch;
  private final float interval;
  private final float spread;
  private float lastX, lastY;
  private float distanceTraveled = 0;
  private final SpriteGenerator sprite;

  public void tick(GameObject object){
    float dx = object.getX()-lastX;
    float dy = object.getY()-lastY;
    float rotation = Util.get_rotation(dx, dy);
    float distance = Util.distanceNotSquared(dx,dy);
    float distanceRemaining=distance;
    float x = lastX, y=lastY;

    if(distanceRemaining + distanceTraveled > interval){
      x += dx/distance * (interval-distanceTraveled);
      y += dy/distance * (interval-distanceTraveled);
      spawnSprite(x,y,rotation);
      distanceRemaining -= interval-distanceTraveled;
      distanceTraveled=0;
      while(distanceRemaining>interval){
        x += dx/distance * interval;
        y += dy/distance * interval;
        spawnSprite(x,y,rotation);
        distanceRemaining-=interval;
      }
    }

    distanceTraveled+=distanceRemaining;
    lastX = object.getX();
    lastY = object.getY();
  }

  private void spawnSprite(float x,float y,float rotation){
    sprite.get(rotation).addToBs(batch).setPosition(x+spread*(Data.unstableRng.nextFloat()-0.5f),
        y+spread*(Data.unstableRng.nextFloat()-0.5f));
  }

  @FunctionalInterface
  public interface SpriteGenerator{
    Sprite get(float rotation);
  }
}
