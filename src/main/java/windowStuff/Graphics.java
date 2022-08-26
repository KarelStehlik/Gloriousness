package windowStuff;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;

import general.Data;
import org.joml.Vector3f;

public class Graphics {

  public Camera camera;

  private BatchSystem test;

  private Batch testBatch;

  public void init() {
    Data.init("assets/shady shit", "assets/final_images");

    camera = new Camera(new Vector3f(0, 0, 20));

    test = new BatchSystem();

    for (int i = 0; i < 1; i++) {
      for (int j = 0; j < 1; j++) {
        test.addSprite(new Sprite("Farm21", 50, 50, 50, 0));
      }
    }

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  public void redraw(double dt) {
    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    //camera.move(0, 0, -.05f);
    test.useCamera(camera);
    test.draw();
  }
}
