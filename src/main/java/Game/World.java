package Game;

import general.Constants;
import general.Data;
import general.Util;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import windowStuff.BatchSystem;
import windowStuff.Button;
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
  private final Text healthTracker;
  private final MobSpawner mobSpawner = new MobSpawner();
  private int tick = 0;
  private int health = Constants.StartingHealth;

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

    game.addMouseDetect(new Button(bs, new Sprite("btd_map", 100, 100, 200, 200, 10, "basic"),
        (int button, int action) -> {
          if (button == 0 && action == 1) {
            new PlaceObjectTool(
                new Sprite("fire", 200, 200, 10, bs).setColors(Util.getBaseColors(.6f)),
                (int x, int y) -> {
                  new Turret(this, x, y);
                  return true;
                });
          }
        }, () -> "some text is texted but it is also very long which may result in " + tick));

    healthTracker = new Text("Lives: " + health, "Calibri", 500, 0, 1050, 5, 40, bs);
  }

  public int getHealth() {
    return health;
  }

  public void changeHealth(int change) {
    health += change;
    healthTracker.setText("Lives: " + health);
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

  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {

  }

  @Override
  public void onScroll(double scroll) {

  }

  @Override
  public void onMouseMove(float newX, float newY) {

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

  private class MobSpawner {

    private float mobsToSpawn = 0;

    private void run(int tickId) {
      mobsToSpawn += tickId / 100f;
      while (mobsToSpawn >= 1) {
        mobsToSpawn--;
        addEnemy(new BasicMob(World.this));
      }
    }
  }
}
