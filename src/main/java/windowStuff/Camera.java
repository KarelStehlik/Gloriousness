package windowStuff;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

  static final Vector3f forward = new Vector3f(0f, 0f, -100f);
  static final Vector3f up = new Vector3f(0f, 1f, 0f);

  private final Matrix4f projection;
  private final Vector3f position;
  private final Matrix4f view;

  public Camera(Vector3f position) {
    projection = new Matrix4f();
    view = new Matrix4f();
    this.position = position;
    adjustProjection();
    view.setLookAt(position, new Vector3f(0f, 0f, -100f).add(position),
        up); // position.add(forward) is different from video
  }

  public void adjustProjection() {
    projection.setOrtho(0f, 1920, 0f, 1080, 0f, 100f);
    // projection.setPerspective(3f, 1.4f, 1, 100);
  }

  public Matrix4f getViewMatrix() {
//    for(int i=0;i<4;i++){
//      String s="";
//      for(int r=0;r<4;r++){
//        s=s+" "+view.getRowColumn(r,i);
//      }
//      Log.write(s);
//    }
//    Log.write("end");
    return view;
  }

  public Matrix4f getProjectionMatrix() {
    return projection;
  }

  public void move(float x, float y, float z) {
    position.x += x;
    position.y += y;
    position.z += z;
    view.setLookAt(position, new Vector3f(0f, 0f, -100f).add(position),
        up); // position.add(forward) is different from video
  }

  public void moveTo(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
    view.setLookAt(position, new Vector3f(0f, 0f, -100f).add(position),
        up); // position.add(forward) is different from video
  }
}
