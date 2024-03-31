package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import Game.Turrets.Turret.Stats;
import general.Util;
import windowStuff.Sprite;
import windowStuff.UserInputListener;

public class Player extends GameObject implements KeyboardDetect, TickDetect {

  private static final int HEIGHT = 200, WIDTH = 100;
  private static final float speed = 10;
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
        30, 30, 50, 30, 3, 100, stats[Stats.cd]);
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
    bulletLauncher.setSize(stats[Stats.projSize]);
    bulletLauncher.setSpeed(stats[Stats.projSpeed]);
    bulletLauncher.setPierce((int) stats[Stats.projPierce]);
    bulletLauncher.setDuration(stats[Stats.projDuration]);
    bulletLauncher.setCooldown(stats[Stats.cd]);
    bulletLauncher.setPower(stats[Stats.projPower]);
  }

  public void takeDamage(float amount, DamageType type) {
    float resistance = 1;
    healthPart -= amount * resistance / stats[Stats.health];
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



  // generated stats
  @Override
  public int getStatsCount(){return 8;}

  @Override
  public void clearStats() {
      stats[Stats.speed] = 1f;
      stats[Stats.health] = 100f;
      stats[Stats.cd] = 999f;
      stats[Stats.projSize] = 10f;
      stats[Stats.projSpeed] = 30f;
      stats[Stats.projPierce] = 100f;
      stats[Stats.projDuration] = 4f;
      stats[Stats.projPower] = 100f;
  }
  // end of generated stats

  public static final class Stats {

    public static final int speed = 0;
    public static final int health = 1;
    public static final int cd = 2;
    public static final int projSize = 3;
    public static final int projSpeed = 4;
    public static final int projPierce = 5;
    public static final int projDuration = 6;
    public static final int projPower = 7;
    private Stats() {
    }
  }
}
