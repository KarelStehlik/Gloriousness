package Game;

import general.Constants;
import general.Data;
import windowStuff.Sprite;

public final class TestObject implements TickDetect {

  private final Sprite sprite;
  private final float rot;
  private boolean exists;
  private float vx, vy, x, y;
  private float currentAngle = 0;
  private static final String[] images = new String[]{"magic_tree", "Cancelbutton", "Intro", "Freeze",
      "fire", "farm", "Farm1", "Farm2", "Mancatcher", "Button", "Golem", "crab", "Defender",
      "Farm11", "Farm21", "Meteor", "mine", "Chestplates", "Boulder", "crater", "faura", "Egg",
      "Bowman", "Bullet"};

  public TestObject(Game game) {
    game.addTickable(this);
    vx = Data.gameMechanicsRng.nextFloat(10);
    vy = Data.gameMechanicsRng.nextFloat(10);
    rot = Data.gameMechanicsRng.nextFloat(5);
    String imageName = images[(int)(Data.unstableRng.nextFloat()*images.length)];
    sprite = new Sprite(imageName, 150, 150, 50, 50, 0, "basic");
    game.getBatchSystem("main").addSprite(sprite);
    exists = true;
  }

  @Override
  public void onGameTick(int tick) {
    x += vx;
    y += vy;
    if (x < 0) {
      x = -x;
      vx = -vx;
    } else if (x > Constants.screenSize.x) {
      x = 2 * Constants.screenSize.x - x;
      vx = -vx;
    }
    if (y < 0) {
      y = -y;
      vy = -vy;
    } else if (y > Constants.screenSize.y) {
      y = 2 * Constants.screenSize.y - y;
      vy = -vy;
    }
    sprite.setPosition(x, y);
    if(Data.unstableRng.nextFloat()<0.5) {
      sprite.setImage(images[(int) (Data.unstableRng.nextFloat() * images.length)]);
    }
    currentAngle += rot;
    sprite.setRotation(currentAngle);
  }

  @Override
  public void delete() {
    sprite.delete();
    exists = false;
  }

  @Override
  public boolean ShouldDeleteThis() {
    return !exists;
  }
}
