package Game;

public interface World extends TickDetect, MouseDetect, KeyboardDetect {

  void showPauseMenu();

  int getTick();
}
