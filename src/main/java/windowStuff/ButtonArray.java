package windowStuff;

import Game.Game;
import Game.MouseDetect;
import Game.TickDetect;

public class ButtonArray implements MouseDetect, TickDetect {

  private final int columns;
  private final Button[] buttons;
  private final AbstractSprite background;
  private final int buttonSize;
  private final int buttonSpacing;
  boolean shown = true;

  public ButtonArray(int columns, Button[] buttons, AbstractSprite background, int buttonSize,
      int x, int y, int buttonSpacing, int anchorX, int anchorY) {
    this.columns = columns;
    this.buttons = buttons;
    this.background = background;
    this.buttonSize = buttonSize;
    this.buttonSpacing = buttonSpacing;
    move(x, y, anchorX, anchorY);
  }

  private void move(int x, int y, int anchorX, int anchorY) {
    for (Button b : buttons) {
      b.getSprite().setSize(buttonSize, buttonSize);
    }

    int rows = (int) Math.ceil(buttons.length / (float) columns);
    background.setSize(columns * buttonSize + (columns + 1) * buttonSpacing,
        rows * buttonSize + (rows + 1) * buttonSpacing);
    int centreX = (int) (x + background.getWidth() * (1 - 2 * anchorX));
    int centreY = (int) (y + background.getHeight() * (1 - 2 * anchorY));
    background.setPosition(centreX, centreY);

    for (int i = 0; i < buttons.length; i++) {
      int column = i % columns;
      int row = (i - column) / columns;
      buttons[i].getSprite().setPosition(
          centreX + column * (buttonSpacing + buttonSize) - background.getWidth() + buttonSize / 2f
              + buttonSpacing,
          centreY + background.getHeight() - (row * (buttonSpacing + buttonSize) + buttonSize / 2f
              + buttonSpacing));
    }
  }

  @Override
  public int getLayer() {
    return background.getLayer();
  }

  @Override
  public boolean onMouseButton(int button, double x, double y, int action, int mods) {
    if (!shown) {
      return false;
    }
    if (x > background.getX() - background.getWidth()
        && x < background.getX() + background.getWidth() &&
        y > background.getY() - background.getHeight()
        && y < background.getY() + background.getHeight()) {
      for (Button b : buttons) {
        b.onMouseButton(button, x, y, action, mods);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean onScroll(double scroll) {
    float x = Game.get().getUserInputListener().getX();
    float y = Game.get().getUserInputListener().getX();
    return x > background.getX() - background.getWidth()
        && x < background.getX() + background.getWidth() &&
        y > background.getY() - background.getHeight()
        && y < background.getY() + background.getHeight();
  }

  @Override
  public boolean onMouseMove(float newX, float newY) {
    if (!shown) {
      return false;
    }
    for (Button b : buttons) {
      b.onMouseMove(newX, newY);
    }
    return newX > background.getX() - background.getWidth()
        && newX < background.getX() + background.getWidth() &&
        newY > background.getY() - background.getHeight()
        && newY < background.getY() + background.getHeight();
  }

  @Override
  public void delete() {
    background.delete();
    for (Button b : buttons) {
      b.delete();
    }
  }

  @Override
  public boolean WasDeleted() {
    return background.isDeleted();
  }

  @Override
  public void onGameTick(int tick) {
    if (!shown) {
      return;
    }
    for (Button b : buttons) {
      b.onGameTick(tick);
    }
  }
}
