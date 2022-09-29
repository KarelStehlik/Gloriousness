package Game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import windowStuff.Text;

public final class Test implements TickDetect {

  private static final int SIZE = 30;

  private final List<TestObject> objects = new ArrayList<>(SIZE);

  private final Game game;

  private final Text label;

  private final SquareGrid<TestObject> grid;

  public Test(Game game) {
    grid = new SquareGrid<TestObject>(-200, -200, 1920 + 400, 1080+400);
    game.addTickable(this);
    for (int i = 0; i < SIZE; i++) {
      objects.add(new TestObject(game, grid));
    }
    this.game = game;
    label = new Text("the fox, wolf dog some shit", "Calibri",
        500, 100, 100, 5, 70, game.getBatchSystem("main"),
        "colorCycle");
    label.setColors(new float[]{
        3, 0, 0, 1,
        0, 3, 0, 1,
        0, 0, 3, 1,
        1.5f, 0, 1.5f, 1,
    });
  }

  @Override
  public void onGameTick(int tick) {
    label.move(label.x+1, label.y);
    label.setText(String.valueOf(tick*tick*tick));
    grid.clear();
  }

  @Override
  public void delete() {
    while (!objects.isEmpty()) {
      objects.remove(objects.size() - 1).delete();
    }
    label.delete();
  }

  @Override
  public boolean WasDeleted() {
    return objects.isEmpty();
  }
}
