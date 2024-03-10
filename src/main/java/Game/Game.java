package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

import general.Log;
import general.Log.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import windowStuff.Graphics;
import windowStuff.SpriteBatching;
import windowStuff.SuperBatch;
import windowStuff.UserInputHandler;
import windowStuff.UserInputListener;

public final class Game implements UserInputHandler {

  public static final int tickIntervalMillis = 1000 / 60;
  private final UserInputListener userInputListener;
  private final Graphics graphics;
  private final Map<String, SpriteBatching> bs = new HashMap<>(1);
  private final Collection<TickDetect> tickables = new ArrayList<>(1);
  private final Collection<TickDetect> newTickables = new ArrayList<>(1);
  private final Collection<KeyboardDetect> keyDetects = new ArrayList<>(1);
  private final Collection<KeyboardDetect> newKeyDetects = new ArrayList<>(1);
  private final Collection<MouseDetect> mouseDetects = new ArrayList<>(1);
  private final Collection<MouseDetect> newMouseDetects = new ArrayList<>(1);
  private final Log.Timer timer = new Timer();

  public int getTicks() {
    return ticks;
  }

  private int ticks = 0;
  private World world;
  private boolean paused = false;

  private Game() {
    userInputListener = new UserInputListener(this);
    graphics = new Graphics();
  }

  public static Game get() {
    return SingletonHolder.singleton;
  }

  public UserInputListener getUserInputListener() {
    return userInputListener;
  }

  public void init() {
    graphics.init();
    world = new World();
  }

  public void addTickable(TickDetect t) {
    newTickables.add(t);
  }

  public void addKeyDetect(KeyboardDetect t) {
    newKeyDetects.add(t);
  }

  public void addMouseDetect(MouseDetect t) {
    newMouseDetects.add(t);
  }

  public void tick() {
    userInputListener.handleEvents();
    tickables.addAll(newTickables);
    newTickables.clear();
    keyDetects.addAll(newKeyDetects);
    newKeyDetects.clear();
    mouseDetects.addAll(newMouseDetects);
    newMouseDetects.clear();

    if (paused) {
      return;
    }
    if (timer.elapsedNano(false) < tickIntervalMillis * 1000000) {
      return;
    }
    timer.elapsed(true);

    ticks++;
    if (ticks % 60 == 0) {
      Log.write(ticks + " in " + timer.saved + " ms");
      timer.saved = 0;
    }
    var iter = tickables.iterator();
    while (iter.hasNext()) {
      TickDetect t = iter.next();
      if (t.WasDeleted()) {
        iter.remove();
      } else {
        t.onGameTick(ticks);
      }
    }
    world.onGameTick(ticks);
    timer.saved += timer.elapsed(true);
  }

  public void graphicsUpdate(double dt) {
    graphics.redraw(dt);
    userInputListener.endFrame();
    if (paused) {
      world.showPauseMenu();
    }
  }

  public SpriteBatching getSpriteBatching(String name) {
    SpriteBatching r = bs.get(name);
    if (r != null) {
      return r;
    }

    SpriteBatching newBS = new SuperBatch();

    Log.write("creating new batch system: " + name);
    graphics.addSpriteBatching(newBS);
    bs.put(name, newBS);
    return newBS;
  }

  @Override
  public void onMouseMove(double newX, double newY) {
    var iter = mouseDetects.iterator();
    while (iter.hasNext()) {
      MouseDetect t = iter.next();
      if (t.WasDeleted()) {
        iter.remove();
      } else {
        t.onMouseMove((float) newX, (float) newY);
      }
    }
  }

  @Override
  public void onMouseButton(int button, int action, int mods) {
    var iter = mouseDetects.iterator();
    while (iter.hasNext()) {
      MouseDetect t = iter.next();
      if (t.WasDeleted()) {
        iter.remove();
      } else {
        t.onMouseButton(button, userInputListener.getX(), userInputListener.getY(), action, mods);
      }
    }
  }

  @Override
  public void onScroll(double xOffset, double yOffset) {
    var iter = mouseDetects.iterator();
    while (iter.hasNext()) {
      MouseDetect t = iter.next();
      if (t.WasDeleted()) {
        iter.remove();
      } else {
        t.onScroll(xOffset);
      }
    }
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    if (action == GLFW_REPEAT) {
      return;
    }
    if (action == GLFW_PRESS && key == GLFW_KEY_ESCAPE) {
      paused = !paused;
    }
    //Util.testBit(mods, GLFW_MOD_SHIFT)
    var iter = keyDetects.iterator();
    while (iter.hasNext()) {
      KeyboardDetect t = iter.next();
      if (t.WasDeleted()) {
        iter.remove();
      } else {
        t.onKeyPress(key, action, mods);
      }
    }
  }

  @SuppressWarnings("resource")
  public void setInputCallback(long window) {
    glfwSetCursorPosCallback(window, userInputListener::mousePosCallback);
    glfwSetScrollCallback(window, userInputListener::scrollCallback);
    glfwSetMouseButtonCallback(window, userInputListener::mouseButtonCallback);
    glfwSetKeyCallback(window, userInputListener::keyCallback);
  }

  private static final class SingletonHolder {

    private static final Game singleton = new Game();
  }
}
