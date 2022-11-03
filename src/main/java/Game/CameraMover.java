package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import windowStuff.Camera;
import windowStuff.UserInputListener;

public class CameraMover implements KeyboardDetect, TickDetect {

  private final Camera cam;
  private final UserInputListener input;
  private int x = 0, y = 0, movX = 0, movY = 0;

  public CameraMover(Game game) {
    game.addKeyDetect(this);
    game.addTickable(this);
    cam = game.getBatchSystem("main").getCamera();
    input = game.getUserInputListener();
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    movX = (input.isKeyPressed(GLFW_KEY_D) ? 1 : 0) - (input.isKeyPressed(GLFW_KEY_A) ? 1 : 0);
    movY = (input.isKeyPressed(GLFW_KEY_W) ? 1 : 0) - (input.isKeyPressed(GLFW_KEY_S) ? 1 : 0);
  }

  @Override
  public void onGameTick(int tick) {
    x += movX * 10;
    y += movY * 10;
    cam.move(movX * 10, movY * 10, 0);
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean WasDeleted() {
    return false;
  }
}
