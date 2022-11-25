package Game;

import general.Constants;
import general.Data;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import windowStuff.BatchSystem;
import windowStuff.Button;
import windowStuff.Sprite;

public class World implements TickDetect, MouseDetect, KeyboardDetect {

  private static final int WIDTH = 16384;
  private static final int HEIGHT = 16384;
  private final BatchSystem bs;
  private final SquareGrid<Mob> mobsGrid;
  private final List<Mob> mobsList;
  private final SquareGrid<Projectile> projectilesGrid;
  private final List<Projectile> projectilesList;
  private final Player player;
  private final Sprite mapSprite;
  private final List<Point> mapData;
  private int tick = 0;

  public World() {
    Game game = Game.get();
    game.addMouseDetect(this);
    game.addKeyDetect(this);
    game.addTickable(this);
    mobsGrid = new SquareGrid<Mob>(-500, -500, WIDTH + 1000, HEIGHT + 1000, 6);
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
    for (int i = 0; i < 5000; i++) {
      addEnemy(new BasicMob(this));
    }

    game.addMouseDetect(new Button(bs, new Sprite("btd_map", 100, 100, 100, 100, 10, "basic"),
        (int button, int action) -> {
          if (button == 0 && action == 1) {
            addEnemy(new BasicMob(this));
          }
        }, () -> "some text is texted but it is also very long which may result in things"+tick));
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
    tickEntities(projectilesGrid, projectilesList);
    player.onGameTick(tick);
    projectilesGrid.clear();
  }

  private <T extends GameObject & TickDetect> void tickEntities(SquareGrid<T> grid, List<T> list) {
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

  SquareGrid<Mob> getMobsGrid() {
    return mobsGrid;
  }

  SquareGrid<Projectile> getProjectilesGrid() {
    return projectilesGrid;
  }
}
