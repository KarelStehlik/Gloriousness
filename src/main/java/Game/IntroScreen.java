package Game;

import general.Constants;
import general.Data;
import general.Util;
import general.Util.Cycle2Colors;
import java.util.List;
import windowStuff.Audio;
import windowStuff.Audio.SoundToPlay;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.ScrollingText;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;
import windowStuff.TextModifiers;

public class IntroScreen implements World {

  private final ButtonArray maps;
  private final SpriteBatching bs;

  private final ScrollingText text;

  private static final String aaah;
  static{
    StringBuilder ah = new StringBuilder("|shader:colorCycle2|#130|");
    for(int i=0;i<15;i++){
      ah.append(TextModifiers.colors(new Cycle2Colors().setyOffset(0.14f).setSpeed(i).setDensity(
          (float) (Math.sqrt(i+1)*0.2f)).get())).append('A');
    }
    aaah=ah.toString();
  }
  private static final List<String> loadingScreenTips = List.of(
      TextModifiers.colors(new Cycle2Colors().setyOffset(0.14f).get())+"shader:colorCycle2|#200|OBSERVE",
      TextModifiers.colors(new Cycle2Colors().setyOffset(0.14f).setDensity(0.6f).get())+"shader:colorCycle2|#150|PINK FLUFFY UNICORNS|#<|#<|#<|#16|#100.#100.#100|not included",
      "Scientists prove that they can't prove shit.",
      "Fun fact: There are no aliens on Earth. Especially not at 38\u00B053'51.5\"N 77\u00B002'11.5\"W. And if there are, they're not weak to guns, don't even try.",
      "Thomas Jefferson was a guy. |#40|(male)",
      "Target a mortar monkey at a bank to reduce the likelihood that it will be robbed.",
      "Why did the chicken cross the road? Because you didn't build enough defense, skill issue.",
      "Breaking news: ẨẨẨẨẨẨẨẨẨ. Alright it's fine, I fixed the news.",
      "We kindly remind everyone that bloons aren't human, which means the Geneva convention doesn't apply.",
      "Dartling gunner has a message. He says \"I GOT A |#140|HUGE|#<| GUN!\". He is not currently threatening me with said GUN. Also, he should get a raise.",
      "BIG DAMAGE is effective against bloons, whereas against MOABs you'll want |#200|"+TextModifiers.red+"HUGE DAMAGE.",
      "|#16|Path of Exile.",
      "Meth is very healthy for babies, says scientific study |#28|sponsored by Big Beautiful 'Murican Meth inc.",
      "Buying monkeys to fight a war for you is not slavery. It's actually...       Fucking |#25|Dude wtf you want me to say???         You can't be serious.          Bro- No YOU're fired!       What do you mean i'm not the boss - you know what, fuck this, i quit!     Never wanted to be a fucking reporter anyway.          Yes it IS good for me! As for you, you can suck on my - Hey! What are you calling security for?      Hey! No! Wait!",
      "If Einstein was so smart, then where is all his |#400.#350.#0|GOLD?|#<|              |#55|Also how much of it is there, what are the security measures on it, what's the guard schedule, and where are the camera blind spots?",
      "Please stand by while we launch a nuclear warhead into our backyard (i think i saw a green bloon)",
      "Camo bloons don't currently exist. Or maybe they're just hidden so well that you don't know it? |#20|#20.#20.#20|boo.",
      "Monkey wizards are proof that magic is real. Proof means wizards are now consistent with science, say scientists. Thus, magic is not real.",
      "How do you slay that which has no life? With fire, i guess? Most things die to fire.",
      "Druids harness the power of nature. Indeed, glowing green balls that destroy bloons and regrow from nothing when depleted are very natural.",
      "The dart monkey can also throw babies. It's not very effective, but hey, if you provide him with babies he can do that i guess.",
      "Necromancers drain the souls of their victims and sell them on the dark web. |#35|Anyone want some? Buy 40, get 10 free!",
      "Engineers did not help in the creation of this game. Lazy fucks.",
      " ".repeat(30)+"|speed:75|Vzoom"+" ".repeat(65),
      "|shader:rotator|SPINNING doesn't really work great",
      aaah
  );

  public IntroScreen() {
    Audio.play(new SoundToPlay("legion",0.85f, "music", true));
    bs = Game.get().getSpriteBatching("main");
    int mapCount = Data.listMaps().length;
    Button[] buttons = new Button[mapCount];
    for (int i = 0; i < mapCount; i++) {
      buttons[i] = makeMapButton(i);
    }

    new Sprite("introScreen", 2).addToBs(bs).
        setPosition(Constants.screenSize.x/2f, Constants.screenSize.y*0.65f ).
        setSize(1024, 1536).scale(0.6f).setShader("colorCycle2").
        setColors(new Cycle2Colors().setyOffset(-0.5f).setDensity(0.03f).setSpeed(0.2f).setStrength(0.05f).get());

    StringBuilder lst = new StringBuilder(" ".repeat(80));
    for(String s : Util.shuffle(loadingScreenTips, Data.unstableRng)){
      lst.append(s).append(TextModifiers.gigaReset).append(" ".repeat(35));
    }
    text = new ScrollingText(lst.toString(), 1500, 3, 100, bs, "path");
    text.setSpeed(4.5f);
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
