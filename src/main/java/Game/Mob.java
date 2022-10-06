package Game;

import general.Constants;
import general.Data;
import general.Util;
import java.awt.Rectangle;
import java.util.Map;
import windowStuff.Sprite;

public abstract class Mob extends GameObject implements TickDetect{
  private static final String[] images = new String[]{"magic_tree", "Cancelbutton", "Intro",
      "Freeze",
      "fire", "farm", "Farm1", "Farm2", "Mancatcher", "Button", "Golem", "crab", "Defender",
      "Farm11", "Farm21", "Meteor", "mine", "Chestplates", "Boulder", "crater", "faura", "Egg",
      "Bowman", "Bullet"};
  final Map<String, Float> stats;
  final String name;
  protected final Sprite sprite;
  protected final float rotation;
  protected boolean exists;
  protected float vx, vy;
  protected float currentAngle = 0;
  protected final SquareGrid<Mob> grid;

  public Mob(World world, String name) {
    super(150,150,150,150, world);
    stats = Data.getEntityStats("mob", name);
    this.name = name;
    setSize((int) (2*stats.get("size")), (int) (2*stats.get("size")));
    grid = world.getMobsGrid();
    vx = Data.gameMechanicsRng.nextFloat(stats.get("speed"));
    vy = Data.gameMechanicsRng.nextFloat(stats.get("speed"));
    rotation = Data.gameMechanicsRng.nextFloat(20)-10;
    String imageName = images[(int) (Data.unstableRng.nextFloat() * images.length)];
    sprite = new Sprite("Bowman", x, y, width, height, 0, "colorCycle2");
    sprite.setColors(Util.getCycle2colors(.66f));
    world.getBs().addSprite(sprite);
    exists = true;
  }

  @Override
  public void onGameTick(int tick) {
    runAI();
    handleCollisions();
    grid.add(this);
    miscTickActions();
  }

  private void miscTickActions(){
    if (Data.unstableRng.nextFloat() < 0.5) {
      sprite.setImage(images[(int) (Data.unstableRng.nextFloat() * images.length)]);
    }
    sprite.setRotation(currentAngle);
    sprite.setPosition(x, y);
  }

  private void handleCollisions(){
    grid.callForEach(this.getHitbox(), this::collide);
  }

  private void runAI(){
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
    currentAngle += rotation;
  }

  private void collide(Mob other){
    if(!other.canCollide){
      return;
    }
    float distanceSq = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
    int minDistance = (int) Util.square(stats.get("size") + other.stats.get("size"));
    if(distanceSq < minDistance) {
      float dir = Util.get_rotation(x - other.x, y - other.y);
      float overlap = ((stats.get("size") + other.stats.get("size")) - (float)Math.sqrt(distanceSq))/2;
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
  public boolean WasDeleted() {
    return !exists;
  }

  @Override
  public Rectangle getHitbox() {
    return new Rectangle((int) x - width /2, (int) y + height /2, width,
        height);
  }
}
