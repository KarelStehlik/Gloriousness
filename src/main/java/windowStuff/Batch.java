package windowStuff;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
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

  protected static final int MAX_BATCH_SIZE = 1000;
  private final Texture texture;
  protected final String textureName;
  private final int maxSize;
  private final int vbo;
  private final int vao;
  private final Shader shader;
  private final Sprite[] sprites;
  protected final List<Integer> freeSpriteSlots;
  protected final int layer;


  public Batch(String textureName, int size, String shader, int layer) {
    texture = Data.getTexture(textureName);
    this.textureName = textureName;
    maxSize = size;
    this.shader = Data.getShader(shader);
    sprites = new Sprite[size];
    freeSpriteSlots = new ArrayList<>(size);
    this.layer = layer;

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

    int ebo = glGenBuffers();
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
    if (freeSpriteSlots.isEmpty() || sprite.batch != null) {
      return;
    }
    int slot = freeSpriteSlots.remove(0);
    sprites[slot] = sprite;
    sprite.batch = this;
  }

  public void draw() {
    glBindVertexArray(vao);
    shader.use();
    shader.uploadTexture("sampler", 0);
    glActiveTexture(GL_TEXTURE0);
    texture.bind();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    long offset = 0;

    for (Sprite sprite : sprites) {
      if (sprite != null && sprite.hasChanged) {
        glBufferSubData(GL_ARRAY_BUFFER, offset, sprite.vertices);
        sprite.hasChanged = false;
      }
      offset += Constants.SpriteSizeFloats * Float.BYTES;
    }

    glDrawElements(GL_TRIANGLES, 6 * maxSize, GL_UNSIGNED_INT, 0);
  }

  public void useCamera(Camera camera) {
    shader.useCamera(camera);
  }
}
