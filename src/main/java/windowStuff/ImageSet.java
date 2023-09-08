package windowStuff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ImageSet {

  private final String imageDirectory;
  private final String imageDataDirectory;
  private final Map<String, Texture> textures = new HashMap<>(1);
  private final List<ImageData> images = new ArrayList<>(1);
  private final Map<String, Integer> imageIndices = new HashMap<>(1);
  private final Map<Integer, Integer> animationLengths = new TreeMap<>();

  public ImageSet(String textures, String imageData) {
    imageDirectory = textures;
    imageDataDirectory = imageData;
    init();
  }

  public int getAnimationLength(int first) {
    var result = animationLengths.get(first);
    assert result != null : "no animation at index " + first + ", tex: " + images.get(first);
    return result;
  }

  public void init() {
    // loads textures
    String[] textureNames = new File(imageDataDirectory).list();
    assert textureNames != null : imageDataDirectory + " is not a valid directory.";
    for (String textureName : textureNames) {
      String shortenedTexName = textureName.substring(0, textureName.length() - 4);
      loadTexture(shortenedTexName);
      try {
        String[] data = Files.readString(Paths.get(imageDataDirectory + '/' + textureName))
            .split("\n");  // array of "Farm21 1.0 0.0 0.0 1.0 1.0 1.0 0.0 0.0"
        for (String dat : data) {
          loadImage(shortenedTexName, dat.split("\\|"));
        }
      } catch (IOException e) {
        System.out.println("could not read file " + imageDataDirectory + '/' + textureName);
        e.printStackTrace();
        return;
      }
    }
  }

  public void loadTexture(String name) {
    if (textures.containsKey(name)) {
      System.out.println("warning: attempting to load duplicate texture " + name);
      return;
    }
    textures.put(name,
        new Texture(imageDirectory + '/' + name + (name.endsWith(".png") ? "" : ".png")));
  }

  /**
   * @param data expects [Farm21 1.0 0.0 0.0 1.0 1.0 1.0 0.0 0.0]
   */
  public void loadImage(String tex, String[] data) {
    assert data.length == 9 : "invalid image location data : " + Arrays.toString(data);
    imageIndices.put(data[0], images.size());
    images.add(new ImageData(tex, List.of(data).subList(1, 9).stream().map(
        Float::parseFloat).collect(Collectors.toList())));

    if (Pattern.matches(".*-\\d+", data[0])) {
      var animName = data[0].substring(0, data[0].lastIndexOf('-')) + "-0";
      var number = Integer.parseInt(data[0].substring(data[0].lastIndexOf('-') + 1));
      int startIndex = getImageId(animName);
      animationLengths.put(startIndex,
          Math.max(animationLengths.getOrDefault(startIndex, 0), number));
    }
  }

  public int getImageId(String name) {
    Integer result = imageIndices.get(name);
    if (result != null) {
      return result;
    }
    System.out.println("No such image: " + name);
    return 0;
  }

  /**
   * the coordinates of the image in its texture
   */
  public float[] getImageCoordinates(int id) {
    return images.get(id).textureCoordinates;
  }

  /**
   * the batch texture where the image is located
   */
  public String getImageTexture(int id) {
    return images.get(id).textureName;
  }

  public Texture getTexture(String name) {

    var result = textures.get(name);
    assert result != null : "Texture " + name + " was not loaded at load time";
    return result;
  }


  /**
   * stores where in a texture the image is located. is stored in a hashmap, where the key is the
   * name of the image.
   */
  private static class ImageData {

    String textureName;
    float[] textureCoordinates;

    ImageData(String name, List<Float> coords) {
      textureName = name;
      textureCoordinates = new float[8];
      for (int i = 0; i < 8; i++) {
        textureCoordinates[i] = coords.get(i);
      }
    }
  }
}
