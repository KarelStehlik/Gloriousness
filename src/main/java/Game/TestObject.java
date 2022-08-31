package Game;

import general.Constants;
import general.Data;
import windowStuff.Sprite;

public class TestObject implements TickDetect {

  private final Sprite sprite;
  private final float rot;
  protected boolean exists;
  private float vx, vy, x, y;
  private float currentAngle = 0;

  public TestObject(Game game) {
    game.addTickable(this);
    vx = Data.gameMechanicsRng.nextFloat(30);
    vy = Data.gameMechanicsRng.nextFloat(30);
    rot = Data.gameMechanicsRng.nextFloat(5);
    sprite = new Sprite("Farm21", 150, 150, 50, 50, 0, "basic");
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
