package GlobalUse;

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

import GlobalUse.MapData.MapData;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageSet;
import windowStuff.GraphicsOnly.Shader;

public final class Data {

  public static final Random gameMechanicsRng = new Random();
  public static final Random unstableRng = new Random();
  private static final String shaderDirectory = "assets/shady shit";
  private static final String imageDirectory = "assets/final images";
  private static final String imageDataDirectory = "assets/image coordinates";
  private static final String mapDataFile = "assets/maps/output.txt";
  private static final Map<String, Shader> shaders = new HashMap<>(1);
  private static final long startTime = System.nanoTime();
  private static String[] maps;

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
    getMaps();
  }

  private static void getMaps() {
    try {
      List<String> data = Files.readAllLines(Paths.get(mapDataFile));

      maps=new String[data.size()];
      for (int i=0;i<data.size();i++) {
        String[] split = data.get(i).split("\\|");
        String name = split[0];
        maps[i]=name;
      }

    } catch (IOException e) {
      System.out.println("failed to read " + mapDataFile);
      e.printStackTrace();
    }
  }

  public static MapData getMapData(String mapName) {
    try {
      List<String> data = Files.readAllLines(Paths.get(mapDataFile));
      MapData mapData=new MapData();
      for (String map : data) {
        String[] split = map.split("\\|");
        String name = split[0];
        if(!name.equals(mapName)){
          continue;
        }
        for(int i=1;i<split.length;i++) {
          mapData.add(split[i].split(" "));
        }
      }
      return mapData;

    } catch (IOException e) {
      System.out.println("failed to read " + mapDataFile);
      e.printStackTrace();
      return null;
    }
  }

  public static String[] listMaps() {
    return maps;
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
