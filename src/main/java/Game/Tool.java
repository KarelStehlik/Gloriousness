package Game;

import windowStuff.UserInputListener;

public abstract class Tool implements MouseDetect, KeyboardDetect, TickDetect {

  protected final UserInputListener input;
  private boolean deleted = false;

  public Tool() {
    input = Game.get().getUserInputListener();
    Game.get().addKeyDetect(this);
    Game.get().addMouseDetect(this);
    Game.get().addTickable(this);
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
