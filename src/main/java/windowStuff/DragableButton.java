package windowStuff;

public class DragableButton extends Button{

  public DragableButton(SpriteBatching bs, AbstractSprite sprite, ClickFunction foo,
      MouseoverText caption) {
    super(bs, sprite, foo, caption);
  }

  public DragableButton(AbstractSprite sprite, ClickFunction foo) {
    super(sprite, foo);
  }

  @Override
  public boolean onMouseMove(float newX, float newY) {
    if(pressed){
      getSprite().setPosition(newX,newY);
    }
    return super.onMouseMove(newX,newY);
  }
}
