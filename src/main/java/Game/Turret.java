package Game;

import general.Util;
import java.awt.Point;
import windowStuff.Sprite;

public class Turret extends GameObject implements TickDetect {

  public static final int HEIGHT = 100, WIDTH = 100;
  public final BaseStats baseStats;
  protected final BulletLauncher bulletLauncher;
  private final Sprite sprite;
  protected float health;
  private float vx, vy;
  protected Turret(World world, int X, int Y, String imageName, BulletLauncher launcher,
      BaseStats newStats) {
    super(X, Y, WIDTH, HEIGHT, world);
    sprite = new Sprite(imageName, WIDTH, HEIGHT, 2);
    sprite.setPosition(x, y);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    bulletLauncher = launcher;
    launcher.move(x, y);
    Game.get().addTickable(this);
    onStatsUpdate();
    baseStats = newStats;
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.setDuration(baseStats.projectileDuration);
    bulletLauncher.setPierce((int) baseStats.pierce);
    bulletLauncher.setPower(baseStats.power);
    bulletLauncher.setSize(baseStats.bulletSize);
    bulletLauncher.setSpeed(baseStats.speed);
    bulletLauncher.setCooldown(baseStats.cd);
  }

  @Override
  public void onGameTick(int tick) {
    bulletLauncher.tickCooldown();
    TdMob target = world.getMobsGrid()
        .getFirst(new Point((int) x, (int) y), (int) baseStats.range);
    while (target != null && bulletLauncher.canAttack()) {
      bulletLauncher.attack(Util.get_rotation(target.x - x, target.y - y));
      target = world.getMobsGrid()
          .getFirst(new Point((int) x, (int) y), (int) baseStats.range);
    }
  }

  @Override
  public void delete() {
    sprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return sprite.isDeleted();
  }

  public static class BaseStats {

    public float power;
    public float range;
    public float pierce;
    public float cd;
    public float projectileDuration;
    public float bulletSize;
    public float speed;

    public BaseStats() {
      init();
    }

    public void init() {
    }
  }
}
