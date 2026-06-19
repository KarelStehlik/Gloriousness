package Game.WorldStuff;

import Game.Misc.TickDetect;
import windowStuff.Controls.KeyboardDetect;
import windowStuff.Controls.MouseDetect;

public interface World extends TickDetect, MouseDetect, KeyboardDetect {

  void showPauseMenu();

  int getTick();
}
