package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import general.Data;
import general.Util;
import java.util.HashMap;
import java.util.Map;
import windowStuff.Sprite;
import windowStuff.UserInputListener;

public class Player extends GameObject implements KeyboardDetect, MouseDetect, TickDetect {

  private static final int HEIGHT = 200, WIDTH = 100;
  final Map<String, Float> stats;
  final Map<String, Float> baseStats;
  private final UserInputListener input;
  protected float health;
  private final Sprite sprite;
  private float vx, vy;
  private final float speed = 10;

  public Player(World world) {
    super(0, 0, WIDTH, HEIGHT, world);
    baseStats = Data.getEntityStats("mob", "Player");
    stats = new HashMap<>(baseStats);
    health = stats.get("health");
    input = Game.get().getInputListener();
    sprite = new Sprite("Chestplates", WIDTH, HEIGHT, 10);
    sprite.setPosition(960, 540);
    sprite.setShader("colorCycle");
    sprite.setColors(Util.getCycleColors());
    world.getBs().addSprite(sprite);
    Game.get().addKeyDetect(this);
    Game.get().addMouseDetect(this);
  }

  public void takeDamage(float amount, DamageType type) {
    float resistance = stats.getOrDefault(type.resistanceName, 1f);
    health -= amount * resistance;
    if (health < 0) {
      world.endGame();
    }
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    vx =
        (input.isKeyPressed(GLFW_KEY_D) ? speed : 0) - (input.isKeyPressed(GLFW_KEY_A) ? speed : 0);
    vy =
        (input.isKeyPressed(GLFW_KEY_W) ? speed : 0) - (input.isKeyPressed(GLFW_KEY_S) ? speed : 0);
    if (vx != 0 && vy != 0) {
      vx *= 0.7071067811865475f;
      vy *= 0.7071067811865475f;
    }
  }

  @Override
  public void onMouseButton(int button, double _x, double _y, int action, int mods) {
  }

  @Override
  public void onScroll(double scroll) {

  }

  @Override
  public void onMouseMove(float newX, float newY) {
  }

  @Override
  public void onGameTick(int tick) {
    if (input.isMousePressed(0)) {
      new BasicDamageProjectile(world, "faura", x, y, 20, Util.get_rotation(input.getX() - x,
          input.getY() - y) + Data.gameMechanicsRng.nextFloat() * 60 - 30, 50, 50, 20, 50, 2, true,
          false, 100);
    }
    x = Math.max(width / 2f, Math.min(1920 - width / 2f, x + vx));
    y = Math.max(height / 2f, Math.min(1080 - height / 2f, y + vy));
    sprite.setPosition(x, y);
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean WasDeleted() {
    return false;
  }
}
