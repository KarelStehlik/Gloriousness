package windowStuff;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;

import general.Constants;
import general.Data;
import java.util.Collection;
import java.util.LinkedList;
import org.joml.Vector3f;

public class Graphics {

  private final Collection<BatchSystem> batchSystems = new LinkedList<BatchSystem>();
  private Camera camera;
  private boolean cameraChanged = true;

  //private BatchSystem test;
  //private Sprite sTest;

  public void init() {
    Data.init("assets/shady shit", "assets/final_images");
    System.out.println(Constants.screenSize);
    //test = new BatchSystem();
    //sTest = new Sprite("Farm21", 50, 50, 100, 100, 0, "colorCycle");
    //test.addSprite(sTest);

    camera = new Camera(new Vector3f(0, 0, 20));

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  public void moveCamera(float x, float y) {
    camera.move(x, y, 20);
    cameraChanged = true;
  }

  public void addBatchSystem(BatchSystem bs) {
    batchSystems.add(bs);
    bs.useCamera(camera);
  }

  public void redraw(double dt) {
    //test.useCamera(camera);
    Data.updateShaders();
    //test.draw();
    for (BatchSystem bs : batchSystems) {
      if (cameraChanged) {
        bs.useCamera(camera);
      }
      bs.draw();
    }
    cameraChanged = false;
  }
}
