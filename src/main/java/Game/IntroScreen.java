package Game;

import general.Constants;
import general.Data;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public class IntroScreen implements World {

  private final ButtonArray maps;
  private final SpriteBatching bs;

  public IntroScreen() {
    bs = Game.get().getSpriteBatching("main");
    int mapCount = Data.listMaps().length;
    Button[] buttons = new Button[mapCount];
    for (int i = 0; i < mapCount; i++) {
      buttons[i] = makeMapButton(i);
    }

    maps = new ButtonArray(2,
        buttons,
        new Sprite("Button", 4).addToBs(bs), 75, Constants.screenSize.x, Constants.screenSize.y, 10,
        1, 1);
    Game.get().addMouseDetect(maps);
  }

  private Button makeMapButton(int id) {
    String mapName = Data.listMaps()[id];
    Sprite sp = new Sprite(mapName, 6).setSize(10, 10);
    Button b = new Button(Game.get().getSpriteBatching("main"), sp, (x, y) -> {
      delete();
      Game.get().setWorld(new TdWorld(id));
    });
    return b;
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {

  }

  @Override
  public int getLayer() {
    return 5;
  }

  @Override
  public boolean onMouseButton(int button, double x, double y, int action, int mods) {
    return false;
  }

  @Override
  public boolean onScroll(double scroll) {
    return false;
  }

  @Override
  public boolean onMouseMove(float newX, float newY) {
    return false;
  }

  @Override
  public void onGameTick(int tick) {

  }

  @Override
  public void delete() {
    maps.delete();
  }

  @Override
  public boolean WasDeleted() {
    return false;
  }

  @Override
  public void showPauseMenu() {

  }

  @Override
  public int getTick() {
    return 0;
  }
}
