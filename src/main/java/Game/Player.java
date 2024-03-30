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

public class Player extends GameObject implements KeyboardDetect, TickDetect {

  private static final int HEIGHT = 200, WIDTH = 100;
  private static final float speed = 10;
  public final float[] baseStats = new float[0];
  // generated stats
  public final float[] extraStats = new float[8];
  private final UserInputListener input;
  private final Sprite sprite;
  private final BulletLauncher bulletLauncher;
  private final BuffHandler<Player> buffHandler;
  protected double healthPart;
  private float vx, vy;

  public Player(World world) {
    super(0, 0, WIDTH, HEIGHT, world);
    clearStats();
    healthPart = 1;
    input = Game.get().getUserInputListener();
    sprite = new Sprite("Chestplates", WIDTH, HEIGHT, 2);
    sprite.setPosition(960, 540);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    Game.get().addKeyDetect(this);
    bulletLauncher = new BulletLauncher(world, "Egg", x, y, 20,
        30, 30, 50, 30, 3, 100, extraStats[ExtraStats.cd]);
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
    bulletLauncher.setSize(extraStats[ExtraStats.projSize]);
    bulletLauncher.setSpeed(extraStats[ExtraStats.projSpeed]);
    bulletLauncher.setPierce((int) extraStats[ExtraStats.projPierce]);
    bulletLauncher.setDuration(extraStats[ExtraStats.projDuration]);
    bulletLauncher.setCooldown(extraStats[ExtraStats.cd]);
    bulletLauncher.setPower(extraStats[ExtraStats.projPower]);
  }

  public void takeDamage(float amount, DamageType type) {
    float resistance = 1;
    healthPart -= amount * resistance / extraStats[ExtraStats.health];
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

  public void clearStats() {
    extraStats[ExtraStats.speed] = 1f;
    extraStats[ExtraStats.health] = 100f;
    extraStats[ExtraStats.cd] = 999f;
    extraStats[ExtraStats.projSize] = 10f;
    extraStats[ExtraStats.projSpeed] = 30f;
    extraStats[ExtraStats.projPierce] = 100f;
    extraStats[ExtraStats.projDuration] = 4f;
    extraStats[ExtraStats.projPower] = 100f;
  }

  public static final class ExtraStats {

    public static final int speed = 0;
    public static final int health = 1;
    public static final int cd = 2;
    public static final int projSize = 3;
    public static final int projSpeed = 4;
    public static final int projPierce = 5;
    public static final int projDuration = 6;
    public static final int projPower = 7;
    private ExtraStats() {
    }
  }
  // end of generated stats
}
