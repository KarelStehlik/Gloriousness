package windowStuff;

import Game.MouseDetect;

public class Button implements MouseDetect {

  private final AbstractSprite sprite;
  private final ClickFunction onClick;
  private final MouseoverText mouseoverTextGenerator;
  private final Text mouseoverText;
  private boolean shown = true;
  private SpriteBatching bs;

  public Button(SpriteBatching bs, AbstractSprite sprite, ClickFunction foo,
      MouseoverText caption) {
    this.sprite = sprite;
    this.onClick = foo;
    mouseoverTextGenerator = caption;
    sprite.addToBs(bs);
    mouseoverText = caption == null ? null
        : new Text(caption.get(), "Calibri", 300, 0, 0, sprite.getLayer() + 1,
            40, bs, "basic", "textbox");

    if (mouseoverText != null) {
      mouseoverText.hide();
    }
  }

  public AbstractSprite getSprite() {
    return sprite;
  }

  public void hide() {
    if (!shown) {
      return;
    }
    sprite.setHidden(true);
    shown = false;
    if (mouseoverText != null) {
      mouseoverText.hide();
    }
  }

  public void show() {
    if (shown) {
      return;
    }
    sprite.setHidden(false);
    shown = true;
  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {
    if (!shown) {
      return;
    }
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
    if (!shown || mouseoverText == null) {
      return;
    }
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
    if (mouseoverText != null) {
      mouseoverText.delete();
    }
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
