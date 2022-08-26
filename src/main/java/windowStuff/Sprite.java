package windowStuff;

import java.util.Arrays;
import org.joml.Vector2f;

public class Sprite {
  protected float[] vertices;
  protected boolean hasChanged;

  public Sprite(float x, float y, float size){
    Vector2f BL = new Vector2f(x-size, y-size);
    Vector2f TR = new Vector2f(x+size, y+size);
    vertices = new float[]{
        // x y z r g b a u v
        TR.x, BL.y, 1, 1, 1, 1, 1, 1, 0,// +-
        BL.x, TR.y, 1, 1, 1, 1, 1, 0, 1,// -+
        TR.x, TR.y, 1, 1, 1, 1, 1, 1, 1,// ++
        BL.x, BL.y, 1, 1, 1, 1, 1, 0, 0// --
    };
    hasChanged = true;
  }

  public void setPosition(Vector2f pos){

  }
}
