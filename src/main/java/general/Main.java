package general;

import org.lwjgl.Version;
import windowStuff.Window;

public final class Main {

  private Main() {
  }

  public static void main(String[] args) {
    Log.write(Version.getVersion());
    var win = Window.get();
    win.run();
  }
}