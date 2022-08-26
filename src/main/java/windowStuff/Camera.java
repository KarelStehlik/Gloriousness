package windowStuff;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

  private final Matrix4f projection;
  private Vector3f position;

  public Camera(Vector3f position) {
    projection = new Matrix4f();
    this.position = position;
    adjustProjection();
  }

  public void adjustProjection() {
    projection.setOrtho(0f, 32f * 40f, 0f, 32f * 21f, 0f, 100f);
   // projection.setPerspective(3f, 1.4f, 1, 100);
  }

  public Matrix4f getViewMatrix(){
    Vector3f forward = new Vector3f(0f, 0f, -100f);
    Vector3f up = new Vector3f(0f, 1f, 0f);
    Matrix4f view = new Matrix4f();
    view.setLookAt(position, forward.add(position), up); // position.add(forward) is different from video
    return view;
  }

  public Matrix4f getProjectionMatrix(){
    return projection;
  }

  public void move(float x,float y,float z){
    position.x+=x;
    position.y+=y;
    position.z+=z;
  }

  public void moveTo(float x,float y,float z){
    position.x=x;
    position.y=y;
    position.z=z;
  }
}
