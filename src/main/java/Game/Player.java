package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import general.RefFloat;
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
        30, 30, 50, 30, 3, 100, stats.cd.get());
    bulletLauncher.addMobCollide(
        BasicCollides.explode
    );
    bulletLauncher.setSpread(60);
    onStatsUpdate();
    buffHandler = new BuffHandler<>(this);
  }

  public boolean addBuff(Buff<Player> eff) {
    return buffHandler.add(eff);
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.setSize(stats.projSize.get());
    bulletLauncher.setSpeed(stats.projSpeed.get());
    bulletLauncher.setPierce((int) stats.projPierce.get());
    bulletLauncher.setDuration(stats.projDuration.get());
    bulletLauncher.setCooldown(stats.cd.get());
    bulletLauncher.setPower(stats.projPower.get());
  }


  public void takeDamage(float amount, DamageType type) {
    float resistance = 1;
    healthPart -= amount * resistance / stats.health.get();
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

    public RefFloat speed = new RefFloat(1);
    public RefFloat health = new RefFloat(100);
    public RefFloat cd = new RefFloat(999);
    public RefFloat projSize = new RefFloat(10);
    public RefFloat projSpeed = new RefFloat(30);
    public RefFloat projPierce = new RefFloat(100);
    public RefFloat projDuration = new RefFloat(4);
    public RefFloat projPower = new RefFloat(100);
    public ExtraStats() {
      init();
    }

    public void init() {
      speed = new RefFloat(1);
      health = new RefFloat(100);
      cd = new RefFloat(999);
      projSize = new RefFloat(10);
      projSpeed = new RefFloat(30);
      projPierce = new RefFloat(100);
      projDuration = new RefFloat(4);
      projPower = new RefFloat(100);
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
