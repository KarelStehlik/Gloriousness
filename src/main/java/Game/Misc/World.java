package Game.Misc;

import windowStuff.Controls.KeyboardDetect;
import windowStuff.Controls.MouseDetect;

public interface World extends TickDetect, MouseDetect, KeyboardDetect {

  void showPauseMenu();

  int getTick();
}
