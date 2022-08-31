package Game;

import java.util.ArrayList;
import java.util.List;

public class Test implements TickDetect {

  private static final int SIZE = 100000;

  private final List<TestObject> objects = new ArrayList<TestObject>(SIZE);

  private final Game game;

  public Test(Game game) {
    game.addTickable(this);
    for (int i = 0; i < SIZE; i++) {
      objects.add(new TestObject(game));
    }
    this.game = game;
  }

  @Override
  public void onGameTick(int tick) {
    if (tick % 200 == 0) {
      delete();
      new Test(game);
    }
  }

  @Override
  public void delete() {
    while (!objects.isEmpty()) {
      objects.remove(objects.size() - 1).delete();
    }
  }

  @Override
  public boolean ShouldDeleteThis() {
    return objects.isEmpty();
  }
}
