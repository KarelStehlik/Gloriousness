package Game;

import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.Tag;
import Game.Player.Stats;
import general.Data;
import general.Util;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Button;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public class UpgradeGiver {

  private static final int BOTTOM = 50, LEFT = 110, BUTTON_WIDTH = 200, BUTTON_HEIGHT = 100, BUTTON_OFFSET = 120;
  private final UpgradeType[] upgradeTypes = new UpgradeType[]{new AttackSpeed(), new Damage(),
      new Pierce(), new Explode()};
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

  public void gib(int gloriousness) {
    clearOptions();
    for (int i = 0; i < Math.min(gloriousness, 5); i++) {
      Button B = upgradeTypes[Data.gameMechanicsRng.nextInt(upgradeTypes.length)].genButton(
          world.getBs(), i);
      buttons.add(B);
      Game.get().addMouseDetect(B);
    }
  }

  private class Damage extends UpgradeType {

    @Override
    void picked() {
      world.getPlayer().addBuff(new StatBuff<Player>(Type.ADDED,
          Stats.projPower, 1));
    }

    @Override
    String getText() {
      return "+1 damage";
    }

    @Override
    String getImageName() {
      return "bu";
    }
  }

  private class Pierce extends UpgradeType {

    @Override
    void picked() {
      world.getPlayer().addBuff(new StatBuff<Player>(Type.ADDED,
          Stats.projPierce, 2));
    }

    @Override
    String getText() {
      return "+2 pierce";
    }

    @Override
    String getImageName() {
      return "Meteor";
    }
  }

  private class AttackSpeed extends UpgradeType {

    @Override
    void picked() {
      world.getPlayer().addBuff(new StatBuff<Player>(Type.MORE,
          Player.Stats.cd, 1 / 1.3f));
    }

    @Override
    String getText() {
      return "30% more attack speed";
    }

    @Override
    String getImageName() {
      return "Button";
    }
  }

  private class Explode extends UpgradeType {

    float radius = 0;
    long id = Util.getUid();

    @Override
    void picked() {
      if (world.getPlayer().addBuff(new Tag<Player>(id))) {
        world.getPlayer().getBulletLauncher()
            .addMobCollide((proj, mob) -> BasicCollides.explodeFunc(
                (int) proj.getX(), (int) proj.getY(), proj.getPower(), this.radius));
      }
      radius += 50;
    }

    @Override
    String getText() {
      return "Exploding projectiles. If already exploding, increases the radius instead.";
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
