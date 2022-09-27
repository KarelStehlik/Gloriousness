package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import general.Util;
import java.awt.Rectangle;
import windowStuff.Sprite;
import windowStuff.UserInputListener;

public class Player extends GameObject implements KeyboardDetect, MouseDetect, TickDetect {
  private Sprite sprite;
  private static final int HEIGHT = 200, WIDTH = 100;
  private float vx, vy;
  private float speed = 10;
  private final UserInputListener input;

  public Player(World world){
    super(0, 0, WIDTH, HEIGHT);
    input = world.game.getInputListener();
    sprite = new Sprite("Chestplates", WIDTH, HEIGHT, 10);
    sprite.setPosition(960, 540);
    sprite.setShader("colorCycle");
    sprite.setColors(Util.getCycleColors());
    world.bs.addSprite(sprite);
    world.addKeyDetect(this);
    world.addMouseDetect(this);
    world.addTickable(this);
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    vx = (input.isKeyPressed(GLFW_KEY_D)?speed:0) - (input.isKeyPressed(GLFW_KEY_A)?speed:0);
    vy = (input.isKeyPressed(GLFW_KEY_W)?speed:0) - (input.isKeyPressed(GLFW_KEY_S)?speed:0);
    if(vx!=0 && vy!=0){
      vx*=0.7071067811865475f;
      vy*=0.7071067811865475f;
    }
  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {
  }

  @Override
  public void onScroll(double scroll) {

  }

  @Override
  public void onMouseMove(float newX, float newY) {
  }

  @Override
  public void onGameTick(int tick) {
    x = x + vx;
    y = y + vy;
    sprite.setPosition(x, y);
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean ShouldDeleteThis() {
    return false;
  }

  @Override
  public Rectangle getHitbox() {
    return new Rectangle( (int) x - WIDTH / 2, (int) y + HEIGHT / 2, WIDTH, HEIGHT);
  }
}
