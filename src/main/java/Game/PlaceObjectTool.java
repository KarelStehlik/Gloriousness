package Game;

import java.util.ArrayList;
import java.util.List;
import windowStuff.AbstractSprite;

public class PlaceObjectTool extends Tool {

  private final onClick click;
  private final List<AbstractSprite> sprites = new ArrayList<>(1);
  private onMove move;
  private voidf onDelete;

  public PlaceObjectTool(World world, AbstractSprite sprite, onClick onclick) {
    super(world);
    click = onclick;
    sprites.add(sprite);
    sprite.setPosition(input.getX(), input.getY());
  }

  public PlaceObjectTool setOnMove(onMove m) {
    move = m;
    return this;
  }

  public PlaceObjectTool setOnDelete(voidf d) {
    onDelete = d;
    return this;
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {

  }

  @Override
  public int getLayer() {
    return 0;
  }

  @Override
  public boolean onMouseButton(int button, double x, double y, int action, int mods) {
    if ((button == 0 && action == 1 && click.click((int) x, (int) y)) || (button == 1
        && action == 1)) {
      delete();
      return true;
    }
    return false;
  }

  @Override
  public boolean onScroll(double scroll) {
    return false;
  }

  @Override
  public boolean onMouseMove(float newX, float newY) {
    sprites.forEach(s -> s.setPosition(newX, newY));
    if (move != null) {
      move.move(newX, newY);
    }
    return false;
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
    if (onDelete != null) {
      onDelete.trigger();
    }
    super.delete();
  }

  @FunctionalInterface
  public interface onClick {

    boolean click(int x, int y); //returns false if the click didn't count
  }

  @FunctionalInterface
  public interface onMove {

    void move(float x, float y);
  }

  @FunctionalInterface
  public interface voidf {

    void trigger();
  }
}
