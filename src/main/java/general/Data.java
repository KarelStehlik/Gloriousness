package general;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.joml.Vector2i;
import windowStuff.Shader;
import windowStuff.Texture;

public final class Data {

  public static final Random gameMechanicsRng = new Random();
  public static final Random unstableRng = new Random();
  private static final String shaderDirectory = "assets/shady shit";
  private static final String imageDirectory = "assets/final images";
  private static final String imageDataDirectory = "assets/image coordinates";
  private static final String statsDirectory = "stats";
  private static final String mapDataFile = "assets/maps/output.txt";
  private static final Map<String, Shader> shaders = new HashMap<>(1);
  private static final Map<String, Texture> textures = new HashMap<>(1);
  private static final List<ImageData> images = new ArrayList<>(1);
  private static final Map<String, Integer> imageIndices = new HashMap<>(1);
  private static final long startTime = System.nanoTime();
  private static final Map<String, Map<String, Map<String, Float>>> entityStats = new HashMap<>(5);
  private static final Map<Integer, Integer> animationLengths = new TreeMap<>();
  private static final Map<String, ArrayList<Point>> mapData = new HashMap<>(1);

  private Data() {
  }

  public static int getAnimationLength(int first) {
    var result = animationLengths.get(first);
    assert result != null : "no animation at index " +first+", tex: " + images.get(first);
    return result;
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
    assert types != null;
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
    loadMapData();
  }

  private static void loadMapData() {
    try {
      String[] data = Files.readString(Paths.get(mapDataFile)).split("\n");

      for (String map : data) {
        String[] split = map.split(" ");
        String name = split[0];
        mapData.put(name, new ArrayList<>(split.length - 1));
        for (int i = 1; i < split.length; i++) {
          String[] point = split[i].split(",");
          mapData.get(name).add(new Point(Integer.parseInt(point[0]), Integer.parseInt(point[1])));
        }
      }

    } catch (IOException e) {
      System.out.println("failed to read " + mapDataFile);
      e.printStackTrace();
    }
  }

  public static String[] listMaps() {
    return mapData.keySet().toArray(new String[0]);
  }

  public static ArrayList<Point> getMapData(String name) {
    return mapData.get(name);
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
    imageIndices.put(data[0], images.size());
    images.add(new ImageData(tex, List.of(data).subList(1, 9).stream().map(
        Float::parseFloat).collect(Collectors.toList())));

    if (Pattern.matches(".*-\\d+", data[0])) {
      var animName = data[0].substring(0, data[0].lastIndexOf('-')) + "-0";
      var number = Integer.parseInt(data[0].substring(data[0].lastIndexOf('-') + 1));
      int startIndex = getImageId(animName);
      animationLengths.put(startIndex, Math.max(animationLengths.getOrDefault(startIndex, 0), number));
      System.out.println(data[0] + " " +number);
    }
  }

  public static void sortAnimationImages(){
    for(var animation : animationLengths.entrySet()){
      int start = animation.getKey();
      int length = animation.getValue();
    }
  }

  public static int getImageId(String name){
    Integer result = imageIndices.get(name);
    if(result != null){
      return result;
    }
    System.out.println("No such image: " + name);
    return 0;
  }

  /**
   * the coordinates of the image in its texture
   */
  public static float[] getImageCoordinates(int id) {
    return images.get(id).textureCoordinates;
  }

  /**
   * the batch texture where the image is located
   */
  public static String getImageTexture(int id) {
    return images.get(id).textureName;
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
    if (type == null) {
      throw new IllegalStateException("No entity type - " + _type);
    }
    var result = type.get(_name);
    if (result == null) {
      throw new IllegalStateException("No " + _type + " " + _name);
    }
    return result;
  }

  public static void updateShaders() {
    getShader("colorCycle").uploadUniform("time", (int) ((System.nanoTime() - startTime) >> 10));
    getShader("colorCycle2").uploadUniform("time", (int) ((System.nanoTime() - startTime) >> 10));
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
