package windowStuff;

public interface SpriteBatching {

  void addSprite(Sprite sprite);

  Camera getCamera();

  void useCamera(Camera cam);

  void draw();
}
