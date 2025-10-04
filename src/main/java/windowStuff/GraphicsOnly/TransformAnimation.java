package windowStuff.GraphicsOnly;

import Game.Misc.Game;
import org.joml.Vector2f;
import windowStuff.GraphicsOnly.Sprite.Sprite;

public class TransformAnimation extends Sprite.Animation {

  private float scaling = 1;
  private float opacityScaling = 0;

  public TransformAnimation setSpinning(float spinning) {
    this.spinning = spinning;
    return this;
  }

  private float spinning = 0;

  private Vector2f linearScaling = new Vector2f(0, 0);
  private float duration;

  public TransformAnimation(float duration) {
    this.duration = duration;
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
    sprite.scale(scaling);
    sprite.setSize(2 * sprite.getWidth() + linearScaling.x,
        2 * sprite.getHeight() + linearScaling.y);
    float opac = Math.max(0, Math.min(1, sprite.getOpacity() + opacityScaling));
    sprite.setOpacity(opac);
    sprite.setRotation(sprite.getRotation() + spinning);
    duration -= Game.secondsPerFrame;
    if (duration < 0) {
      end(sprite);
    }
  }
}
