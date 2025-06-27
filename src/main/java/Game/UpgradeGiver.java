package Game;

import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.Tag;
import Game.Enums.DamageType;
import Game.Player.Stats;
import general.Data;
import general.Util;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import windowStuff.Button;
import windowStuff.Graphics;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public class UpgradeGiver {

  private static final int BOTTOM = 50, LEFT = 110, BUTTON_WIDTH = 200, BUTTON_HEIGHT = 100, BUTTON_OFFSET = 120;
  private final UpgradeType[] upgradeTypes = new UpgradeType[]{new AttackSpeed(), new Damage(),
      new Pierce(), new Explode()};
  private final List<Button> buttons = new ArrayList<>(2);
  private final World world;
  private final Queue<Integer> requested = new LinkedList<>();

  public UpgradeGiver(World w) {
    world = w;
  }

  private void clearOptions() {
    for (Button b : buttons) {
      b.delete();
    }
    buttons.clear();
  }

  private void LoadNewUpgrades() {
    if (requested.isEmpty()) {
      return;
    }
    int gloriousness = requested.poll();
    for (int i = 0; i < Math.min(gloriousness, 5); i++) {
      Button B = upgradeTypes[Data.gameMechanicsRng.nextInt(upgradeTypes.length)].genButton(
          world.getBs(), i);
      buttons.add(B);
      Game.get().addMouseDetect(B);
    }
  }

  public void gib(int gloriousness) {
    if (gloriousness == 0) {
      return;
    }
    requested.add(gloriousness);
    if (buttons.isEmpty()) {
      LoadNewUpgrades();
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
      return "+1 damage. currently " + world.getPlayer().stats[Stats.projPower];
    }

    @Override
    String getImageName() {
      return "Damage";
    }
  }

  private class Pierce extends UpgradeType {

    @Override
    void picked() {
      world.getPlayer().addBuff(new StatBuff<Player>(Type.ADDED,
          Stats.projPierce, 1));
    }

    @Override
    String getText() {
      return "+1 pierce. currently " + world.getPlayer().stats[Stats.projPierce];
    }

    @Override
    String getImageName() {
      return "Pierce";
    }
  }

  private class AttackSpeed extends UpgradeType {

    @Override
    void picked() {
      world.getPlayer().addBuff(new StatBuff<Player>(Type.INCREASED,
          Player.Stats.aspd, .4f));
    }

    @Override
    String getText() {
      return "40% increased attack speed. currently " + world.getPlayer().stats[Stats.aspd];
    }

    @Override
    String getImageName() {
      return "Aspd";
    }
  }

  private class Explode extends UpgradeType {

    float radius = 50;
    long id = Util.getUid();

    @Override
    void picked() {
      if (world.getPlayer().addBuff(new Tag<Player>(id))) {
        world.getPlayer().getBulletLauncher().setImage(Graphics.getImage("Bomb-0"));
        world.getPlayer().addBuff(new StatBuff<Player>(Type.INCREASED, Stats.projSize, 3f));
        world.getPlayer().getBulletLauncher()
            .addMobCollide((proj, mob) -> {
              world.aoeDamage((int) proj.getX(), (int) proj.getY(), (int) this.radius,
                  proj.getPower(), DamageType.TRUE);
              world.lesserExplosionVisual((int) proj.getX(), (int) proj.getY(), (int) this.radius)
                  .getSprite().setOpacity(.8f);
              return true;
            }, 0);
      } else {
        radius += 2000 / radius;
      }
    }

    @Override
    String getText() {
      return "Exploding projectiles. If already exploding, increases the area instead.";
    }

    @Override
    String getImageName() {
      return "Radius";
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
      picked();
      LoadNewUpgrades();
    }

    abstract void picked();

    abstract String getText();

    abstract String getImageName();
  }
}
