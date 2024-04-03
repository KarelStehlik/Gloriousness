package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;

import Game.Buffs.VoidFunc;
import general.Util;
import windowStuff.Button;
import windowStuff.Sprite;

public class Ability implements KeyboardDetect, TickDetect {

  public final Button button;
  public final Sprite bg;
  private final float cooldown;
  private float cooldownRemaining;
  private final VoidFunc onTrigger;
  private static int count = 0;

  public Ability(String image, float cooldown, Button.MouseoverText text, VoidFunc onTrigger) {
    bg = new Sprite("Button", 9).setSize(90, 90).addToBs(Game.get().getSpriteBatching("main"));
    button = new Button(
        Game.get().getSpriteBatching("main"),
        new Sprite(image, 10).setSize(75, 75),
        (x, y) -> this.triggered(),
        () -> text.get() + "\nCooldown:" + cooldownRemaining / 1000f
    );
    button.getSprite().setShader("colorCycle2").setColors(Util.getCycle2colors(0.5f));
    Game.get().addMouseDetect(button);
    Game.get().addTickable(button);
    Game.get().addKeyDetect(this);

    this.cooldown = cooldown;
    this.onTrigger = onTrigger;
    button.getSprite().setPosition(275 + count * 100, 50);
    bg.setPosition(275 + count * 110, 50);
    keyBind = GLFW_KEY_1 + count;
    count++;
  }

  private final int keyBind;

  private void triggered() {
    if (cooldownRemaining > 0) {
      return;
    }
    onTrigger.apply();
    cooldownRemaining += cooldown;
    button.getSprite().setShader("basic").setColors(Util.getColors(.2f, .2f, .2f));
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    if (key == keyBind && action == 1 && mods == 0) {
      triggered();
    }
  }

  @Override
  public void onGameTick(int tick) {
    if (cooldownRemaining > 0) {
      cooldownRemaining -= Game.tickIntervalMillis;
      if (cooldownRemaining <= 0) {
        button.getSprite().setShader("colorCycle2").setColors(Util.getCycle2colors(0.5f));
      }
    }
  }

  @Override
  public void delete() {
    button.delete();
  }

  @Override
  public boolean WasDeleted() {
    return button.WasDeleted();
  }
}
