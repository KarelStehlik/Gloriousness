package Game.Common.Turrets;

import Game.Misc.Game;
import Game.Common.Projectile;
import Game.Misc.TdWorld;
import Game.Misc.TickDetect;
import windowStuff.GraphicsOnly.TransformAnimation;
import GlobalUse.Util.Cycle2Colors;
import org.joml.Vector2f;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;

public class DruidBall extends Projectile {

  private final float regrowTime;

  protected DruidBall(TdWorld world, ImageData image, float X, float Y, float speed, float rotation,
      int width, float aspectRatio, int pierce, float size, float duration, float power, float regrowTime) {
    super(world, image, X, Y, speed, rotation, width, 1, pierce, size, duration, power);
    this.regrowTime = regrowTime;
  }

  @Override
  public void delete() {
    super.delete();
    if (res != null) {
      res.sprite.delete();
    }
  }

  @Override
  public void onGameTick(int tick) {
    if (res != null && !res.WasDeleted()) {
      res.onGameTick(tick);
      return;
    }
    super.onGameTick(tick);
    sprite.setRotation(sprite.getRotation() - 8);
  }

  public void special(int i) {
    clearCollisions();
    res = new RespawningProjectile();
  }

  private RespawningProjectile res;

  class RespawningProjectile implements TickDetect {

    private final Sprite sprite;
    private float remainingTime;

    RespawningProjectile() {
      float size = getStats()[Projectile.Stats.size];

      remainingTime = regrowTime;

      float scaling = .015f / regrowTime;
      this.sprite =
          new Sprite(DruidBall.this.sprite).setSize(0, 0).setShader("colorCycle2").
              setOpacity(0.5f).addToBs(world.getBs()).
              setColors(new Cycle2Colors().setStrength(0.8f).setSpeed(5).get()
              ).playAnimation(new TransformAnimation(regrowTime)
              .setLinearScaling(new Vector2f(size * scaling, size * scaling))
              .setOpacityScaling(scaling)
              .setSpinning(-20f));
    }

    @Override
    public void onGameTick(int tick) {
      sprite.setHidden(false);
      remainingTime -= Game.secondsPerFrame;
      if (remainingTime<=0) {
        delete();
      }
    }

    @Override
    public void delete() {
      setActive(true);
    }

    @Override
    public boolean WasDeleted() {
      return sprite.isDeleted();
    }
  }
}
