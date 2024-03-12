package Game.Turrets;

import Game.Buffs.BuffHandler;
import Game.BulletLauncher;
import Game.Game;
import Game.GameObject;
import Game.TdMob;
import Game.TickDetect;
import Game.World;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import windowStuff.Sprite;

public class Turret extends GameObject implements TickDetect {

  public static final int HEIGHT = 150, WIDTH = 150;
  public final BaseStats baseStats;
  protected final BulletLauncher bulletLauncher;
  private final Sprite sprite;
  private final BuffHandler<Turret> buffHandler;

  protected Turret(World world, int X, int Y, String imageName, BulletLauncher launcher,
      BaseStats newStats) {
    super(X, Y, WIDTH, HEIGHT, world);
    baseStats = newStats;
    sprite = new Sprite(imageName, WIDTH, HEIGHT, 2);
    sprite.setPosition(x, y);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    bulletLauncher = launcher;
    launcher.move(x, y);
    Game.get().addTickable(this);
    onStatsUpdate();
    buffHandler = new BuffHandler<>(this);
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.setDuration(baseStats.projectileDuration.get());
    bulletLauncher.setPierce((int) baseStats.pierce.get());
    bulletLauncher.setPower(baseStats.power.get());
    bulletLauncher.setSize(baseStats.bulletSize.get());
    bulletLauncher.setSpeed(baseStats.speed.get());
    bulletLauncher.setCooldown(baseStats.cd.get());
  }

  @Override
  public void onGameTick(int tick) {
    bulletLauncher.tickCooldown();
    TdMob target = world.getMobsGrid()
        .getFirst(new Point((int) x, (int) y), (int) baseStats.range.get());
    if (target != null) {
      var rotation = Util.get_rotation(target.getX() - x, target.getY() - y);
      while (bulletLauncher.canAttack()) {
        bulletLauncher.attack(rotation);
      }
      sprite.setRotation(rotation - 90);
    }

    buffHandler.tick();
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

    public RefFloat power;
    public RefFloat range;
    public RefFloat pierce;
    public RefFloat cd;
    public RefFloat projectileDuration;
    public RefFloat bulletSize;
    public RefFloat speed;

    public BaseStats() {
      init();
    }

    public void init() {
    }
  }
}
