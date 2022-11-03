package Game;

import windowStuff.BatchSystem;
import windowStuff.SingleAnimationSprite;

public class Animation implements TickDetect {

  private final SingleAnimationSprite sprite;

  public Animation(String anim, BatchSystem bs, float duration, int x, int y, float width,
      float height, int layer) {
    sprite = new SingleAnimationSprite(anim, duration, x, y, width, height, layer, "basic");
    bs.addSprite(sprite);
  }

  @Override
  public void onGameTick(int tick) {
  }

  @Override
  public void delete() {
  }

  @Override
  public boolean WasDeleted() {
    return sprite.animationEnded();
  }
}
