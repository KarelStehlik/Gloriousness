package Game;

import windowStuff.UserInputListener;

public abstract class Tool implements MouseDetect, KeyboardDetect, TickDetect {

  protected final UserInputListener input;
  private boolean deleted = false;

  public Tool(World world) {
    input = Game.get().getUserInputListener();
  }

  @Override
  public void delete() {
    deleted = true;
  }

  @Override
  public boolean WasDeleted() {
    return deleted;
  }
}
