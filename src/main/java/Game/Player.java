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
  private static final float speed = 10;
  private final UserInputListener input;
  private final Sprite sprite;
  private final BulletLauncher bulletLauncher;
  protected float health;
  private float vx, vy;

  public Player(World world) {
    super(0, 0, WIDTH, HEIGHT, world, Data.getEntityStats("mob", "Player"));
    health = stats.get("health");
    input = Game.get().getUserInputListener();
    sprite = new Sprite("Chestplates", WIDTH, HEIGHT, 2);
    sprite.setPosition(960, 540);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    Game.get().addKeyDetect(this);
    Game.get().addMouseDetect(this);
    bulletLauncher = new BulletLauncher(world, "Egg", x, y, 20,
        30, 30, 50, 30, 3, 100, stats.get("cd"));
    bulletLauncher.addMobCollide(
        (proj, target) -> {
          world.aoeDamage((int) proj.x, (int) proj.y, (int) proj.getPower(), proj.getPower(),
              DamageType.TRUE);
          world.explosionVisual(proj.x, proj.y, proj.getPower(), false, "Explosion2-0");
        }
    );
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
    if (vx != 0 && vy != 0) { // diagonal movement
      vx *= 0.7071067811865475f;
      vy *= 0.7071067811865475f;
    }
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean WasDeleted() {
    return false;
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
    bulletLauncher.tickCooldown();
    if (input.isMousePressed(0)) {
      float dist = (float) Math.hypot(input.getX() - x, input.getY() - y);
      while (bulletLauncher.canAttack()) {
        bulletLauncher.attack(Util.get_rotation(input.getX() - x, input.getY() - y)
            + Data.gameMechanicsRng.nextFloat() * 60 - 30);
      }
    }
    x = Math.max(width / 2f, Math.min(1920 - width / 2f, x + vx));
    y = Math.max(height / 2f, Math.min(1080 - height / 2f, y + vy));
    bulletLauncher.move(x, y);
    sprite.setPosition(x, y);
    //camera.moveTo(x-Constants.screenSize.x/2f,y-Constants.screenSize.y/2f,0);
  }
}
