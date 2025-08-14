package Game.Turrets;

import Game.Game;
import Game.TransformAnimation;
import Game.Projectile;
import Game.TdWorld;
import Game.TickDetect;
import general.Util;
import org.joml.Vector2f;
import windowStuff.ImageData;
import windowStuff.Sprite;

public class DruidBall extends Projectile {

  private final float regrowTime;

  protected DruidBall(TdWorld world, ImageData image, float X, float Y, float speed, float rotation,
      int W, int pierce, float size, float duration, float power, float regrowTime) {
    super(world, image, X, Y, speed, rotation, W, 1, pierce, size, duration, power);
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

      float scaling = .015f / regrowTime;
      remainingTime = regrowTime;

      this.sprite =
          new Sprite(DruidBall.this.sprite).setSize(0, 0).setShader("colorCycle2").
              setOpacity(0.5f).addToBs(world.getBs()).
              setColors(Util.getCycle2colors(1f)
              ).playAnimation(new TransformAnimation(regrowTime)
              .setLinearScaling(new Vector2f(size * scaling, size * scaling))
              .setOpacityScaling(scaling)
              .setSpinning(-20f));
    }

    @Override
    public void onGameTick(int tick) {
      sprite.setHidden(false);
      remainingTime -= Game.tickIntervalMillis / 1000f;
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
