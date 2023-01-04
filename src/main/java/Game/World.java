package Game;

import general.Constants;
import general.Data;
import java.awt.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import windowStuff.BatchSystem;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.Sprite;
import windowStuff.Text;

public class World implements TickDetect, MouseDetect, KeyboardDetect {

  private static final int WIDTH = 16384;
  private static final int HEIGHT = 16384;
  private final BatchSystem bs;
  private final SquareGridMobs mobsGrid;
  private final List<Mob> mobsList;
  private final SquareGrid<Projectile> projectilesGrid;
  private final List<Projectile> projectilesList;
  private final Player player;
  private final Sprite mapSprite;
  private final List<Point> mapData;
  private final Text resourceTracker;
  private final MobSpawner mobSpawner = new MobSpawner();
  private int tick = 0;
  private int health = Constants.StartingHealth;
  private TurretGenerator[] availableTurrets;
  private float money = 100;
  public Tool currentTool = null;

  public World() {
    Game game = Game.get();
    game.addMouseDetect(this);
    game.addKeyDetect(this);
    game.addTickable(this);
    mobsGrid = new SquareGridMobs(-500, -500, WIDTH + 1000, HEIGHT + 1000, 7);
    mobsList = new LinkedList<>();
    projectilesGrid = new SquareGrid<Projectile>(-500, -500, WIDTH + 1000, HEIGHT + 1000, 8);
    projectilesList = new LinkedList<>();
    bs = game.getBatchSystem("main");
    getBs().getCamera().moveTo(0, -0, 20);
    player = new Player(this);
    String mapName = Data.listMaps()[0];
    mapSprite = new Sprite(mapName, Constants.screenSize.x / 2f, Constants.screenSize.y / 2f,
        Constants.screenSize.x, Constants.screenSize.y, 0, "basic");
    bs.addSprite(mapSprite);
    mapData = Data.getMapData(mapName);

    TurretGenerator test = new TurretGenerator(this, "gun", "ph", 100).
        addOnMobCollide((proj, mob)->mob.takeDamage(proj.getPower(), DamageType.PHYSICAL));
    availableTurrets = new TurretGenerator[] {test,test,test,test,test,test,test,test,test,test,
        test,test,test,test,test,test,test,test,test,test,test,test,test,test,};

    ButtonArray turretBar = new ButtonArray(2,
        Arrays.stream(availableTurrets).map(tg ->tg.makeButton(5)).toArray(Button[]::new),
        new Sprite("Button", 4).addToBs(bs),75,Constants.screenSize.x,Constants.screenSize.y,10,1,1);
    game.addMouseDetect(turretBar);

    resourceTracker = new Text("Lives: " + health+ "\nCash: " + (int) getMoney(), "Calibri", 500, 0, 1050, 10, 40, bs);
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
    System.out.println("gjghjghjg");
  }

  public void addEnemy(Mob e) {
    mobsList.add(e);
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    if(currentTool != null) {
      currentTool.onKeyPress(key, action, mods);
    }
  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {
    if(currentTool != null) {
      currentTool.onMouseButton(button, x, y, action, mods);
    }
  }

  @Override
  public void onScroll(double scroll) {
    if(currentTool != null) {
      currentTool.onScroll(scroll);
    }
  }

  @Override
  public void onMouseMove(float newX, float newY) {
    if(currentTool != null) {
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
    projectilesGrid.clear();
    mobSpawner.run(tick);
  }

  private <T extends GameObject & TickDetect> void tickEntities(SpacePartitioning<T> grid,
      Iterable<T> list) {
    grid.clear();
    for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
      T e = iterator.next();
      e.onGameTick(tick);
      if (e.WasDeleted()) {
        iterator.remove();
      }
    }
  }

  @Override
  public void delete() {
    mapSprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return false;
  }

  BatchSystem getBs() {
    return bs;
  }

  SquareGridMobs getMobsGrid() {
    return mobsGrid;
  }

  SquareGrid<Projectile> getProjectilesGrid() {
    return projectilesGrid;
  }

  public float getMoney() {
    return money;
  }

  public void setMoney(float money) {
    this.money = money;
    resourceTracker.setText("Lives: " + health + "\nCash: " + (int) getMoney());
  }

  private class MobSpawner {

    private float mobsToSpawn = 0;

    private void run(int tickId) {
      mobsToSpawn += tickId / 100f;
      while (mobsToSpawn >= 1) {
        mobsToSpawn--;
        BasicMob e = new BasicMob(World.this);
        e.addStatusEffect(new Mob.StatusEffect(0, m ->{
          m.stats.put("health", m.stats.get("health") * tickId);
        }));
        addEnemy(e);
      }
    }
  }
}
