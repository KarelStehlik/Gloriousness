package general;

public class RefFloat {

  private float value;

  public RefFloat(float f) {
    value = f;
  }

  public RefFloat(double f) {
    value = (float) f;
  }

  public float get() {
    return value;
  }

  public void set(float f) {
    value = f;
  }

  public void add(float f) {
    value += f;
  }

  public void multiply(float f) {
    value *= f;
  }

  @Override
  public String toString() {
    return "RefFloat{"
        + "value=" + value
        + '}';
  }
}
