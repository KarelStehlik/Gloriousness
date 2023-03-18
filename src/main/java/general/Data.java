package general;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import windowStuff.ImageSet;
import windowStuff.Shader;

public final class Data {

  public static final Random gameMechanicsRng = new Random();
  public static final Random unstableRng = new Random();
  private static final String shaderDirectory = "assets/shady shit";
  private static final String imageDirectory = "assets/final images";
  private static final String imageDataDirectory = "assets/image coordinates";
  private static final String statsDirectory = "stats";
  private static final String mapDataFile = "assets/maps/output.txt";
  private static final Map<String, Shader> shaders = new HashMap<>(1);
  private static final long startTime = System.nanoTime();
  private static final Map<String, Map<String, Map<String, Float>>> entityStats = new HashMap<>(5);
  private static final Map<String, ArrayList<Point>> mapData = new HashMap<>(1);

  private static final ImageSet defaultImageSet = new ImageSet(imageDirectory, imageDataDirectory);

  public static ImageSet getImageSet(){
    return defaultImageSet;
  }

  private Data() {
  }

  public static void init() {
    // loads shaders
    var shaderNames = new File(shaderDirectory).list();
    assert shaderNames != null : shaderDirectory + " is not a valid directory.";
    for (String shaderName : shaderNames) {
      loadShader(shaderName);
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

  public static List<Point> getMapData(String name) {
    return mapData.get(name);
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
}
