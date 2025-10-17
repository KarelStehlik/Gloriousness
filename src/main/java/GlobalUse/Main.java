
package GlobalUse;

import org.lwjgl.Version;
import windowStuff.Audio;
import windowStuff.Window;

public final class Main {

  private Main() {
  }

  public static void main(String[] args) {

    //* Tests

      double toDeg = 180 / Math.PI;
      for(float i=-180;i<=180;i+=0.1f){
        double rad = i/toDeg;
        float i2 = Util.get_rotation((float)Math.cos(rad), (float)Math.sin(rad));
        if(Math.abs(i-i2)>0.0001f){
          Log.write("get rotation imprecision: "+i+" got: "+i2);
        }
      }

    //*/

    //System.loadLibrary("renderdoc");
    Log.write(Version.getVersion());
    Audio.init();
    var win = Window.get();
    win.run();
  }
}