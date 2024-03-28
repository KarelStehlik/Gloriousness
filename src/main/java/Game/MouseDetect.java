package Game;

public interface MouseDetect {

  int getLayer();

  // return true if this blocked events to lower layers
  boolean onMouseButton(int button, double x, double y, int action, int mods);

  // return true if this blocked events to lower layers
  boolean onScroll(double scroll);

  // return true if this blocked events to lower layers
  boolean onMouseMove(float newX, float newY);

  void delete();

  boolean WasDeleted();
}
