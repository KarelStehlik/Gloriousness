package Game;

import general.Constants;
import general.Data;
import general.Util;
import java.awt.Rectangle;
import windowStuff.Sprite;

public final class TestObject extends GameObject implements TickDetect {

  private static final String[] images = new String[]{"magic_tree", "Cancelbutton", "Intro",
      "Freeze",
      "fire", "farm", "Farm1", "Farm2", "Mancatcher", "Button", "Golem", "crab", "Defender",
      "Farm11", "Farm21", "Meteor", "mine", "Chestplates", "Boulder", "crater", "faura", "Egg",
      "Bowman", "Bullet"};
  private final Sprite sprite;
  private final float rot;
  private boolean exists;
  private float vx, vy;
  private float currentAngle = 0;
  private final SquareGrid<TestObject> grid;
  private final int radius = 75;

  public TestObject(Game game, SquareGrid<TestObject> g) {
    super(150,150,150,150);
    width = 2*radius;
    height = 2*radius;
    grid = g;
    g.add(this);
    game.addTickable(this);
    vx = Data.gameMechanicsRng.nextFloat(5);
    vy = Data.gameMechanicsRng.nextFloat(5);
    rot = Data.gameMechanicsRng.nextFloat(20)-10;
    String imageName = images[(int) (Data.unstableRng.nextFloat() * images.length)];
    sprite = new Sprite(imageName, x, y, width, height, 0, "colorCycle2");
    sprite.setColors(Util.getCycle2colors(1f));
    game.getBatchSystem("main").addSprite(sprite);
    exists = true;
  }

  @Override
  public void onGameTick(int tick) {
    x = x + vx;
    y = y + vy;
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
    if (Data.unstableRng.nextFloat() < 0.5) {
      sprite.setImage(images[(int) (Data.unstableRng.nextFloat() * images.length)]);
    }
    currentAngle += rot;
    for(TestObject t : grid.get(this)){
      float distanceSq = (x - t.x) * (x - t.x) + (y - t.y) * (y - t.y);
      int minDistance = (radius + t.radius) * (radius + t.radius);
      if(distanceSq < minDistance) {
        float dir = Util.get_rotation(x - t.x, y - t.y);
        float overlap = ((radius + t.radius) - (float)Math.sqrt(distanceSq))/2;
        float s = Util.sin(dir), c = Util.cos(dir);
        x+=overlap * c;
        y+=overlap * s;
        t.x-=overlap * c;
        t.y-=overlap * s;
      }
    }
    sprite.setRotation(currentAngle);
    sprite.setPosition(x, y);
    grid.add(this);
  }

  @Override
  public void delete() {
    sprite.delete();
    exists = false;
  }

  @Override
  public boolean WasDeleted() {
    return !exists;
  }

  @Override
  public Rectangle getHitbox() {
    return new Rectangle((int) x - width /2, (int) y + height /2, width,
        height);
  }
}
