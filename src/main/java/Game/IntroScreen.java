package Game;

import general.Constants;
import general.Data;
import general.Log;
import general.Util;
import java.util.List;
import windowStuff.Audio;
import windowStuff.Audio.SoundToPlay;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.OtherText;
import windowStuff.ScrollingText;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;
import windowStuff.TextModifiers;

public class IntroScreen implements World {

  private final ButtonArray maps;
  private final SpriteBatching bs;

  private final ScrollingText text;

  private static final List<String> loadingScreenTips = List.of(
      TextModifiers.colors(Util.getCycle2colors(1))+"shader:colorCycle2|#200|OBSERVE",
      TextModifiers.colors(Util.getCycle2colors(1))+"shader:colorCycle2|#150|PINK FLUFFY UNICORNS|#<|#<|#<|#16|#100.#100.#100|not included",
      "Scientists prove that they can't prove shit",
      "Fun fact: There are no aliens on Earth. Especially not at 38\u00B053'51.5\"N 77\u00B002'11.5\"W. And if there are, they're not weak to guns, don't even try.",
      "Thomas Jefferson was a guy. |#40|(male)",
      "Target a mortar monkey at a bank to reduce the likelihood that it will be robbed.",
      "Why did the chicken cross the road? Because you didn't build enough defense, skill issue.",
      "BIG DAMAGE is effective against bloons, whereas against MOABs you'll want |#200|HUGE DAMAGE.",
      "Breaking news: ẨẨẨẨẨẨẨẨẨ. Alright it's fine, I fixed the news.",
      "We kindly remind everyone that balloons aren't human, which means the Geneva convention doesn't apply.",
      "Dartling gunner has a message. He says \"I GOT A |#140|HUGE|#<| GUN!\". He is not currently threatening me with said GUN. Also, he should get a raise."
  );

  public IntroScreen() {
    Audio.play(new SoundToPlay("legion",0.85f, "music", true));
    bs = Game.get().getSpriteBatching("main");
    int mapCount = Data.listMaps().length;
    Button[] buttons = new Button[mapCount];
    for (int i = 0; i < mapCount; i++) {
      buttons[i] = makeMapButton(i);
    }

    StringBuilder lst = new StringBuilder(" ".repeat(80));
    for(String s : Util.shuffle(loadingScreenTips, Data.unstableRng)){
      lst.append(s).append(TextModifiers.gigaReset).append(" ".repeat(15));
    }
    text = new ScrollingText(lst.toString(), 1500, 3, 100, bs, "path");
    text.move(200,200);

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
    text.update();
  }

  @Override
  public void delete() {
    maps.delete();
    Game.get().nuke();
    Audio.getGroup("music").clear();
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
