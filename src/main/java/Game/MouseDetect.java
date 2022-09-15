package Game;

public interface MouseDetect {
  void onMouseButton(int button, double x, double y, int action, int mods);

  void onScroll(double scroll);

  void onMouseMove(double newX, double newY);

  void delete();

  boolean ShouldDeleteThis();
}
