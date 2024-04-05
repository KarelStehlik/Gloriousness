package Game.Turrets;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import Game.Buffs.VoidFunc;
import Game.BulletLauncher;
import Game.Game;
import Game.GameObject;
import Game.Mobs.TdMob;
import Game.TickDetect;
import Game.World;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Button;
import windowStuff.Button.MouseoverText;
import windowStuff.NoSprite;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public abstract class Turret extends GameObject implements TickDetect {

  private static final Upgrade maxUpgrades = new Upgrade("MaxUpgrades",
      () -> "here is a number. " + Game.get().getTicks(), () -> {
  }, Float.POSITIVE_INFINITY);
  private static UpgradeMenu menu;
  protected final BulletLauncher bulletLauncher;
  protected final Sprite sprite;
  protected final Sprite rangeDisplay;
  protected final BuffHandler<Turret> buffHandler;
  protected int path1Tier = 0, path2Tier = 0, path3Tier = 0;
  protected boolean notYetPlaced = true;
  protected float totalCost;

  protected Turret(World world, int X, int Y, String imageName, BulletLauncher launcher) {
    super(X, Y, 0, 0, world);
    setSize((int) stats[Turret.Stats.size], (int) stats[Turret.Stats.size]);
    clearStats();

    sprite = new Sprite(imageName, stats[Turret.Stats.spritesize], stats[Turret.Stats.spritesize],
        2);
    sprite.setPosition(x, y);
    sprite.setShader("basic");
    world.getBs().addSprite(sprite);

    rangeDisplay = new Sprite("Shockwave", 1).
        setSize(2 * stats[Turret.Stats.range], 2 * stats[Turret.Stats.range]).
        setPosition(x, y).
        addToBs(world.getBs()).
        setOpacity(0.3f);

    bulletLauncher = launcher;
    launcher.move(x, y);
    onStatsUpdate();
    buffHandler = new BuffHandler<>(this);
    world.addTurret(this);
    totalCost = stats[Stats.cost];
  }

  public void place() {
    notYetPlaced = false;
    rangeDisplay.setHidden(true);
    Game.get().addMouseDetect(new Button(this.sprite, (mouseX, mouseY) -> {
      if (!notYetPlaced) {
        openUpgradeMenu();
      }
    }));
  }

  private List<Upgrade> getUpgradePath1() {
    return List.of(up100(), up200(), up300(), up400(), up500());
  }

  private List<Upgrade> getUpgradePath2() {
    return List.of(up010(), up020(), up030(), up040(), up050());
  }

  private List<Upgrade> getUpgradePath3() {
    return List.of(up001(), up002(), up003(), up004(), up005());
  }

  protected Upgrade up100() {
    return maxUpgrades;
  }

  protected Upgrade up200() {
    return maxUpgrades;
  }

  protected Upgrade up300() {
    return maxUpgrades;
  }

  protected Upgrade up400() {
    return maxUpgrades;
  }

  protected Upgrade up500() {
    return maxUpgrades;
  }

  protected Upgrade up010() {
    return maxUpgrades;
  }

  protected Upgrade up020() {
    return maxUpgrades;
  }

  protected Upgrade up030() {
    return maxUpgrades;
  }

  protected Upgrade up040() {
    return maxUpgrades;
  }

  protected Upgrade up050() {
    return maxUpgrades;
  }

  protected Upgrade up001() {
    return maxUpgrades;
  }

  protected Upgrade up002() {
    return maxUpgrades;
  }

  protected Upgrade up003() {
    return maxUpgrades;
  }

  protected Upgrade up004() {
    return maxUpgrades;
  }

  protected Upgrade up005() {
    return maxUpgrades;
  }

  private void openUpgradeMenu() {
    if (menu != null) {
      menu.close();
    }
    menu = new UpgradeMenu();
  }

  public boolean addBuff(Buff<Turret> b) {
    return buffHandler.add(b);
  }

  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    bulletLauncher.tickCooldown();
    TdMob target = world.getMobsGrid()
        .getFirst(new Point((int) x, (int) y), (int) stats[Turret.Stats.range]);
    if (target != null) {
      setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
      while (bulletLauncher.canAttack()) {
        bulletLauncher.attack(rotation);
      }
    }

    buffHandler.tick();
  }

  @Override
  public void delete() {
    sprite.delete();
    buffHandler.delete();
    rangeDisplay.delete();
  }

  @Override
  public boolean WasDeleted() {
    return sprite.isDeleted();
  }

  public boolean isNotYetPlaced() {
    return notYetPlaced;
  }

  @Override
  protected int getStatsCount() {
    return 10;
  }

  @Override
  public void setRotation(float f) {
    rotation = f;
    sprite.setRotation(f - 90);
  }

  @Override
  public void move(float _x, float _y) {
    super.move(_x, _y);
    sprite.setPosition(_x, _y);
    rangeDisplay.setPosition(_x, _y);
    bulletLauncher.move(_x, _y);
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.setDuration(stats[Turret.Stats.projectileDuration]);
    bulletLauncher.setPierce((int) stats[Turret.Stats.pierce]);
    bulletLauncher.setPower(stats[Turret.Stats.power]);
    bulletLauncher.setSize(stats[Turret.Stats.bulletSize]);
    bulletLauncher.setSpeed(stats[Turret.Stats.speed]);
    bulletLauncher.setCooldown(1000f / stats[Turret.Stats.aspd]);
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
      return new Sprite(image, 10).setSize(200, 100).addToBs(Game.get().getSpriteBatching("main"));
    }
  }

  public static final class Stats {

    public static final int power = 0;
    public static final int range = 1;
    public static final int pierce = 2;
    public static final int aspd = 3;
    public static final int projectileDuration = 4;
    public static final int bulletSize = 5;
    public static final int speed = 6;
    public static final int cost = 7;
    public static final int size = 8;
    public static final int spritesize = 9;

    private Stats() {
    }

  }

  private void sell() {
    if (sprite.isDeleted()) {
      return;
    }
    world.setMoney(world.getMoney() + totalCost * .8);
    delete();
  }

  private class UpgradeMenu {

    private final List<Sprite> sprites = new ArrayList<>(1);
    private final List<Button> buttons = new ArrayList<>(3);

    UpgradeMenu() {
      float X = Util.clamp(x, 110, 1810), Y = Util.clamp(y, 190, 870);
      SpriteBatching bs = Game.get().getSpriteBatching("main");
      rangeDisplay.setHidden(false);
      rangeDisplay.setSize(stats[Stats.range] * 2, stats[Stats.range] * 2);
      sprites.add(new Sprite("Button", 10).
          setSize(220, 420).
          setPosition(X, Y).
          addToBs(bs)
      );
      buttons.add(new Button(
          new NoSprite().setLayer(9).setSize(5000, 5000).setPosition(X, Y - 150),
          (x, y) -> close()));

      buttons.add(new Button(
          world.getBs(),
          new Sprite("Cancelbutton", 10).setSize(150, 50).setPosition(X, Y - 150),
          (x, y) -> {
            close();
            sell();
          },
          () -> "Sell for: " + totalCost * 0.8f));

      List<Upgrade> p1 = getUpgradePath1();
      List<Upgrade> p2 = getUpgradePath2();
      List<Upgrade> p3 = getUpgradePath3();

      int maxTier1 = p1.size();
      int maxTier2 = p2.size();
      int maxTier3 = p3.size();

      if (!world.getOptions().isUltimateCrosspathing()) {
        if (path1Tier >= 3) {
          maxTier2 = 2;
          maxTier3 = 2;
        }
        if (path2Tier >= 3) {
          maxTier3 = 2;
          maxTier1 = 2;
        }
        if (path3Tier >= 3) {
          maxTier2 = 2;
          maxTier1 = 2;
        }
        if (path1Tier > 0 && path2Tier > 0) {
          maxTier3 = 0;
        }
        if (path3Tier > 0 && path2Tier > 0) {
          maxTier1 = 0;
        }
        if (path1Tier > 0 && path3Tier > 0) {
          maxTier2 = 0;
        }
      }

      Upgrade u1 = path1Tier < maxTier1 ? p1.get(path1Tier) : maxUpgrades;
      Upgrade u2 = path2Tier < maxTier2 ? p2.get(path2Tier) : maxUpgrades;
      Upgrade u3 = path3Tier < maxTier3 ? p3.get(path3Tier) : maxUpgrades;

      buttons.add(
          new Button(bs, u1.makeSprite().setPosition(X, Y - 50), (mx, my) -> buttonClicked(u1, 1),
              () -> u1.text.get() + " cost: " + u1.cost));
      buttons.add(
          new Button(bs, u2.makeSprite().setPosition(X, Y + 50), (mx, my) -> buttonClicked(u2, 2),
              () -> u2.text.get() + " cost: " + u2.cost));
      buttons.add(
          new Button(bs, u3.makeSprite().setPosition(X, Y + 150), (mx, my) -> buttonClicked(u3, 3),
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
      totalCost += u.cost;
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
      rangeDisplay.setHidden(true);
    }
  }
}
