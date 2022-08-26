package windowStuff;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;


import general.Data;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11C;

public class Graphics {

  public Camera camera;

  private Batch testBatch;

  private Batch testBatch2;

  public void init() {
    Data.init("assets/shady shit", "assets/final_images");

    camera = new Camera(new Vector3f(0, 0, 20));

    testBatch = new Batch("shockwave", 2, "basic");

    testBatch2 = new Batch("Farm21", 1, "basic");

    testBatch.addSprite(new Sprite(50, 50, 50));

    var test = new Sprite(150, 50, 100);

    //test.vertices[11] = -10;

    //glEnable(GL_DEPTH_TEST);

    testBatch.addSprite(test);

    testBatch2.addSprite(new Sprite(150, 150, 50));

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  public void redraw() {
    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    //camera.move(0, 0, -.05f);

    testBatch2.useCamera(camera);
    testBatch2.draw();

    testBatch.useCamera(camera);
    testBatch.draw();
  }
}
