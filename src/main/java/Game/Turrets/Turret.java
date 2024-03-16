package Game.Turrets;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import Game.Buffs.VoidFunc;
import Game.BulletLauncher;
import Game.Game;
import Game.GameObject;
import Game.TdMob;
import Game.TickDetect;
import Game.World;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Button;
import windowStuff.Button.MouseoverText;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public abstract class Turret extends GameObject implements TickDetect {

  private static final Upgrade maxUpgrades = new Upgrade("MaxUpgrades",
      () -> "here is a number. " + Game.get().getTicks(), () -> {
  }, Float.POSITIVE_INFINITY);
  public final BaseStats baseStats;
  protected final BulletLauncher bulletLauncher;
  protected final Sprite sprite;
  private final BuffHandler<Turret> buffHandler;
  protected int path1Tier = 0, path2Tier = 0, path3Tier = 0;

  protected Turret(World world, int X, int Y, String imageName, BulletLauncher launcher,
      BaseStats newStats) {
    super((float) X, (float) Y, (int) newStats.size.get(), (int) newStats.size.get(), world);
    baseStats = newStats;
    sprite = new Sprite(imageName, newStats.spritesize.get(), newStats.spritesize.get(), 2);
    sprite.setPosition(x, y);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);
    bulletLauncher = launcher;
    launcher.move(x, y);
    Game.get().addTickable(this);
    onStatsUpdate();
    buffHandler = new BuffHandler<>(this);
    Game.get().addMouseDetect(new Button(this.sprite, (mouseX, mouseY) -> openUpgradeMenu()));
    world.addTurret(this);
  }

  protected abstract List<Upgrade> getUpgradePath1();

  protected abstract List<Upgrade> getUpgradePath2();

  protected abstract List<Upgrade> getUpgradePath3();

  private static UpgradeMenu menu;

  private void openUpgradeMenu() {
    if(menu != null){
      menu.close();
    }
    menu=new UpgradeMenu();
  }

  public boolean addBuff(Buff<Turret> b) {
    return buffHandler.add(b);
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

  protected static class Upgrade {

    protected final float cost;
    private final String image;
    protected Button.MouseoverText text;
    protected VoidFunc apply;

    public Upgrade(String image, MouseoverText text, VoidFunc apply, float cost) {
      this.image = image;
      this.text = text;
      this.apply = apply;
      this.cost = cost;
    }

    public Sprite makeSprite() {
      return new Sprite(image, 5).setSize(200, 100).addToBs(Game.get().getSpriteBatching("main"));
    }
  }

  public static class BaseStats {

    public RefFloat power;
    public RefFloat range;
    public RefFloat pierce;
    public RefFloat cd;
    public RefFloat projectileDuration;
    public RefFloat bulletSize;
    public RefFloat speed;
    public RefFloat cost;
    public RefFloat size;
    public RefFloat spritesize;

    public BaseStats() {
      init();
    }

    public void init() {
    }
  }

  private class UpgradeMenu {

    private final List<Sprite> sprites = new ArrayList<>(1);
    private final List<Button> buttons = new ArrayList<>(3);

    UpgradeMenu() {
      SpriteBatching bs = Game.get().getSpriteBatching("main");
      buttons.add(new Button(
          new Sprite("Cancelbutton", 5).addToBs(bs).setSize(200, 50).setPosition(200, 1000),
          (x, y) -> close()));

      List<Upgrade> p1 = getUpgradePath1();
      List<Upgrade> p2 = getUpgradePath2();
      List<Upgrade> p3 = getUpgradePath3();

      Upgrade u1 = path1Tier < p1.size() ? p1.get(path1Tier) : maxUpgrades;
      Upgrade u2 = path1Tier < p2.size() ? p2.get(path1Tier) : maxUpgrades;
      Upgrade u3 = path1Tier < p3.size() ? p3.get(path1Tier) : maxUpgrades;

      buttons.add(
          new Button(bs, u1.makeSprite().setPosition(100, 100), (mx, my) -> buttonClicked(u1, 1),
              () -> u1.text.get() + " cost: " + u1.cost));
      buttons.add(
          new Button(bs, u2.makeSprite().setPosition(100, 300), (mx, my) -> buttonClicked(u2, 2),
              () -> u2.text.get() + " cost: " + u2.cost));
      buttons.add(
          new Button(bs, u3.makeSprite().setPosition(100, 500), (mx, my) -> buttonClicked(u3, 3),
              () -> u3.text.get() + " cost: " + u3.cost));

      buttons.forEach(Game.get()::addMouseDetect);
      buttons.forEach(Game.get()::addTickable);
    }

    private void buttonClicked(Upgrade u, int path) {
      if (!world.tryPurchase(u.cost)) {
        return;
      }
      upgradePicked(path);
      u.apply.apply();
    }

    private void upgradePicked(int path) {
      switch (path) {
        case 1 -> path1Tier++;
        case 2 -> path2Tier++;
        case 3 -> path3Tier++;
      }
      openUpgradeMenu();
    }

    void close() {
      sprites.forEach(Sprite::delete);
      buttons.forEach(Button::delete);
    }
  }
}
