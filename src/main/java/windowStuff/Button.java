package windowStuff;

import Game.MouseDetect;
import Game.TickDetect;

public class Button implements MouseDetect, TickDetect {

  private final AbstractSprite sprite;
  private final ClickFunction onClick;
  private final MouseoverText mouseoverTextGenerator;
  private final Text mouseoverText;
  private boolean shown = true;

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

  public Button(AbstractSprite sprite, ClickFunction foo) {
    this.sprite = sprite;
    this.onClick = foo;
    mouseoverTextGenerator = null;
    mouseoverText = null;
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
  public int getLayer() {
    return sprite.getLayer();
  }

  @Override
  public boolean onMouseButton(int button, double x, double y, int action, int mods) {
    if (!shown) {
      return false;
    }
    if (action==1 && x > sprite.getX() - sprite.getWidth() && x < sprite.getX() + sprite.getWidth() &&
        y > sprite.getY() - sprite.getHeight() && y < sprite.getY() + sprite.getHeight()) {
      onClick.onClick(button, action);
      return true;
    }
    return false;
  }

  @Override
  public boolean onScroll(double scroll) {
    return false;
  }

  @Override
  public boolean onMouseMove(float newX, float newY) {
    if (!shown || mouseoverText == null) {
      return false;
    }
    if (newX > sprite.getX() - sprite.getWidth() && newX < sprite.getX() + sprite.getWidth() &&
        newY > sprite.getY() - sprite.getHeight() && newY < sprite.getY() + sprite.getHeight()) {
      mouseoverText.show();
      mouseoverText.setText(mouseoverTextGenerator.get());
      mouseoverText.move((int) newX, (int) newY);
      return false;
    }
    mouseoverText.hide();
    return false;
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

  @Override
  public void onGameTick(int tick) {
    if (!shown || mouseoverText == null) {
      return;
    }
    mouseoverText.setText(mouseoverTextGenerator.get());
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
