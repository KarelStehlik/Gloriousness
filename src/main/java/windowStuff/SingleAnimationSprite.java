package windowStuff;

public class SingleAnimationSprite extends Sprite {

  private boolean ended = false;

  public SingleAnimationSprite(String anim, float duration, int layer) {
    this(anim, duration, layer, "basic");
  }

  public SingleAnimationSprite(String anim, float duration, int layer, String shader) {
    super(anim+"-0", layer, shader);
    playAnimation(new BasicAnimation(Graphics.getAnimation(anim), duration));
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
