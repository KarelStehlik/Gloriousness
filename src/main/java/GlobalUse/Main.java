
package GlobalUse;

import org.lwjgl.Version;
import windowStuff.Audio;
import windowStuff.Window;

public final class Main {

  private Main() {
  }

  public static void main(String[] args) {
    //System.loadLibrary("renderdoc");
    Log.write(Version.getVersion());
    Audio.init();
    var win = Window.get();
    win.run();
  }
}