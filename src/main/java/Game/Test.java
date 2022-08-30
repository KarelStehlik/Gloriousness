package Game;

import general.Constants;
import general.Data;
import windowStuff.Sprite;

public class Test implements TickDetect {

  private final Sprite sprite;
  private float vx, vy, x, y;
  private final float rot;
  private float currentAngle = 0;

  public Test(Game game) {
    game.addTickable(this);
    vx = Data.gameMechanicsRng.nextFloat(30);
    vy = Data.gameMechanicsRng.nextFloat(30);
    rot = Data.gameMechanicsRng.nextFloat(5);
    sprite = new Sprite("Farm21", 150, 150, 5, 5, 0, "basic");
    game.getBatchSystem("main").addSprite(sprite);
  }

  @Override
  public void onGameTick() {
    x += vx;
    y += vy;
    if (x < 0) {
      x *= -1;
      vx *= -1;
    } else if (x > Constants.screenSize.x) {
      x = 2 * Constants.screenSize.x - x;
      vx *= -1;
    }
    if (y < 0) {
      y *= -1;
      vy *= -1;
    } else if (y > Constants.screenSize.y) {
      y = 2 * Constants.screenSize.y - y;
      vy *= -1;
    }
    sprite.setPosition(x, y);
    currentAngle += rot;
    sprite.setRotation(currentAngle);
  }

  @Override
  public void delete() {
    sprite.delete();
  }
}
