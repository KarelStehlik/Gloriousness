package windowStuff;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import general.Constants;
import imgui.ImGui;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class UserInputListener {

  private final boolean[] keysPressed;
  private final boolean[] buttonsPressed;
  private final UserInputHandler game;
  private final BlockingQueue<Event> events = new LinkedBlockingQueue<>();
  private double x, y, dx, dy, lastX, lastY, scrollX, scrollY;
  private boolean dragging;

  public UserInputListener(UserInputHandler g) {
    x = 0;
    y = 0;
    dx = 0;
    dy = 0;
    lastX = 0;
    lastY = 0;
    scrollX = 0;
    scrollY = 0;
    dragging = false;
    keysPressed = new boolean[350];
    buttonsPressed = new boolean[5];
    game = g;
  }

  public void handleEvents() {
    while (!events.isEmpty()) {
      events.remove().run();
    }
  }

  public float getX() {
    return (float) x;
  }

  public float getY() {
    return (float) y;
  }

  public float getDx() {
    return (float) dx;
  }

  public float getDy() {
    return (float) dy;
  }

  public float getScrollX() {
    return (float) scrollX;
  }

  public float getScrollY() {
    return (float) scrollY;
  }

  public boolean isDragging() {
    return dragging;
  }

  public boolean isKeyPressed(int button) {
    return keysPressed[button];
  }

  public void mousePosCallback(long window, double newX, double newY) {
    lastX = x;
    lastY = y;
    x = newX;
    y = Constants.screenSize.y - newY;
    dx = newX - lastX;
    dy = Constants.screenSize.y - newY - lastY;
    dragging = Arrays.asList(buttonsPressed, 5).contains(true);
    if (game != null) {
      events.add(() -> game.onMouseMove(newX, Constants.screenSize.y - newY));
    }
  }

  public void mouseButtonCallback(long window, int button, int action, int mods) {
    if (button > buttonsPressed.length || ImGui.getIO().getWantCaptureMouse()) {
      return;
    }
    if (action == GLFW_PRESS) {
      buttonsPressed[button] = true;
    } else if (action == GLFW_RELEASE) {
      buttonsPressed[button] = false;
      dragging = false;
    }
    if (game != null) {
      events.add(() -> game.onMouseButton(button, action, mods));
    }
  }

  public void scrollCallback(long window, double xOffset, double yOffset) {
    scrollX = xOffset;
    scrollY = yOffset;
    if (game != null) {
      events.add(() -> game.onScroll(xOffset, yOffset));
    }
  }

  public void endFrame() {
    scrollX = 0;
    scrollY = 0;
    dx = 0;
    dy = 0;
  }

  public void keyCallback(long window, int key, int scancode, int action, int mods) {
    if (key == -1) { // idk why this happens, but it does
      return;
    }
    keysPressed[key] = action >= GLFW_PRESS;
    if (game != null) {
      events.add(() -> game.onKeyPress(key, action, mods));
    }
  }

  public boolean isMousePressed(int key) {
    return buttonsPressed[key];
  }

  @Override
  public String toString() {
    return "UserInputListener{"
        + "buttonsPressed=" + Arrays.toString(buttonsPressed)
        + '}';
  }

  @FunctionalInterface
  private interface Event {

    void run();
  }
}
