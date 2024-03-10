package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import general.Util;
import windowStuff.Sprite;
import windowStuff.UserInputListener;

public class Player extends GameObject implements KeyboardDetect, MouseDetect, TickDetect {

  private static final int HEIGHT = 200, WIDTH = 100;
  private static final float speed = 10;
  public final ExtraStats stats;
  private final UserInputListener input;
  private final Sprite sprite;
  private final BulletLauncher bulletLauncher;
  private final BuffHandler<Player> buffHandler;
  protected double healthPart;
  private float vx, vy;

  public Player(World world) {
    super(0, 0, WIDTH, HEIGHT, world);
    stats = new ExtraStats();
    healthPart = 1;
    input = Game.get().getUserInputListener();
    sprite = new Sprite("Chestplates", WIDTH, HEIGHT, 2);
    sprite.setPosition(960, 540);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    Game.get().addKeyDetect(this);
    Game.get().addMouseDetect(this);
    bulletLauncher = new BulletLauncher(world, "Egg", x, y, 20,
        30, 30, 50, 30, 3, 100, stats.cd);
    bulletLauncher.addMobCollide(
        BasicCollides.explode
    );
    bulletLauncher.setSpread(60);
    onStatsUpdate();
    buffHandler = new BuffHandler<>(this);
  }

  public void addBuff(Buff<Player> eff) {
    buffHandler.add(eff);
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.setSize(stats.projSize);
    bulletLauncher.setSpeed(stats.projSpeed);
    bulletLauncher.setPierce((int) stats.projPierce);
    bulletLauncher.setDuration(stats.projDuration);
    bulletLauncher.setCooldown(stats.cd);
    bulletLauncher.setPower(stats.projPower);
  }

  @Override
  public void clearStats() {
    stats.init();
  }

  public void takeDamage(float amount, DamageType type) {
    float resistance = 1;
    healthPart -= amount * resistance / stats.health;
    if (healthPart < 0) {
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
        bulletLauncher.attack(Util.get_rotation(input.getX() - x, input.getY() - y));
      }
    }
    x = Math.max(width / 2f, Math.min(1920 - width / 2f, x + vx));
    y = Math.max(height / 2f, Math.min(1080 - height / 2f, y + vy));
    bulletLauncher.move(x, y);
    sprite.setPosition(x, y);
  }

  public static class BaseStats {

    public BaseStats() {
      init();
    }

    public void init() {
    }
  }


  // generated stats
  public static final class ExtraStats {

    public float speed = 1f;
    public float health = 100f;
    public float cd = 999f;
    public float projSize = 10f;
    public float projSpeed = 30f;
    public float projPierce = 100f;
    public float projDuration = .81f;
    public float projPower = 100f;

    public ExtraStats() {
      init();
    }

    public void init() {
      speed = 1f;
      health = 100f;
      cd = 999f;
      projSize = 10f;
      projSpeed = 30f;
      projPierce = 100f;
      projDuration = .81f;
      projPower = 100f;
    }
  }

  public static final class Stats extends BaseStats {

    public Stats() {
      init();
    }

    @Override
    public void init() {

    }
  }
  // end of generated stats
}
