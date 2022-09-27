package Game;

import java.awt.Rectangle;

public class GameObject {
  float x, y;
  int width, height;

  protected GameObject(float X, float Y, int W, int H){
    x=X;y=Y;width=W;height=H;
  }

  Rectangle getHitbox(){
    return new Rectangle((int) (x - width /2), (int) (y + height /2), width
        /2, height /2);
  }
}
