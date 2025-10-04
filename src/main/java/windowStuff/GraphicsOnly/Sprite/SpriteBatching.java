package windowStuff.GraphicsOnly.Sprite;

import windowStuff.GraphicsOnly.Camera;

public interface SpriteBatching {

  void addSprite(Sprite sprite);

  Camera getCamera();

  void useCamera(Camera cam);

  void draw();

  void nuke();
}
