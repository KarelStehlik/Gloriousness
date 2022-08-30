package Game;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import windowStuff.BatchSystem;
import windowStuff.Graphics;
import windowStuff.UserInputHandler;
import windowStuff.UserInputListener;

public class Game implements UserInputHandler {

  private static final int tickInterval = 1000 / 60;
  private final UserInputListener userInput;
  private final Graphics graphics;
  private final List<TickDetect> tickables = new ArrayList<TickDetect>(1000);
  private final Map<String, BatchSystem> bs = new HashMap<>(1);
  private long startTime = System.currentTimeMillis();
  private int ticks = 0;

  public Game() {
    userInput = new UserInputListener(this);
    graphics = new Graphics();
  }

  public void init() {
    graphics.init();
    startTime = System.currentTimeMillis();
    for (int i = 0; i < 1000000; i++) {
      new Test(this);
    }
  }

  public void addTickable(TickDetect t) {
    tickables.add(t);
  }

  public synchronized void tick() {
    long timeTillTick = startTime + (long) tickInterval * ticks - System.currentTimeMillis();
    if (timeTillTick > 0) {
      return;
    }
    ticks++;
    if (ticks % 60 == 0) {
      System.out.println(ticks);
    }
    for (TickDetect t : tickables) {
      t.onGameTick();
    }

    if (ticks % 200 == 0) {
      while (!tickables.isEmpty()) {
        tickables.remove(tickables.size() - 1).delete();
      }
    }
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
    r = new BatchSystem();
    graphics.addBatchSystem(r);
    bs.put(name, r);
    return r;
  }

  @Override
  public synchronized void onMouseMove(double newX, double newY) {
  }

  @Override
  public synchronized void onMouseButton(int button, int action, int mods) {
  }

  @Override
  public synchronized void onScroll(double xOffset, double yOffset) {
  }

  @Override
  public synchronized void onKeyPress(int key, int action, int mods) {
  }

  @SuppressWarnings("resource")
  public void setInputCallback(long window) {
    glfwSetCursorPosCallback(window, userInput::mousePosCallback);
    glfwSetScrollCallback(window, userInput::scrollCallback);
    glfwSetMouseButtonCallback(window, userInput::mouseButtonCallback);
    glfwSetKeyCallback(window, userInput::keyCallback);
  }
}
