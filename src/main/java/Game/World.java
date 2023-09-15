package Game;

import Game.Buffs.Buff;
import general.Constants;
import general.Data;
import general.Log;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector2f;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.NoSprite;
import windowStuff.SingleAnimationSprite;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;
import windowStuff.Text;

public class World implements TickDetect, MouseDetect, KeyboardDetect {

  private static final int WIDTH = 16384;
  private static final int HEIGHT = 16384;
  private final SpriteBatching bs;
  private final SquareGridMobs mobsGrid;
  private final List<TdMob> mobsList;
  private final SquareGrid<Projectile> projectilesGrid;
  private final List<Projectile> projectilesList;
  private final Player player;
  private final Sprite mapSprite;
  private final List<Point> mapData;
  private final Text resourceTracker;
  private final MobSpawner mobSpawner = new MobSpawner();
  private final Log.Timer t = new Log.Timer();
  private Tool currentTool;
  private int tick = 0;
  private int health = Constants.StartingHealth;
  private double money = 1234567890;

  public World() {
    Game game = Game.get();
    game.addMouseDetect(this);
    game.addKeyDetect(this);
    game.addTickable(this);
    mobsGrid = new SquareGridMobs(-500, -500, WIDTH + 1000, HEIGHT + 1000, 7);
    mobsList = new ArrayList<>(1024);
    projectilesGrid = new SquareGrid<Projectile>(-500, -500, WIDTH + 1000, HEIGHT + 1000, 8);
    projectilesList = new ArrayList<>(128);
    bs = game.getSpriteBatching("main");
    getBs().getCamera().moveTo(0, -0, 20);
    player = new Player(this);
    String mapName = Data.listMaps()[0];
    mapSprite = new Sprite(mapName, Constants.screenSize.x / 2f, Constants.screenSize.y / 2f,
        Constants.screenSize.x, Constants.screenSize.y, 0, "basic");
    bs.addSprite(mapSprite);
    mapData = Data.getMapData(mapName);

    TurretGenerator test = new TurretGenerator(this, "gun", "ph", 100).
        addOnMobCollide((proj, mob) -> mob.takeDamage(proj.getPower(), DamageType.PHYSICAL));
    TurretGenerator[] availableTurrets = new TurretGenerator[]{test, test, test, test, test,
        test, test, test, test,
        test,
        test, test, test, test, test, test, test, test, test, test, test, test, test, test,};

    ButtonArray turretBar = new ButtonArray(2,
        Arrays.stream(availableTurrets).map(tg -> tg.makeButton(5)).toArray(Button[]::new),
        new Sprite("Button", 4).addToBs(bs), 75, Constants.screenSize.x, Constants.screenSize.y, 10,
        1, 1);
    game.addMouseDetect(turretBar);

    game.addMouseDetect(new Button(bs, new NoSprite().setSize(100, 100).setPosition(410, 660)
        , (button, action) -> {
      if (action == 0) {
        return;
      }
      float x = game.getUserInputListener().getX(), y = game.getUserInputListener().getY();
      explosionVisual(x, y, 100, true, "Explosion1-0");
    }, null));

    resourceTracker = new Text("Lives: " + health + "\nCash: " + (int) getMoney(), "Calibri", 500,
        0, 1050, 10, 40, bs);

    currentTool = new PlaceObjectTool(this, new NoSprite(), (x, y) -> false);
    currentTool.delete();
  }

  public void explosionVisual(float x, float y, float size, boolean shockwave, String image) {
    Game game = Game.get();
    if (shockwave) {
      game.addTickable(
          new Animation(
              new Sprite("Shockwave", x, y, size, size, 3, "basic").addToBs(bs).setOpacity(0.7f), 3
          ).setLinearScaling(new Vector2f(size / 3, size / 3)).setOpacityScaling(-0.01f)
      );
    }
    Sprite s = new SingleAnimationSprite(image, .7f, x, y, size * 5, size * 5, 4, "basic").
        addToBs(bs).setRotation(Data.unstableRng.nextFloat(360));
  }

  public void aoeDamage(int x, int y, int size, float damage, DamageType type) {
    mobsGrid.callForEachCircle(x, y, size, m -> m.takeDamage(damage, type));
  }

  public int getHealth() {
    return health;
  }

  public void changeHealth(int change) {
    health += change;
    resourceTracker.setText("Lives: " + health + "\nCash: " + (int) getMoney());
  }

  public List<Point> getMapData() {
    return mapData;
  }

  public Player getPlayer() {
    return player;
  }

  public List<Projectile> getProjectilesList() {
    return projectilesList;
  }

  protected void endGame() {
    Log.write("gjghjghjg");
  }

  public void addEnemy(TdMob e) {
    mobsList.add(e);
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    if (!currentTool.WasDeleted()) {
      currentTool.onKeyPress(key, action, mods);
    }
  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {
    if (!currentTool.WasDeleted()) {
      currentTool.onMouseButton(button, x, y, action, mods);
    }
  }

  @Override
  public void onScroll(double scroll) {
    if (!currentTool.WasDeleted()) {
      currentTool.onScroll(scroll);
    }
  }

  @Override
  public void onMouseMove(float newX, float newY) {
    if (!currentTool.WasDeleted()) {
      currentTool.onMouseMove(newX, newY);
    }
  }

  @Override
  public void onGameTick(int tick) {
    this.tick = tick;
    tickEntities(mobsGrid, mobsList);

    mobsGrid.filled();

    tickEntities(projectilesGrid, projectilesList);

    player.onGameTick(tick);
    mobSpawner.run(tick);
  }

  @Override
  public void delete() {
    mapSprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return false;
  }

  private <T extends GameObject & TickDetect> void tickEntities(SpacePartitioning<T> grid,
      List<T> list) {
    grid.clear();

    for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
      T e = iterator.next();
      if (e.WasDeleted()) {
        iterator.remove();
      } else {
        e.onGameTick(tick);
      }
    }
  }

  SpriteBatching getBs() {
    return bs;
  }

  SquareGridMobs getMobsGrid() {
    return mobsGrid;
  }

  SquareGrid<Projectile> getProjectilesGrid() {
    return projectilesGrid;
  }

  public double getMoney() {
    return money;
  }

  public void setMoney(double money) {
    this.money = money;
    resourceTracker.setText("Lives: " + health + "\nCash: " + (int) getMoney());
  }

  public Tool getCurrentTool() {
    return currentTool;
  }

  public void setCurrentTool(Tool currentTool) {
    if (this.currentTool != null) {
      this.currentTool.delete();
    }
    this.currentTool = currentTool;
  }

  private class MobSpawner {

    private float mobsToSpawn = 0;

    private static float scaling(int tickId) {
      return 1 + (Math.max(tickId, 10) - 10) / 100f;
    }

    private void run(int tickId) {
      mobsToSpawn += tickId / 10f;
      while (mobsToSpawn >= 1) {
        mobsToSpawn--;
        TdMob e = new BasicMob(World.this);
        e.addBuff(new Buff<TdMob>(0, Buff.INFINITE_DURATION, Buff.TRIGGER_ON_UPDATE,
            m -> {
              m.stats.put("health", m.stats.get("health") * scaling(tickId));
              m.stats.put("speed", m.stats.get("speed") * (float) Math.pow(scaling(tickId), 0.3));
            }
        ));
        addEnemy(e);
      }
    }
  }
}
