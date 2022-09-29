package Game;

public interface KeyboardDetect {

  void onKeyPress(int key, int action, int mods);

  void delete();

  boolean WasDeleted();
}
