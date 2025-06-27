package windowStuff;

import general.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

  private final Map<String, List<ImageData>> animations = new TreeMap<>();
  private final Map<String, ImageData> images = new TreeMap<>();

  public ImageSet(String textures, String imageData) {
    imageDirectory = textures;
    imageDataDirectory = imageData;
    init();
  }

  public List<ImageData> getAnimation(String name) {
    var result = animations.get(name);
    if(result==null){
      return List.of(getImage(name));
    }
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
        Log.write("could not read file " + imageDataDirectory + '/' + textureName);
        e.printStackTrace();
        return;
      }
    }
    animations.replaceAll((a, v) -> Collections.unmodifiableList(animations.get(a)));
  }

  public void loadTexture(String name) {
    if (textures.containsKey(name)) {
      Log.write("warning: attempting to load duplicate texture " + name);
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
    ImageData img = new ImageData(tex, List.of(data).subList(1, 9).stream().map(
        Float::parseFloat).collect(Collectors.toList()));
    images.put(data[0], img);

    if (Pattern.matches(".*-\\d+", data[0])) {
      var animName = data[0].substring(0, data[0].lastIndexOf('-'));
      animations.computeIfAbsent(animName, k-> new ArrayList<>(1)).add(img);
    }
  }

  public ImageData getImage(String name) {
    ImageData result = images.get(name);
    if (result != null) {
      return result;
    }
    Log.write("No such image: " + name);
    return images.get("_notfound");
  }

  public Texture getTexture(String name) {

    var result = textures.get(name);
    assert result != null : "Texture " + name + " was not loaded at load time";
    return result;
  }
}
