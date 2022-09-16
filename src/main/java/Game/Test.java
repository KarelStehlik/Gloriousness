package Game;

import java.util.ArrayList;
import java.util.List;
import windowStuff.Text;

public final class Test implements TickDetect {

  private static final int SIZE = 10;

  private final List<TestObject> objects = new ArrayList<>(SIZE);

  private final Game game;

  private final Text label;

  public Test(Game game) {
    game.addTickable(this);
    for (int i = 0; i < SIZE; i++) {
      objects.add(new TestObject(game));
    }
    this.game = game;
    label = new Text("the fox, wolf dog some shit", "Calibri",
        500, 100, 100, 5, 70, game.getBatchSystem("main"),
        "colorCycle");
    label.setColors(new float[]{
        1, 0, 0, 1,
        0, 1, 0, 1,
        0, 0, 1, 1,
        1, 0, 1, 1,
    });
  }

  @Override
  public void onGameTick(int tick) {
    if (tick % 200 == 0) {
      delete();
      new Test(game);
    }
    label.move(label.x+10, label.y);
  }

  @Override
  public void delete() {
    while (!objects.isEmpty()) {
      objects.remove(objects.size() - 1).delete();
    }
    label.delete();
  }

  @Override
  public boolean ShouldDeleteThis() {
    return objects.isEmpty();
  }
}
