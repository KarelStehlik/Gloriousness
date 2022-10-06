package general;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import windowStuff.Shader;
import windowStuff.Texture;

public final class Data {

  public static final Random gameMechanicsRng = new Random();
  public static final Random unstableRng = new Random();
  private static final String shaderDirectory = "assets/shady shit";
  private static final String imageDirectory = "assets/final images";
  private static final String imageDataDirectory = "assets/image coordinates";
  private static final String statsDirectory = "stats";
  private static final Map<String, Shader> shaders = new HashMap<>(1);
  private static final Map<String, Texture> textures= new HashMap<>(1);
  private static final Map<String, ImageData> images= new HashMap<>(1);
  private static final long startTime = System.nanoTime();
  private static final Map<String, Map<String, Map<String, Float>>> entityStats = new HashMap<>(5);

  private Data() {
  }

  public static void init() {
    // loads shaders
    var shaderNames = new File(shaderDirectory).list();
    assert shaderNames != null : shaderDirectory + " is not a valid directory.";
    for (String shaderName : shaderNames) {
      loadShader(shaderName);
    }

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

    // loads stats
    String[] types = new File(statsDirectory).list();
    for (String type : types) {
      String shortenedType = type.substring(0, type.length() - 4);
      entityStats.put(shortenedType, new HashMap<>(10));
      String[] sources;
      try {
        sources = Files.readString(Paths.get(statsDirectory + "/" + type)).split("\n");
      } catch (IOException e) {
        System.out.println("could not read file " + statsDirectory + "/" + type);
        e.printStackTrace();
        return;
      }
      for (String source : sources) {
        String[] splitSource = source.split("\\|");
        entityStats.get(shortenedType).put(splitSource[0], new HashMap<>(10));
        for (int i = 1, splitSourceLength = splitSource.length; i < splitSourceLength; i++) {
          String stat = splitSource[i];
          String[] splitStat = stat.split("=");
          entityStats.get(shortenedType).get(splitSource[0])
              .put(splitStat[0], Float.parseFloat(splitStat[1]));
        }
      }
    }
  }

  public static void loadTexture(String name) {
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
  public static void loadImage(String tex, String[] data) {
    assert data.length == 9 : "invalid image location data : " + Arrays.toString(data);
    images.put(data[0], new ImageData(tex, List.of(data).subList(1, 9).stream().map(
        Float::parseFloat).collect(Collectors.toList())));
    List.of(data).subList(1, 8);
  }

  /**
   * the coordinates of the image in its texture
   */
  public static float[] getImageCoordinates(String name) {
    //System.out.println(name);
    return images.get(name).textureCoordinates;
  }

  /**
   * the batch texture where the image is located
   */
  public static String getImageTexture(String name) {
    return images.get(name).textureName;
  }

  public static void loadShader(String name) {
    if (shaders.containsKey(name)) {
      return;
    }
    shaders.put(
        name,
        new Shader("assets/shady shit/" + name + (name.endsWith(".glsl") ? "" : ".glsl"))
    );
  }

  public static Shader getShader(String name) {
    var result = shaders.get(name + (name.endsWith(".glsl") ? "" : ".glsl"));
    assert result != null : "Shader " + name + " was not loaded at load time";
    return result;
  }

  public static Texture getTexture(String name) {

    var result = textures.get(name);
    assert result != null : "Texture " + name + " was not loaded at load time";
    return result;
  }

  public static Collection<Shader> getAllShaders() {
    return shaders.values();
  }

  public static Map<String, Float> getEntityStats(String _type, String _name) {
    var type = entityStats.get(_type);
    if(type==null){
      throw new IllegalStateException("No entity type - "+_type);
    }
    var result = type.get(_name);
    if(result==null){
      throw new IllegalStateException("No "+_type+" "+_name);
    }
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
      for(int i=0; i<8; i++){
        textureCoordinates[i]=coords.get(i);
      }
    }

    static float Float2float(Float f){
      return f;
    }
  }

  public static void updateShaders() {
    getShader("colorCycle").uploadUniform("time", (int) ((System.nanoTime()-startTime) >> 10));
    getShader("colorCycle2").uploadUniform("time", (int) ((System.nanoTime()-startTime) >> 10));
  }
}
