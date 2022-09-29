package windowStuff;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import Game.Game;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public final class Window {

  private final long window;

  private final Game game;

  private volatile boolean running = false;

  private Window() {
    System.out.println("Running LWJGL ver.. " + Version.getVersion());

    GLFWErrorCallback.createPrint(System.err).set();
    if (!glfwInit()) {
      throw new IllegalStateException("LWJGL not initialized");
    }

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    String title = "hm";
    int height = 1080;
    int width = 1920;
    window = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);

    if (window == NULL) {
      throw new IllegalStateException("No win?");
    }

    glfwMakeContextCurrent(window);

    game = Game.get();

    game.setInputCallback(window);

    glfwSwapInterval(1);
  }

  public static Window get() {
    return SingletonHolder.singleton;
  }

  public void run() {
    running = true;
    glfwShowWindow(window);

    GL.createCapabilities();

    game.init();

    Thread gameThread = new Thread(this::gameLoop);

    gameThread.start();

    float dt = 0;
    float frameStartTime = System.nanoTime();
    while (!glfwWindowShouldClose(window)) {
      loop(dt);
      float frameEndTime = System.nanoTime();
      dt = frameEndTime - frameStartTime;
      frameStartTime = frameEndTime;
    }

    running = false;

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);
    glfwTerminate();
  }

  // dt is nanoseconds
  private void loop(float dt) {
    glfwPollEvents();

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    game.graphicsUpdate(dt / 1000000000);

    glfwSwapBuffers(window);
  }

  private void gameLoop() {
    while (running) {
      try {
        Thread.sleep(0, 1); // prevents synchronized game.tick from hogging the lock
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      game.tick();
    }
  }

  @Override
  public String toString() {
    return "Window{"
        + "glfwWindow=" + window
        + '}';
  }

  private static final class SingletonHolder {

    private static final Window singleton = new Window();
  }
}
