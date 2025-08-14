package windowStuff;

import Game.MouseDetect;
import Game.TickDetect;

public class Button implements MouseDetect, TickDetect {

  private final AbstractSprite sprite;
  private final ClickFunction onClick;
  protected boolean pressed = false;

  public Text getMouseoverText() {
    return mouseoverText;
  }

  private final Text mouseoverText;
  private boolean shown = true;

  public Button(SpriteBatching bs, AbstractSprite sprite, ClickFunction foo,
      Text caption) {
    this.sprite = sprite;
    this.onClick = foo;
    sprite.addToBs(bs);
    mouseoverText = caption;
    if (mouseoverText != null) {
      mouseoverText.hide();
    }
  }

  public Button(SpriteBatching bs, AbstractSprite sprite, ClickFunction foo,
      SimpleText.TextGenerator caption) {
    this.sprite = sprite;
    this.onClick = foo;
    sprite.addToBs(bs);
    mouseoverText = caption == null ? null
        : new SimpleText(caption, "Calibri", 450, 0, 0, sprite.getLayer() + 10,
            35, bs, "basic", "textbox");

    if (mouseoverText != null) {
      mouseoverText.hide();
    }
  }

  public Button(SpriteBatching bs, AbstractSprite sprite,
      ClickFunction foo) { //I dunno if this should ever be used,
    // because it looks kinda sus ngl
    this(sprite, foo);
    sprite.addToBs(bs);
  }

  public Button(AbstractSprite sprite, ClickFunction foo) {
    this.sprite = sprite;
    this.onClick = foo;
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
    if (action == 1 && x > sprite.getX() - sprite.getWidth()
        && x < sprite.getX() + sprite.getWidth() &&
        y > sprite.getY() - sprite.getHeight() && y < sprite.getY() + sprite.getHeight()) {
      onClick.onClick(button, action);
      pressed = true;
      return true;
    }
    if (action == 0) {
      pressed = false;
    }
    return false;
  }

  public void trigger(){
    onClick.onClick(0, 1);
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
      mouseoverText.update();
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
    mouseoverText.update();
  }

  @FunctionalInterface
  public interface ClickFunction {

    void onClick(int button, int action);
  }

}
