package Game.Turrets;

import static Game.Enums.TargetingOption.FIRST;
import static Game.Enums.TargetingOption.LAST;
import static Game.Enums.TargetingOption.STRONG;

import Game.Buffs.Buff;
import Game.Buffs.BuffHandler;
import Game.Buffs.VoidFunc;
import Game.BulletLauncher;
import Game.Enums.TargetingOption;
import Game.Game;
import Game.GameObject;
import Game.Mobs.TdMob;
import Game.TdWorld;
import Game.TickDetect;
import general.Data;
import general.Description;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Button;
import windowStuff.Graphics;
import windowStuff.ImageData;
import windowStuff.NoSprite;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public abstract class Turret extends GameObject implements TickDetect {

  private static final Upgrade maxUpgrades = new Upgrade("MaxUpgrades",
      new Description(() -> "LOCKED",
          () -> "here is a number. " + Data.unstableRng.nextFloat() * 562874,
          () -> "it's something else now."), () -> {
  }, Float.POSITIVE_INFINITY);
  private static UpgradeMenu menu;
  protected final BulletLauncher bulletLauncher;
  protected final Sprite sprite;
  protected final Sprite rangeDisplay;
  protected final BuffHandler<Turret> buffHandler;
  protected int path1Tier = 0, path2Tier = 0, path3Tier = 0;
  protected boolean notYetPlaced = true;
  protected float totalCost;
  public float resaleValue=1;
  protected final float[] originalStats;
  protected TargetingOption targeting = FIRST;
  private final boolean imageRatio = false; // scales itself to the aspect ratio of the image

  protected Turret(TdWorld world, int X, int Y, BulletLauncher launcher) {
    super(X, Y, 0, 0, world);
    setSize((int) stats[Turret.Stats.size], (int) stats[Turret.Stats.size]);
    clearStats();
    originalStats = getStats().clone();

    sprite = new Sprite(getImageUpdate(), 20).setSize(stats[Turret.Stats.spritesize],
        stats[Turret.Stats.spritesize]);
    sprite.setNaturalHeight();
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

  protected ImageData getImageUpdate(){
    return Graphics.getImage("");
  }

  protected int getHighestTier() {
    return Math.max(Math.max(path3Tier, path2Tier), path1Tier);
  }

  protected List<Upgrade> getUpgradePath1() {
    return List.of(up100(), up200(), up300(), up400(), up500());
  }

  protected List<Upgrade> getUpgradePath2() {
    return List.of(up010(), up020(), up030(), up040(), up050());
  }

  protected List<Upgrade> getUpgradePath3() {
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

  protected void openUpgradeMenu() {
    if (menu != null) {
      menu.close();
    }
    menu = new UpgradeMenu();
  }

  protected void closeUpgradeMenu() {

  }

  public boolean addBuff(Buff<Turret> b) {
    return buffHandler.add(b);
  }
  public void scale(float _width,float _height){
    sprite.scale(_width,_height);
  }

  protected TdMob target() {
    return world.getMobsGrid().search(
        new Point((int) x, (int) y), (int) stats[Turret.Stats.range], targeting
    );
  }

  protected ArrayList<TdMob> target(int maxTargets) {
    return world.getMobsGrid().search(
        new Point((int) x, (int) y), (int) stats[Turret.Stats.range], targeting, maxTargets
    );
  }

  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    bulletLauncher.tickCooldown();
    TdMob target = target();
    if (target != null) {
      setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
      while (bulletLauncher.canAttack()) {
        bulletLauncher.attack(Util.get_rotation(target.getX() - x, target.getY() - y));
      }
    }

    buffHandler.tick();
  }

  protected final List<VoidFunc> endOfRoundEffects = new ArrayList<>(0);

  public void endOfRound() {
    endOfRoundEffects.forEach(VoidFunc::apply);
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
    super.setRotation(f);
    sprite.setRotation(f - 90);
  }

  @Override
  public void move(float _x, float _y) {
    super.move(_x, _y);
    sprite.setPosition(_x, _y);
    rangeDisplay.setPosition(_x, _y);
    if (world.canFitTurret((int) x, (int) y, stats[Stats.size])) {
      rangeDisplay.setColors(Util.getColors(0, 0, 0));
    } else {
      rangeDisplay.setColors(Util.getColors(9, 0, 0));
    }
    bulletLauncher.move(_x, _y);
  }

  @Override
  public void onStatsUpdate() {
    bulletLauncher.updateStats(stats);
    rangeDisplay.setSize(stats[Stats.range] * 2, stats[Stats.range] * 2);
    extraStatsUpdate();
  }

  protected void extraStatsUpdate() {
  }

  public boolean blocksPlacement() {
    return true;
  }

  protected static class Upgrade {

    protected final float cost;
    private final String image;
    protected Description description;
    protected VoidFunc apply;

    public Upgrade(String image, Description description, VoidFunc apply, float cost) {
      this.image = image;
      this.description = description;
      this.apply = apply;
      this.cost = cost;
    }

    public Sprite makeSprite(int layer) {
      return new Sprite(image, layer).setSize(200, 100).addToBs(Game.get().getSpriteBatching("main"));
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
  public float getSellprice(){
    return totalCost*resaleValue;
  }

  protected void sell() {
    if (sprite.isDeleted()) {
      return;
    }
    world.setMoney(world.getMoney()+ this.getSellprice());
    delete();
  }

  private class UpgradeMenu {

    private final List<Sprite> sprites = new ArrayList<>(1);
    private final List<Button> buttons = new ArrayList<>(3);

    UpgradeMenu() {
      float X = Util.clamp(x, 110, 1810), Y = Util.clamp(y, 190, 870);
      SpriteBatching bs = Game.get().getSpriteBatching("main");
      rangeDisplay.setHidden(false);
      sprites.add(new Sprite("Button", sprite.getLayer() + 5).
          setSize(220, 420).
          setPosition(X, Y).
          addToBs(bs)
      );
      buttons.add(new Button(
          new NoSprite().setLayer(sprite.getLayer() + 6).setSize(5000, 5000).setPosition(X, Y - 150),
          (x, y) -> close()));

      buttons.add(new Button(
          world.getBs(),
          new Sprite("Sell", sprite.getLayer() + 9).setSize(190, 40).setPosition(X, Y - 180),
          (x, y) -> {
            close();
            sell();
          },
          () -> "Sell for: " + getSellprice()));

      buttons.add(new Button(
          world.getBs(),
          new Sprite("Radar", sprite.getLayer() + 10).setSize(170, 40).setPosition(X, Y - 135),
          (x, y) -> {
            targeting = switch (targeting) {
              case FIRST -> LAST;
              case LAST -> STRONG;
              case STRONG -> FIRST;
            };
          },
          () -> "" + targeting));

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
          new Button(bs, u1.makeSprite(sprite.getLayer()+10).setPosition(X, Y - 50), (mx, my) -> buttonClicked(u1, 1),
              u1.description.getAsTextBox(sprite.getLayer() + 11, bs, u1.cost)));
      buttons.add(
          new Button(bs, u2.makeSprite(sprite.getLayer()+10).setPosition(X, Y + 50), (mx, my) -> buttonClicked(u2, 2),
              u2.description.getAsTextBox(sprite.getLayer() + 11, bs, u2.cost)));
      buttons.add(
          new Button(bs, u3.makeSprite(sprite.getLayer()+10).setPosition(X, Y + 150), (mx, my) -> buttonClicked(u3, 3),
              u3.description.getAsTextBox(sprite.getLayer() + 11, bs, u3.cost)));

      buttons.forEach(Game.get()::addMouseDetect);
      buttons.forEach(Game.get()::addTickable);
    }

    private void buttonClicked(Upgrade u, int path) {
      if (!world.tryPurchase(u.cost)) {
        return;
      }
      u.apply.apply();
      upgradePicked(path);
      totalCost += u.cost;
      if(resaleValue==1){
        resaleValue=0.8f;
      }
      sprite.setImage(getImageUpdate());
      sprite.setNaturalHeight();
      openUpgradeMenu();
    }

    private void upgradePicked(int path) {
      switch (path) {
        case 1 -> path1Tier++;
        case 2 -> path2Tier++;
        case 3 -> path3Tier++;
      }
    }

    void close() {
      sprites.forEach(Sprite::delete);
      buttons.forEach(Button::delete);
      rangeDisplay.setHidden(true);
      closeUpgradeMenu();
    }
  }
}
