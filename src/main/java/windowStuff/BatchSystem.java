package windowStuff;

import static windowStuff.Batch.MAX_BATCH_SIZE;

import general.Data;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BatchSystem {

  //private final List<Batch> batches;

  protected final Map<Shader, List<Batch>> batches;

  private final List<Sprite> spritesToAdd = new LinkedList<>();

  private Camera camera;

  public BatchSystem() {
    //batches = new LinkedList<>(); // is sorted
    Collection<Shader> shaders = Data.getAllShaders();
    batches = new HashMap<Shader, List<Batch>>(shaders.size());
    for (Shader shader : shaders) {
      batches.put(shader, new LinkedList<Batch>());
    }
  }

  public void addSprite(Sprite sprite) {
    synchronized (spritesToAdd) {
      spritesToAdd.add(sprite);
    }
  }

  private void _addSprite(Sprite sprite) {
    // find an available batch, if it exists
    List<Batch> batchList = batches.get(sprite.shader);
    int index = 0; // at which index do the batches have the correct layer?
    for (Batch batch : batchList) {
      if (batch.layer < sprite.layer) {
        index++;
        continue;
      }
      if (batch.layer > sprite.layer) {
        break;
      }
      if (batch.textureName.equals(sprite.textureName) && !batch.freeSpriteSlots.isEmpty()) {
        batch.addSprite(sprite);
        return;
      }
    }

    // available batch does not exist, create one
    Batch batch = new Batch(sprite.textureName, MAX_BATCH_SIZE, "basic", sprite.layer, this);
    batch.addSprite(sprite);
    batchList.add(index, batch); // keep the list sorted
  }


  public void draw() {
    _useCamera();
    synchronized (spritesToAdd) {
      while (!spritesToAdd.isEmpty()) {
        _addSprite(
            spritesToAdd.remove(0)); // do this in the graphics thread so that context is current
      }
    }
    for (Entry<Shader, List<Batch>> entry : batches.entrySet()) {
      entry.getKey().use();
      entry.getKey().uploadTexture("sampler", 0);
      var iter = entry.getValue().iterator();
      while (iter.hasNext()) {
        Batch batch = iter.next();
        batch.draw();
        if (batch.isEmpty) {
          batch.delete();
          iter.remove();
        }
      }
    }
  }


  private void _useCamera(){
    for (Shader shader : batches.keySet()) {
      shader.useCamera(camera);
    }
  }

  public void useCamera(Camera camera) {
    this.camera = camera;
  }

  public Camera getCamera(){
    return camera;
  }
}