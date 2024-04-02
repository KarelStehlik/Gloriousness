package Game;

import org.joml.Vector2f;
import windowStuff.Sprite;

public class Animation implements TickDetect {

  public Sprite getSprite() {
    return sprite;
  }

  private final Sprite sprite;

  private float scaling = 1;
  private float opacityScaling = 0;

  public Animation setSpinning(float spinning) {
    this.spinning = spinning;
    return this;
  }

  private float spinning=0;

  private Vector2f linearScaling = new Vector2f(0, 0);
  private float duration;

  public Animation(Sprite anim, float duration) {
    sprite = anim;
    this.duration = duration;
  }

  public Animation setOpacityScaling(float opacityScaling) {
    this.opacityScaling = opacityScaling;
    return this;
  }

  public Animation setScaling(float value) {
    scaling = value;
    return this;
  }

  public Animation setLinearScaling(Vector2f value) {
    linearScaling = value;
    return this;
  }

  @Override
  public void onGameTick(int tick) {
    sprite.scale(scaling);
    sprite.setSize(2 * sprite.getWidth() + linearScaling.x,
        2 * sprite.getHeight() + linearScaling.y);
    float opac = Math.max(0, Math.min(1, sprite.getOpacity() + opacityScaling));
    sprite.setOpacity(opac);
    sprite.setRotation(sprite.getRotation()+spinning);
    duration -= Game.tickIntervalMillis / 1000f;
    if (duration < 0) {
      delete();
    }
  }

  @Override
  public void delete() {
    sprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return sprite.isDeleted();
  }

  @Override
  public String toString() {
    return "Animation{"
        + "sprite=" + sprite
        + '}';
  }
}
