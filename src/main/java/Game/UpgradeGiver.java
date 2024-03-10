package Game;

import Game.Buffs.StatBuff;
import general.Data;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Button;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public class UpgradeGiver {

  private static final int BOTTOM = 50, LEFT = 110, BUTTON_WIDTH = 200, BUTTON_HEIGHT = 100, BUTTON_OFFSET = 120;
  private final UpgradeType[] upgradeTypes = new UpgradeType[]{new AttackSpeed(), new Damage()};
  private final AttackSpeed test = new AttackSpeed();
  private final List<Button> buttons = new ArrayList<>(2);
  private final World world;

  public UpgradeGiver(World w) {
    world = w;
  }

  private void clearOptions() {
    for (Button b : buttons) {
      b.delete();
    }
  }

  private void optionPicked(int id) {
    world.getPlayer().addBuff(
        new StatBuff<Player>(0, Float.POSITIVE_INFINITY, p -> p.stats.cd /= 2));
    clearOptions();
    world.beginWave();
  }

  public void gib(int gloriousness) {
    clearOptions();
    for (int i = 0; i < gloriousness; i++) {
      Button B = upgradeTypes[Data.gameMechanicsRng.nextInt(upgradeTypes.length)].genButton(
          world.getBs(), i);
      buttons.add(B);
      Game.get().addMouseDetect(B);
    }
  }

  private class Damage extends UpgradeType {

    @Override
    void picked() {
      world.getPlayer().addBuff(new StatBuff<Player>( 0, Float.POSITIVE_INFINITY,
          p -> p.stats.projPower *= 2));
    }

    @Override
    String getText() {
      return "Doubles your damage (and also explosion radius)";
    }

    @Override
    String getImageName() {
      return "bu";
    }
  }

  private class AttackSpeed extends UpgradeType {

    @Override
    void picked() {
      world.getPlayer().addBuff(new StatBuff<Player>( 0, Float.POSITIVE_INFINITY,
          p -> p.stats.cd /= 2));
    }

    @Override
    String getText() {
      return "Doubles your attack speed KEKW";
    }

    @Override
    String getImageName() {
      return "Button";
    }
  }

  private abstract class UpgradeType {

    public final Button genButton(SpriteBatching batch, int offset) {
      return new Button(batch,
          new Sprite(getImageName(), 10).setSize(BUTTON_WIDTH, BUTTON_HEIGHT)
              .setPosition(LEFT, BOTTOM + offset * BUTTON_OFFSET),
          (b, a) -> this._picked(a), this::getText);
    }

    private void _picked(int action) {
      if (action == 0) {
        return;
      }
      clearOptions();
      world.beginWave();
      picked();
    }

    abstract void picked();

    abstract String getText();

    abstract String getImageName();
  }
}
