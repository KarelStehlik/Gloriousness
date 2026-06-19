package Game.Misc;

import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import windowStuff.Controls.KeyboardDetect;
import windowStuff.Controls.MouseDetect;
import windowStuff.Controls.UserInputListener;

public abstract class Tool implements MouseDetect, KeyboardDetect, TickDetect {

  protected final UserInputListener input;
  private boolean deleted = false;

  public Tool(TdWorld world) {
    input = Game.get().getUserInputListener();
  }

  @Override
  public void delete() {
    deleted = true;
  }

  @Override
  public boolean wasDeleted() {
    return deleted;
  }
}
