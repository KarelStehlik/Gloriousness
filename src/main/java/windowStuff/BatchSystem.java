package windowStuff;

import static windowStuff.Batch.MAX_BATCH_SIZE;

import java.util.LinkedList;
import java.util.List;

public class BatchSystem{
  private final List<Batch> batches;

  public BatchSystem(){
    batches = new LinkedList<>(); // is sorted
  }

  public void addSprite(Sprite sprite){
    // find an available batch, if it exists
    int index = 0; // at which index do the batches have the correct layer?
    for(Batch batch : batches){
      if(batch.layer < sprite.layer){
        index++;
        continue;
      }
      if(batch.layer > sprite.layer){
        break;
      }
      if(batch.textureName.equals(sprite.textureName) && !batch.freeSpriteSlots.isEmpty()){
        batch.addSprite(sprite);
        return;
      }
    }

    // available batch does not exist, create one
    Batch batch = new Batch(sprite.textureName, MAX_BATCH_SIZE, "basic", sprite.layer);
    batch.addSprite(sprite);
    batches.add(index, batch); // keep the list sorted
  }

  public void draw(){
    for(Batch batch:batches){
      batch.draw();
    }
  }

  public void useCamera(Camera camera){
    for(Batch batch: batches){
      batch.useCamera(camera);
    }
  }
}