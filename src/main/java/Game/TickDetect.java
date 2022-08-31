package Game;

public interface TickDetect {

  void onGameTick(int tick);

  void delete();

  boolean ShouldDeleteThis();
}
