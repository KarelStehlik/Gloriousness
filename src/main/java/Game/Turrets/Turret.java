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
import general.Log;
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
  private static UpgradeMenu menu;
  protected final BulletLauncher bulletLauncher;
  protected final Sprite sprite;
  protected final Sprite rangeDisplay;
  private final BuffHandler<Turret> buffHandler;
  protected int path1Tier = 0, path2Tier = 0, path3Tier = 0;
  protected boolean notYetPlaced=true;

  protected Turret(World world, int X, int Y, String imageName, BulletLauncher launcher) {
    super(X, Y, 0,0, world);
    setSize((int)stats[Turret.Stats.size], (int)stats[Turret.Stats.size]);
    clearStats();

    sprite = new Sprite(imageName, stats[Turret.Stats.spritesize], stats[Turret.Stats.spritesize], 2);
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
    Game.get().addTickable(this);
    onStatsUpdate();
    buffHandler = new BuffHandler<>(this);
    world.addTurret(this);
  }

  @Override
  public void move(float _x, float _y){
    super.move(_x,_y);
    sprite.setPosition(_x,_y);
    rangeDisplay.setPosition(_x,_y);
    bulletLauncher.move(_x,_y);
  }

  public void place(){
    notYetPlaced = false;
    rangeDisplay.setHidden(true);
    Button butt = new Button(this.sprite, (mouseX, mouseY) -> {if(!notYetPlaced){openUpgradeMenu();}});
    Game.get().addMouseDetect(butt);
  }

  protected abstract List<Upgrade> getUpgradePath1();

  protected abstract List<Upgrade> getUpgradePath2();

  protected abstract List<Upgrade> getUpgradePath3();

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
  public void onStatsUpdate() {
    bulletLauncher.setDuration(stats[Turret.Stats.projectileDuration]);
    bulletLauncher.setPierce((int) stats[Turret.Stats.pierce]);
    bulletLauncher.setPower(stats[Turret.Stats.power]);
    bulletLauncher.setSize(stats[Turret.Stats.bulletSize]);
    bulletLauncher.setSpeed(stats[Turret.Stats.speed]);
    bulletLauncher.setCooldown(stats[Turret.Stats.cd]);
  }

  @Override
  public void onGameTick(int tick) {
    if(notYetPlaced)return;
    bulletLauncher.tickCooldown();
    TdMob target = world.getMobsGrid()
        .getFirst(new Point((int) x, (int) y), (int) stats[Turret.Stats.range]);
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
    rangeDisplay.delete();
  }

  @Override
  public boolean WasDeleted() {
    return sprite.isDeleted();
  }

  public boolean isNotYetPlaced() {
    return notYetPlaced;
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

  @Override
  protected int getStatsCount(){
    return 10;
  }
  public static class Stats {

    public static final int power=0;
    public static final int  range=1;
    public static final int  pierce=2;
    public static final int  cd=3;
    public static final int  projectileDuration=4;
    public static final int  bulletSize=5;
    public static final int  speed=6;
    public static final int  cost=7;
    public static final int  size=8;
    public static final int  spritesize=9;

    private Stats() {
    }

  }

  private class UpgradeMenu {

    private final List<Sprite> sprites = new ArrayList<>(1);
    private final List<Button> buttons = new ArrayList<>(3);

    UpgradeMenu() {
      SpriteBatching bs = Game.get().getSpriteBatching("main");
      rangeDisplay.setHidden(false);
      sprites.add(new Sprite("Button", 5).
          setSize(220, 420).
          setPosition(x, y).
          addToBs(bs)
      );
      buttons.add(new Button(
          new Sprite("Cancelbutton", 5).addToBs(bs).setSize(200, 50).setPosition(x, y - 150),
          (x, y) -> close()));

      List<Upgrade> p1 = getUpgradePath1();
      List<Upgrade> p2 = getUpgradePath2();
      List<Upgrade> p3 = getUpgradePath3();

      Upgrade u1 = path1Tier < p1.size() ? p1.get(path1Tier) : maxUpgrades;
      Upgrade u2 = path1Tier < p2.size() ? p2.get(path1Tier) : maxUpgrades;
      Upgrade u3 = path1Tier < p3.size() ? p3.get(path1Tier) : maxUpgrades;

      buttons.add(
          new Button(bs, u1.makeSprite().setPosition(x, y - 50), (mx, my) -> buttonClicked(u1, 1),
              () -> u1.text.get() + " cost: " + u1.cost));
      buttons.add(
          new Button(bs, u2.makeSprite().setPosition(x, y + 50), (mx, my) -> buttonClicked(u2, 2),
              () -> u2.text.get() + " cost: " + u2.cost));
      buttons.add(
          new Button(bs, u3.makeSprite().setPosition(x, y + 150), (mx, my) -> buttonClicked(u3, 3),
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
      rangeDisplay.setHidden(true);
    }
  }
}
