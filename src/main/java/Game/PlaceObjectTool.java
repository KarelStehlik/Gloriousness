package Game;

import java.util.ArrayList;
import java.util.List;
import windowStuff.AbstractSprite;

public class PlaceObjectTool extends Tool {

  private final onClick click;
  private final List<AbstractSprite> sprites = new ArrayList<>(1);

  public PlaceObjectTool(World world, AbstractSprite sprite, onClick onclick) {
    super(world);
    click = onclick;
    sprites.add(sprite);
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
    sprites.forEach(s -> s.setPosition(newX, newY));
  }

  @Override
  public void onGameTick(int tick) {

  }

  public void addSprite(AbstractSprite s) {
    sprites.add(s);
  }

  @Override
  public void delete() {
    sprites.forEach(AbstractSprite::delete);
    super.delete();
  }

  @FunctionalInterface
  public interface onClick {

    boolean click(int x, int y); //returns false if the click didn't count
  }
}
