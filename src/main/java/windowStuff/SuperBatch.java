package windowStuff;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11C.GL_POINTS;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengles.GLES20.GL_STREAM_DRAW;

import general.Constants;
import general.Data;
import java.util.ArrayList;
import java.util.List;

public class SuperBatch implements SpriteBatching {

  private final List<Sprite> spritesToAdd = new ArrayList<>(1);
  private final int vao;
  private final List<Batch> batches = new ArrayList<>(1);
  private final ImageSet images;
  private final ArrayList<Sprite> spritesToRebatch = new ArrayList<>(1);
  private Camera camera;
  private boolean visible = true;
  private boolean nukeNextTick = false;


  private static int attribArray = 0;
  private static int position=0;
  static void attribPointer(int count){
    int vertexBytes = Constants.SpriteSizeFloats * Float.BYTES;
    glEnableVertexAttribArray(attribArray);
    glVertexAttribPointer(attribArray, count, GL_FLOAT, false, vertexBytes, position);
    attribArray++;
    position+=count*Float.BYTES;
  }

  public SuperBatch() {
    this.images = Graphics.getLoadedImages();

    vao = glGenVertexArrays();
    glBindVertexArray(vao);

    {
      int positionCount = 2;
      int colorCountPerVertex = 4;
      int texCoords = 4;
      int sizeCount = 2;
      int rotationCount=1;

      Graphics.vbo.bind();


      position=0;
      attribArray=0;

      attribPointer(positionCount);
      attribPointer(colorCountPerVertex);
      attribPointer(colorCountPerVertex);
      attribPointer(colorCountPerVertex);
      attribPointer(colorCountPerVertex);
      attribPointer(texCoords);
      attribPointer(sizeCount);
      attribPointer(rotationCount);
    }

    glBindVertexArray(0);
  }


  public void show() {
    visible = true;
  }

  public void hide() {
    visible = false;
  }

  @Override
  public void addSprite(Sprite sprite) {
    synchronized (spritesToAdd) {
      spritesToAdd.add(sprite);
    }
  }

  @Override
  public Camera getCamera() {
    return camera;
  }

  @Override
  public void useCamera(Camera cam) {
    this.camera = cam;
  }

  @Override
  public void draw() {
    if (nukeNextTick) {
      nukeNextTick = false;
      batches.clear();
      spritesToRebatch.clear();
    }
    if (!visible) {
      return;
    }

    for (Shader shader : Data.getAllShaders()) {
      shader.useCamera(camera);
    }

    for (Batch b : batches) {
      var spriterator = b.sprites.iterator();
      while (spriterator.hasNext()) {
        final Sprite s = spriterator.next();
        if (s.mustBeRebatched) {
          spriterator.remove();
          spritesToRebatch.add(s);
        }
      }
    }

    synchronized (spritesToAdd) {
      for (Sprite sprite : spritesToAdd) {
        _addSprite(sprite);
      }
      spritesToAdd.clear();
    }

    for (Sprite sprite : spritesToRebatch) {
      _addSprite(sprite);
    }
    spritesToRebatch.clear();

    int drawStart = 0;
    while (drawStart < batches.size()) {
      String texture = batches.get(drawStart).texture;
      Shader shader = batches.get(drawStart).shader;
      int spriteCount = batches.get(drawStart).squishSize();

      int drawEnd = drawStart + 1;

      while (drawEnd < batches.size() && batches.get(drawEnd).texture.equals(texture)
          && batches.get(drawEnd).shader.equals(shader)) {
        spriteCount += batches.get(drawEnd).squishSize();
        drawEnd++;
      }

      glBindVertexArray(this.vao);

      Graphics.vbo.alloc(spriteCount * Constants.SpriteSizeFloats * Float.BYTES, GL_STREAM_DRAW);

      for (int i = drawStart; i < drawEnd; i++) {
        synchronized (batches.get(i).sprites) {
          for (Sprite s : batches.get(i).sprites) {
            s.updateVertices();
            spriteCount -= s.buffer(Graphics.vbo);
          }
        }
      }

      Graphics.vbo.doneBuffering();
      shader.use();
      images.getTexture(texture).bind();
      shader.uploadTexture("sampler", 0);
      glActiveTexture(GL_TEXTURE0);

      glDrawArrays(GL_POINTS, 0, spriteCount);

      /*shader.detach();
      glBindVertexArray(0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);*/

      drawStart = drawEnd;
    }
  }

  @Override
  public void nuke() {
    nukeNextTick = true;
    synchronized (spritesToAdd) {
      spritesToAdd.clear();
    }
  }

  private void _addSprite(Sprite sprite) {
    sprite.mustBeRebatched = false;
    int layer = sprite.layer;
    String tex = sprite.textureName;
    Shader shader = sprite.shader;

    //find the right batch. todo: maybe a bin search?
    int smallerBatches = 0;
    while (smallerBatches < batches.size()
        && batches.get(smallerBatches).compareTo(layer, tex, shader) < 0) {
      smallerBatches++;
    }
    if (smallerBatches < batches.size()
        && batches.get(smallerBatches).compareTo(layer, tex, shader) == 0) {
      batches.get(smallerBatches).addSprite(sprite);
      return;
    }
    Batch b = new Batch(layer, tex, shader);
    b.addSprite(sprite);
    synchronized (batches) {
      batches.add(smallerBatches, b);
    }
  }

  private static class Batch implements Comparable<Batch> {

    public final int layer;
    public final String texture;
    public final Shader shader;
    private final ArrayList<Sprite> sprites = new ArrayList<>(10);

    Batch(int layer, String texture, Shader shader) {
      this.layer = layer;
      this.texture = texture;
      this.shader = shader;
    }

    /**
     * removes deleted sprites.
     *
     * @return number of sprites
     */
    int squishSize() {
      synchronized (sprites) {
        sprites.removeIf(Sprite::isDeleted);
        return sprites.size();
      }
    }

    void addSprite(Sprite s) {
      synchronized (sprites) {
        sprites.add(s);
      }
    }

    @Override
    public int compareTo(Batch o) {
      if (layer - o.layer != 0) {
        return layer - o.layer;
      }
      if (texture.compareTo(o.texture) != 0) {
        return texture.compareTo(o.texture);
      }
      return shader.shaderID - o.shader.shaderID;
    }

    public int compareTo(int lay, String tex, Shader shade) {
      if (layer - lay != 0) {
        return layer - lay;
      }
      if (texture.compareTo(tex) != 0) {
        return texture.compareTo(tex);
      }
      return shader.shaderID - shade.shaderID;
    }

    @Override
    public int hashCode() {
      int result = layer;
      result = 31 * result + (texture != null ? texture.hashCode() : 0);
      result = 31 * result + (shader != null ? shader.hashCode() : 0);
      result = 31 * result + sprites.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Batch && compareTo((Batch) o) == 0;
    }
  }
}
