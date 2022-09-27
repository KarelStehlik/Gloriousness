package Game;

import general.Constants;
import general.Data;
import general.Util;
import java.awt.Rectangle;
import windowStuff.Sprite;

public class Enemy extends GameObject implements TickDetect{
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
  private final SquareGrid<Enemy> grid;
  private final int radius = 10;

  public Enemy(World world) {
    super(150,150,150,150);
    width = 2*radius;
    height = 2*radius;
    grid = world.mobs;
    grid.add(this);
    world.addTickable(this);
    vx = Data.gameMechanicsRng.nextFloat(20);
    vy = Data.gameMechanicsRng.nextFloat(20);
    rot = Data.gameMechanicsRng.nextFloat(20)-10;
    String imageName = images[(int) (Data.unstableRng.nextFloat() * images.length)];
    sprite = new Sprite(imageName, x, y, width, height, 0, "basic");
    //sprite.setColors(Util.getCycle2colors(1f));
    world.bs.addSprite(sprite);
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
   //for(Enemy other : grid.get(this)){
   //  collide(other);
   //}
    grid.callForEach(this.getHitbox(), this::collide);
    sprite.setRotation(currentAngle);
    sprite.setPosition(x, y);
    grid.add(this);
  }

  private void collide(Enemy other){
    float distanceSq = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
    int minDistance = (radius + other.radius) * (radius + other.radius);
    if(distanceSq < minDistance) {
      float dir = Util.get_rotation(x - other.x, y - other.y);
      float overlap = ((radius + other.radius) - (float)Math.sqrt(distanceSq))/2;
      float sin = Util.sin(dir), cos = Util.cos(dir);
      x+=overlap * cos;
      y+=overlap * sin;
      other.x-=overlap * cos;
      other.y-=overlap * sin;
    }
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

  @Override
  public Rectangle getHitbox() {
    return new Rectangle((int) x - width /2, (int) y + height /2, width,
        height);
  }
}
