package windowStuff;

import java.util.List;

public class ImageData {

  public final String textureName;
  public final float[] textureCoordinates;

  ImageData(String name, List<Float> coords) {
    textureName = name;
    textureCoordinates = new float[8];
    for (int i = 0; i < 8; i++) {
      textureCoordinates[i] = coords.get(i);
    }
  }
}