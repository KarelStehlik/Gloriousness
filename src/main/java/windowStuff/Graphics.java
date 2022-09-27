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

public final class Graphics {

  private final Collection<BatchSystem> batchSystems = new LinkedList<BatchSystem>();

  //private BatchSystem test;
  //private Sprite sTest;

  public void init() {
    Data.init();
    System.out.println(Constants.screenSize); // this is needed to load Constants
    //test = new BatchSystem();
    //sTest = new Sprite("Farm21", 50, 50, 100, 100, 0, "colorCycle");
    //test.addSprite(sTest);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  public void addBatchSystem(BatchSystem bs) {
    batchSystems.add(bs);
    bs.useCamera(new Camera(new Vector3f(0, 0, 20)));
  }

  public void redraw(double dt) {
    //test.useCamera(camera);
    Data.updateShaders();
    //test.draw();

    //Sprite.updateAll();

    for (BatchSystem bs : batchSystems) {
      bs.draw();
    }
  }
}
