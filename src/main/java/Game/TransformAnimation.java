package Game;

import org.joml.Vector2f;
import windowStuff.Sprite;

public class TransformAnimation extends Sprite.Animation {

  private float scaling = 1;
  private float opacityScaling = 0;
  private int lastTick;

  public TransformAnimation setSpinning(float spinning) {
    this.spinning = spinning;
    return this;
  }

  private float spinning = 0;

  private Vector2f linearScaling = new Vector2f(0, 0);
  private float duration;

  public TransformAnimation(float duration) {
    this.duration = duration;
    lastTick = Game.get().getTicks();
  }

  public TransformAnimation setOpacityScaling(float opacityScaling) {
    this.opacityScaling = opacityScaling;
    return this;
  }

  public TransformAnimation setScaling(float value) {
    scaling = value;
    return this;
  }

  public TransformAnimation setLinearScaling(Vector2f value) {
    linearScaling = value;
    return this;
  }

  @Override
  public void update(Sprite sprite) {
    int ticks = Game.get().getTicks()-lastTick;
    if(ticks==0){
      return;
    }
    lastTick += ticks;
    sprite.scale((float) Math.pow(scaling, ticks));
    sprite.setSize(2 * sprite.getWidth() + linearScaling.x * ticks,
        2 * sprite.getHeight() + linearScaling.y * ticks);
    float opac = Math.max(0, Math.min(1, sprite.getOpacity() + opacityScaling * ticks));
    sprite.setOpacity(opac);
    sprite.setRotation(sprite.getRotation() + spinning * ticks);
    duration -= ticks * Game.tickIntervalMillis / 1000f;
    if (duration < 0) {
      end(sprite);
    }
  }
}
