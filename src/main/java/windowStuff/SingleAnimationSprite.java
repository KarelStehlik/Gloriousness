package windowStuff;

import java.util.List;

public class SingleAnimationSprite extends Sprite {

  private boolean ended = false;

  public SingleAnimationSprite(String anim, float duration, int layer) {
    this(anim, duration, layer, "basic");
  }

  public SingleAnimationSprite(String anim, float duration, int layer, String shader) {
    super(anim + "-0", layer, shader);
    playAnimation(new FrameAnimation(Graphics.getAnimation(anim), duration));
  }

  public SingleAnimationSprite(List<ImageData> anim, float duration, int layer) {
    this(anim, duration, layer, "basic");
  }

  public SingleAnimationSprite(List<ImageData> anim, float duration, int layer, String shader) {
    super(anim.get(0), layer, shader);
    playAnimation(new FrameAnimation(anim, duration));
  }

  public boolean animationEnded() {
    return ended;
  }

  @Override
  protected void onAnimationEnd() {
    ended = true;
    delete();
  }
}
