package windowStuff;

import Game.MouseDetect;

public class Button implements MouseDetect {

  private final Sprite sprite;
  private final ClickFunction onClick;
  private final MouseoverText mouseoverTextGenerator;
  private final Text mouseoverText;

  public Button(BatchSystem bs, Sprite sprite, ClickFunction foo, MouseoverText caption) {
    this.sprite = sprite;
    this.onClick = foo;
    mouseoverTextGenerator = caption;
    bs.addSprite(sprite);
    mouseoverText = new Text(caption.get(), "Calibri", 300, 0, 0, sprite.getLayer(),
        40, bs, "basic", "Cancelbutton");
    mouseoverText.hide();
  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {
    if (x > sprite.getX() - sprite.getWidth() && x < sprite.getX() + sprite.getWidth() &&
        y > sprite.getY() - sprite.getHeight() && y < sprite.getY() + sprite.getHeight()) {
      onClick.onClick(button, action);
    }
  }

  @Override
  public void onScroll(double scroll) {

  }

  @Override
  public void onMouseMove(float newX, float newY) {
    if (newX > sprite.getX() - sprite.getWidth() && newX < sprite.getX() + sprite.getWidth() &&
        newY > sprite.getY() - sprite.getHeight() && newY < sprite.getY() + sprite.getHeight()) {
      mouseoverText.show();
      mouseoverText.setText(mouseoverTextGenerator.get());
      mouseoverText.move((int) newX, (int) newY);
    } else {
      mouseoverText.hide();
    }
  }

  @Override
  public void delete() {
    sprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return sprite.isDeleted();
  }

  @FunctionalInterface
  public interface ClickFunction {

    void onClick(int button, int action);
  }

  @FunctionalInterface
  public interface MouseoverText {

    String get();
  }
}
