package Game;

import general.Data;
import general.Util;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import windowStuff.Sprite;
import windowStuff.UserInputListener;

public class Turret extends GameObject implements TickDetect {

  public static final int HEIGHT = 100, WIDTH = 100;
  public final String type;
  protected final BulletLauncher bulletLauncher;
  final Map<String, Float> stats;
  final Map<String, Float> baseStats;
  private final UserInputListener input;
  private final Sprite sprite;
  protected float health;
  private float vx, vy;

  public Turret(World world, int X, int Y, String imageName, BulletLauncher launcher, String type) {
    super(X, Y, WIDTH, HEIGHT, world);
    this.type = type;
    baseStats = Data.getEntityStats("turret", type);
    stats = new HashMap<>(baseStats);
    input = Game.get().getUserInputListener();
    sprite = new Sprite(imageName, WIDTH, HEIGHT, 2);
    sprite.setPosition(x, y);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    bulletLauncher = launcher;
    launcher.move(x, y);
    Game.get().addTickable(this);
    updateStats();
  }

  private void updateStats() {
    // TBD: effects (get stats from base stats)

    bulletLauncher.setDuration(stats.get("projectileDuration"));
    bulletLauncher.setPierce(stats.get("pierce").intValue());
    bulletLauncher.setPower(stats.get("power"));
    bulletLauncher.setSize(stats.get("bulletSize"));
    bulletLauncher.setSpeed(stats.get("speed"));
    bulletLauncher.setCooldown(stats.get("cd"));
  }

  @Override
  public void onGameTick(int tick) {
    bulletLauncher.tickCooldown();
    TdMob target = world.getMobsGrid()
        .getFirst(new Point((int) x, (int) y), stats.get("range").intValue());
    while (target != null && bulletLauncher.canAttack()) {
      bulletLauncher.attack(Util.get_rotation(target.x - x, target.y - y));
      target = world.getMobsGrid()
          .getFirst(new Point((int) x, (int) y), stats.get("range").intValue());
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
}
