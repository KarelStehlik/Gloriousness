package Game;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import windowStuff.BatchSystem;
import windowStuff.Graphics;
import windowStuff.UserInputHandler;
import windowStuff.UserInputListener;

public final class Game implements UserInputHandler {

  private static final int tickInterval = 1000 / 60;
  private final UserInputListener userInput;
  private final Graphics graphics;
  private final Map<String, BatchSystem> bs = new HashMap<>(1);
  private final Collection<TickDetect> tickables = new LinkedList<>();
  private final Collection<TickDetect> newTickables = new LinkedList<>();
  private final Collection<KeyboardDetect> keyDetects = new LinkedList<>();
  private final Collection<KeyboardDetect> newKeyDetects = new LinkedList<>();
  private final Collection<MouseDetect> mouseDetects = new LinkedList<>();
  private final Collection<MouseDetect> newMouseDetects = new LinkedList<>();
  private long startTime = System.currentTimeMillis();
  private int ticks = 0;

  public Game() {
    userInput = new UserInputListener(this);
    graphics = new Graphics();
  }

  public void init() {
    graphics.init();
    startTime = System.currentTimeMillis();
    new Test(this);
  }

  public void addTickable(TickDetect t) {
    newTickables.add(t);
  }

  public void addKeyDetect(KeyboardDetect t) {
    newKeyDetects.add(t);
  }

  public void tick() {
    long timeTillTick = startTime + (long) tickInterval * ticks - System.currentTimeMillis();
    if (timeTillTick > 0) {
      return;
    }
    ticks++;
    if (ticks % 60 == 0) {
      System.out.println(ticks);
      //System.out.println(tickables.size());
    }
    var iter = tickables.iterator();
    while (iter.hasNext()) {
      TickDetect t = iter.next();
      if (t.ShouldDeleteThis()) {
        iter.remove();
      } else {
        t.onGameTick(ticks);
      }
    }
    userInput.handleEvents();
    tickables.addAll(newTickables);
    newTickables.clear();
    keyDetects.addAll(newKeyDetects);
    newKeyDetects.clear();
    mouseDetects.addAll(newMouseDetects);
    newMouseDetects.clear();
  }

  public void graphicsUpdate(double dt) {
    graphics.redraw(dt);
    userInput.endFrame();
  }

  public BatchSystem getBatchSystem(String name) {
    BatchSystem r = bs.get(name);
    if (r != null) {
      return r;
    }
    BatchSystem newBS = new BatchSystem();
    System.out.println("creating new batch system: " + name);
    graphics.addBatchSystem(newBS);
    bs.put(name, newBS);
    return newBS;
  }

  @Override
  public void onMouseMove(double newX, double newY) {
    var iter = mouseDetects.iterator();
    while (iter.hasNext()) {
      MouseDetect t = iter.next();
      if (t.ShouldDeleteThis()) {
        iter.remove();
      } else {
        t.onMouseMove(newX, newY);
      }
    }
  }

  @Override
  public void onMouseButton(int button, int action, int mods) {
    var iter = mouseDetects.iterator();
    while (iter.hasNext()) {
      MouseDetect t = iter.next();
      if (t.ShouldDeleteThis()) {
        iter.remove();
      } else {
        t.onMouseButton(button, userInput.getX(), userInput.getY(), action, mods);
      }
    }
  }

  @Override
  public void onScroll(double xOffset, double yOffset) {
    var iter = mouseDetects.iterator();
    while (iter.hasNext()) {
      MouseDetect t = iter.next();
      if (t.ShouldDeleteThis()) {
        iter.remove();
      } else {
        t.onScroll(xOffset);
      }
    }
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    //Util.testBit(mods, GLFW_MOD_SHIFT)
    var iter = keyDetects.iterator();
    while (iter.hasNext()) {
      KeyboardDetect t = iter.next();
      if (t.ShouldDeleteThis()) {
        iter.remove();
      } else {
        t.onKeyPress(key, action, mods);
      }
    }
  }

  @SuppressWarnings("resource")
  public void setInputCallback(long window) {
    glfwSetCursorPosCallback(window, userInput::mousePosCallback);
    glfwSetScrollCallback(window, userInput::scrollCallback);
    glfwSetMouseButtonCallback(window, userInput::mouseButtonCallback);
    glfwSetKeyCallback(window, userInput::keyCallback);
  }
}
