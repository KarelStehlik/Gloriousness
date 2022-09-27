package Game;

import windowStuff.BatchSystem;

public class World implements TickDetect, MouseDetect, KeyboardDetect{

  private static final int WIDTH = 16384;
  private static final int HEIGHT = 16384;
  final Game game;
  final BatchSystem bs;
  SquareGrid<Enemy> mobs;

  public World(Game g) {
    game = g;
    game.addMouseDetect(this);
    game.addKeyDetect(this);
    game.addTickable(this);
    mobs = new SquareGrid<Enemy>(-500, -500, WIDTH+1000, HEIGHT+1000);
    bs = game.getBatchSystem("main");
    bs.getCamera().moveTo(0,-0, 20);
    Player player = new Player(this);
    for(int i=0;i<10000;i++){
      mobs.add(new Enemy(this));
    }
  }

  public void addTickable(TickDetect t) {
    game.addTickable(t);
  }

  public void addKeyDetect(KeyboardDetect t) {
    game.addKeyDetect(t);
  }

  public void addMouseDetect(MouseDetect t) {
    game.addMouseDetect(t);
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
    mobs.clear();
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean ShouldDeleteThis() {
    return false;
  }
}
