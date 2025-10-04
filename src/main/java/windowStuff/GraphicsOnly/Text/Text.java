package windowStuff.GraphicsOnly.Text;

public interface Text {

  int getX();

  int getY();

  void update();

  void hide();

  void show();

  void move(int newX, int newY);

  void delete();
}
