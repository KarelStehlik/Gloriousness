package Game;

import windowStuff.Sprite;

public class PlaceObjectTool extends Tool {

  private final onClick click;
  private final Sprite sprite;

  public PlaceObjectTool(World world, Sprite sprite, onClick onclick) {
    super(world);
    click = onclick;
    this.sprite = sprite;
    sprite.setPosition(input.getX(), input.getY());
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {

  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {
    if (button == 0 && action == 1 && click.click((int) x, (int) y)) {
      delete();
    } else if (button == 1 && action == 1) {
      delete();
    }
  }

  @Override
  public void onScroll(double scroll) {

  }

  @Override
  public void onMouseMove(float newX, float newY) {
    sprite.setPosition(newX, newY);
  }

  @Override
  public void onGameTick(int tick) {

  }

  @Override
  public void delete() {
    sprite.delete();
    super.delete();
  }

  @FunctionalInterface
  public interface onClick {

    boolean click(int x, int y); //returns false if the click didn't count
  }
}
