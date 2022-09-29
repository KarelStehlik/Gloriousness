package Game;

import general.Util;
import windowStuff.Sprite;

public class Projectile extends GameObject implements TickDetect{
  private final Sprite sprite;
  private int pierce;
  private float speed;
  private float vx, vy;
  private float size;
  private final World world;

  protected Projectile(World world, String image, float X, float Y, float speed, float rotation, int W, int H, int pierce, float size) {
    super(X, Y, W, H);
    sprite = new Sprite(image, X, Y, W, H, 1, "basic");
    sprite.setRotation(rotation);
    world.bs.addSprite(sprite);
    this.pierce = pierce;
    this.speed = speed;
    vx = Util.cos(rotation) * speed;
    vy = Util.sin(rotation) * speed;
    this.size = size;
    this.world = world;
  }

  @Override
  public void onGameTick(int tick) {
    fly();
    handleCollisions();
    world.projectilesGrid.add(this);
  }

  private void fly(){
    move(x+vx, y+vy);
    sprite.setPosition(x, y);
  }

  private void handleCollisions(){

  }

  @Override
  public void delete() {
    sprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return pierce == 0;
  }
}
