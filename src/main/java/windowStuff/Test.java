package windowStuff;


public class Test {
  @FunctionalInterface
  public interface ButtonFunc{
    void press();
  }

  public static class Button {

    private final ButtonFunc a;

    Button(ButtonFunc test) {
      a = test;
    }

    void press() {
      a.press();
    }
  }
}
