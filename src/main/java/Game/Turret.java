package Game;

import general.Data;
import general.Util;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import windowStuff.Sprite;
import windowStuff.UserInputListener;

public class Turret extends GameObject implements TickDetect {

  private static final int HEIGHT = 200, WIDTH = 100;
  final Map<String, Float> stats;
  final Map<String, Float> baseStats;
  private final UserInputListener input;
  private final Sprite sprite;
  private final float speed = 10;
  private final BulletLauncher bulletLauncher;
  private final int range = 500;
  protected float health;
  private float vx, vy;

  public Turret(World world, int X, int Y) {
    super(X, Y, WIDTH, HEIGHT, world);
    baseStats = Data.getEntityStats("mob", "Player");
    stats = new HashMap<>(baseStats);
    health = stats.get("health");
    input = Game.get().getUserInputListener();
    sprite = new Sprite("Defender", WIDTH, HEIGHT, 10);
    sprite.setPosition(x, y);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    bulletLauncher = new BulletLauncher(world, "Egg", x, y, 30,
        30, 30, 0, 30, 3, 200);
    bulletLauncher.addMobCollide(
        (proj, target) -> target.takeDamage(proj.getPower(), DamageType.PHYSICAL));
    Game.get().addTickable(this);
  }

  @Override
  public void onGameTick(int tick) {
    Mob target = world.getMobsGrid().getFirst(new Point((int) x, (int) y), range);
    if (target != null) {
      for (int i = 0; i < 10; i++) {
        bulletLauncher.attack(
            Util.get_rotation(target.x - x, target.y - y) + Data.gameMechanicsRng.nextFloat() * 20
                - 10);
      }
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
