package windowStuff;

public interface Text {

    public int getX();
    public int getY();
    public void update();
    public void hide();
    public void show();
    public void move(int newX, int newY);
    public void delete();

}
