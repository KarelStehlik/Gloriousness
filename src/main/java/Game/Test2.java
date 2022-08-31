package Game;

import java.util.LinkedList;
import java.util.List;
import windowStuff.BatchSystem;
import windowStuff.Sprite;

public class Test2 implements TickDetect{
  private List<Sprite> sprites = new LinkedList<>();
  private BatchSystem batch;

  public Test2(Game game){
    batch=game.getBatchSystem("basic");
    game.addTickable(this);
    for (int i=0;i<100;i++){
      Sprite s = new Sprite("Farm21", 150, 150, 50, 50, 0, "basic");
      sprites.add(s);
      batch.addSprite(s);
    }
  }

  @Override
  public void onGameTick(int tick) {
    for (int i=0;i<1000;i++){
      Sprite s = new Sprite("Farm21", 350, 150, 50, 50, 0, "basic");
      sprites.add(s);
      batch.addSprite(s);
      sprites.remove(0).delete();
    }
  }

  @Override
  public void delete() {
  }

  @Override
  public boolean ShouldDeleteThis() {
    return false;
  }
}
