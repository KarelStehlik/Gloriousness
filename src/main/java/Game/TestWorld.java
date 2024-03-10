package Game;


import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public class TestWorld implements TickDetect, MouseDetect, KeyboardDetect {

  public TestWorld() {
    SpriteBatching bs = Game.get().getSpriteBatching("main");
    new Sprite("b", 4).addToBs(bs);
    new Sprite("Explosion1-0", 4).addToBs(bs);
    //new Sprite("b", 4).addToBs(bs);
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {

  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {

  }

  @Override
  public void onScroll(double scroll) {

  }

  @Override
  public void onMouseMove(float newX, float newY) {

  }

  @Override
  public void onGameTick(int tick) {

  }

  @Override
  public void delete() {

  }

  @Override
  public boolean WasDeleted() {
    return false;
  }

  public void showPauseMenu() {
  }
}
