package windowStuff;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import general.Constants;
import general.Data;
import general.Util;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Batch {

  protected static final int MAX_BATCH_SIZE = 10000;
  protected final String textureName;
  protected final List<Integer> freeSpriteSlots;
  protected final int layer;
  protected final BatchSystem group;
  protected final Sprite[] sprites;
  private final Texture texture;
  private final int maxSize;
  private final int vao, vbo, ebo;
  private final Shader shader;
  protected boolean isEmpty;


  public Batch(String textureName, int size, String shader, int layer, BatchSystem system) {
    texture = Data.getTexture(textureName);
    this.textureName = textureName;
    maxSize = size;
    this.shader = Data.getShader(shader);
    sprites = new Sprite[size];
    freeSpriteSlots = new ArrayList<>(size);
    this.layer = layer;
    isEmpty = true;
    group = system;

    for (int i = 0; i < size; i++) {
      freeSpriteSlots.add(i);
    }

    int[] elements = new int[6 * size];
    for (int i = 0; i < size; i++) {
      elements[6 * i] = 2 + 4 * i;
      elements[6 * i + 1] = 1 + 4 * i;
      elements[6 * i + 2] = 4 * i;
      elements[6 * i + 3] = 4 * i;
      elements[6 * i + 4] = 1 + 4 * i;
      elements[6 * i + 5] = 3 + 4 * i;
    }

    vao = glGenVertexArrays();  // seems unnecessary for now
    glBindVertexArray(vao);

    ebo = glGenBuffers();
    IntBuffer elementBuffer = Util.buffer(elements).flip();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

    float[] vertices = new float[size * Constants.SpriteSizeFloats];
    FloatBuffer vertexBuffer = Util.buffer(vertices).flip();
    vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_DYNAMIC_DRAW);

    int positionCount = 3;
    int floatBytes = Float.BYTES;
    int vertexBytes = Constants.VertexSizeFloats * floatBytes;

    glVertexAttribPointer(0, positionCount, GL_FLOAT, false, vertexBytes, 0);
    glEnableVertexAttribArray(0);

    int colorCount = 4;
    glVertexAttribPointer(1, colorCount, GL_FLOAT, false, vertexBytes, positionCount * floatBytes);
    glEnableVertexAttribArray(1);

    int texCoords = 2;
    glVertexAttribPointer(2, texCoords, GL_FLOAT, false, vertexBytes,
        (positionCount + colorCount) * floatBytes);
    glEnableVertexAttribArray(2);

    glBindVertexArray(0);
  }

  public void addSprite(Sprite sprite) {
    assert !freeSpriteSlots.isEmpty()
        && sprite.batch == null : "Attempt to add sprite to a full batch.";
    int slot = freeSpriteSlots.remove(0);
    synchronized (sprites) {
      sprites[slot] = sprite;
    }
    sprite.getBatched(this, slot);
    isEmpty = false;
  }

  public void removeSprite(Sprite sprite) {
    synchronized (sprites) {
      sprites[sprite.slotInBatch] = null;
    }
    freeSpriteSlots.add(sprite.slotInBatch);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferSubData(GL_ARRAY_BUFFER,
        (long) Constants.SpriteSizeFloats * Float.BYTES * sprite.slotInBatch,
        new float[Constants.SpriteSizeFloats]);
    if (freeSpriteSlots.size() == maxSize) {
      isEmpty = true;
    }
  }

  protected void delete() {
    glDeleteBuffers(vbo);
    glDeleteBuffers(ebo);
    glDeleteVertexArrays(vao);
  }

  public void draw() {
    glBindVertexArray(vao);
    // shader.use();
    // shader.uploadTexture("sampler", 0);
    glActiveTexture(GL_TEXTURE0);
    texture.bind();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    long offset = 0;

    for (int i = 0; i < maxSize; i++) {
      Sprite sprite = sprites[i];
      if (sprite != null) {
        if (sprite.deleteThis) {
          sprite._delete();
        } else if (sprite.hasUnsavedChanges) {
          sprite.updateVertices();
          sprite.bufferVertices(offset);
        }
      }
      offset += Constants.SpriteSizeFloats * Float.BYTES;
    }
    if (isEmpty) {
      return;
    }
    glDrawElements(GL_TRIANGLES, 6 * maxSize, GL_UNSIGNED_INT, 0);
  }

  public void useCamera(Camera camera) {
    shader.useCamera(camera);
  }
}
