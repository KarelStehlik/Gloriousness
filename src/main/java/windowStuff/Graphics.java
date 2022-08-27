package windowStuff;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;

import general.Data;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

public class Graphics {

  public Camera camera;

  private BatchSystem test;

  private List<Sprite> sprites;

  private float testNum = 0;

  public void init() {
    Data.init("assets/shady shit", "assets/final_images");

    camera = new Camera(new Vector3f(0, 0, 20));

    test = new BatchSystem();

    sprites = new ArrayList<>(1000000);

    Sprite s = new Sprite("Farm21", 50, 50, 100, 100, 0, "colorCycle");
    test.addSprite(s);
    sprites.add(s);
    //s.setColors(
    //    new float[]{
    //        1, 1, 0, 1,
    //        0, 1, 1, 1,
    //        1, 0, 1, 1,
    //        0, 0, 2, 1,}
    //);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  public void redraw(double dt) {
    //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    //camera.move(0, 0, -.05f);
    test.useCamera(camera);
    Data.updateShaders();
    test.draw();
    // sprites.get(0).setRotation(testNum);
    testNum += dt * 200;
    //sprites.get(0).scale(1.001f);
    sprites.get(0).setPosition(testNum * .2f, testNum * .2f);
  }
}
