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
import windowStuff.Graphics;
import windowStuff.ImageSet;
import windowStuff.Shader;

public final class Data {

  public static final Random gameMechanicsRng = new Random();
  public static final Random unstableRng = new Random();
  private static final String shaderDirectory = "assets/shady shit";
  private static final String imageDirectory = "assets/final images";
  private static final String imageDataDirectory = "assets/image coordinates";
  private static final String mapDataFile = "assets/maps/output.txt";
  private static final Map<String, Shader> shaders = new HashMap<>(1);
  private static final long startTime = System.nanoTime();
  private static final Map<String, ArrayList<Point>> mapData = new HashMap<>(1);

  private Data() {
  }

  public static void init() {
    Graphics.setLoadedImages(new ImageSet(imageDirectory, imageDataDirectory));

    // loads shaders
    var shaderNames = new File(shaderDirectory).list();
    assert shaderNames != null : shaderDirectory + " is not a valid directory.";
    loadShader("basic");
    for (String shaderName : shaderNames) {
      loadShader(shaderName);
    }
    loadMapData();
  }

  private static void loadMapData() {
    try {
      List<String> data = Files.readAllLines(Paths.get(mapDataFile));

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
    if(result==null){
      Log.write("Not a valid shader: "+name);
      return getShader("basic");
    }
    return result;
  }

  public static Collection<Shader> getAllShaders() {
    return shaders.values();
  }

  public static void updateShaders() {
    getShader("colorCycle").uploadUniform("time", (int) ((System.nanoTime() - startTime) >> 10));
    getShader("colorCycle2").uploadUniform("time", (int) ((System.nanoTime() - startTime) >> 10));
    getShader("rotator").uploadUniform("rotat",(float)((System.nanoTime() - startTime)/Math.pow(10,9)));
  }

  /**
   * stores where in a texture the image is located. is stored in a hashmap, where the key is the
   * name of the image.
   */
}
