package windowStuff;

import java.util.Arrays;
import java.util.List;

public class ImageData {

  public final Texture texture;
  public final float[] textureCoordinates;

  ImageData(Texture tex, List<Float> coords) {
    if(tex==null){
      new Exception().printStackTrace();
    }
    texture = tex;
    textureCoordinates = new float[8];
    for (int i = 0; i < 8; i++) {
      textureCoordinates[i] = coords.get(i);
    }
  }

  @Override
  public String toString() {
    return "ImageData{"
        + "texture=" + texture
        + ", textureCoordinates=" + Arrays.toString(textureCoordinates)
        + '}';
  }
}