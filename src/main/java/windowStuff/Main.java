package windowStuff;

import org.lwjgl.Version;

public final class Main {

  private Main() {
  }

  public static void main(String[] args) {
    System.out.println(4>>1+1);
    System.out.println(Version.getVersion());
    var win = Window.get();
    win.run();
  }
}