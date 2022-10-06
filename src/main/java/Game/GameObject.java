package Game;

import java.awt.Rectangle;

public class GameObject {
  protected float x, y;
  protected int width, height;
  protected boolean canCollide = true;
  protected World world;

  protected GameObject(float X, float Y, int W, int H, World w){
    x=X;y=Y;width=W;height=H;
    world=w;
  }

  Rectangle getHitbox(){
    return new Rectangle((int) (x - width /2), (int) (y + height /2), width
        /2, height /2);
  }

  void move(float _x, float _y){
    x = _x; y = _y;
  }

  void setSize(int _width, int _height){
    width = _width; height = _height;
  }
}
