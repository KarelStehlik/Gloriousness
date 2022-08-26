package Game;

import windowStuff.Graphics;
import windowStuff.UserInputListener;

public class Game {

  private final UserInputListener userInput;

  private final Graphics graphics;

  public Game() {
    userInput = new UserInputListener();
    graphics = new Graphics();
  }

  public void init() {
    graphics.init();
  }

  public void tick() {
    userInput.endFrame();
  }

  public void graphicsUpdate(double dt) {
    graphics.redraw(dt);
  }

  public void onMouseMove(long window, double newX, double newY) {
    userInput.mousePosCallback(window, newX, newY);
  }

  public void onMouseButton(long window, int button, int action, int mods) {
    userInput.mouseButtonCallback(window, button, action, mods);
  }

  public void onScroll(long window, double xOffset, double yOffset) {
    userInput.scrollCallback(window, xOffset, yOffset);
  }

  public void onKeyPress(long window, int key, int scancode, int action, int mods) {
    userInput.keyCallback(window, key, scancode, action, mods);
  }
}
