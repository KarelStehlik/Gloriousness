package Game.Turrets;

import Game.Animation;
import Game.Projectile;
import Game.TickDetect;
import Game.World;
import general.Util;
import org.joml.Vector2f;
import windowStuff.Sprite;

public class DruidBall extends Projectile {

  private final float regrowTime;

  protected DruidBall(World world, String image, float X, float Y, float speed, float rotation,
      int W, int H, int pierce, float size, float duration, float power, float regrowTime) {
    super(world, image, X, Y, speed, rotation, W, H, pierce, size, duration, power);
    this.regrowTime = regrowTime;
  }

  @Override
  public void setRotation(float rotation) {
    super.setRotation(rotation);
    vx = Util.cos(rotation) * stats[Stats.speed];
    vy = Util.sin(rotation) * stats[Stats.speed];
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

  @Override
  public void special(int i) {
    clearCollisions();
    res = new RespawningProjectile();
  }

  private RespawningProjectile res;

  class RespawningProjectile implements TickDetect {

    private final Animation sprite;

    RespawningProjectile() {
      float size = getStats()[Projectile.Stats.size];

      float scaling = .015f / regrowTime;
      this.sprite = new Animation(
          new Sprite(DruidBall.this.sprite).setSize(0, 0).setShader("colorCycle2").
              setOpacity(0.5f).addToBs(world.getBs()).
              setColors(Util.getCycle2colors(1f)
              )
          , regrowTime
      ).setLinearScaling(new Vector2f(size * scaling, size * scaling)).setOpacityScaling(scaling)
          .setSpinning(-20f);
    }

    @Override
    public void onGameTick(int tick) {
      sprite.onGameTick(tick);
      sprite.getSprite().setHidden(false);
      if (sprite.WasDeleted()) {
        delete();
      }
    }

    @Override
    public void delete() {
      setActive(true);
    }

    @Override
    public boolean WasDeleted() {
      return sprite.WasDeleted();
    }
  }
}
