package general;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import windowStuff.Shader;
import windowStuff.Texture;

public final class Data {

  public static final Random gameMechanicsRng = new Random();
  public static final Random unstableRng = new Random();
  private static Map<String, Shader> shaders;
  private static Map<String, Texture> textures;
  private static Map<String, ImageData> images;
  private static final String shaderDirectory = "assets/shady shit";
  private static final String imageDirectory = "assets/final images";
  private static final String imageDataDirectory = "assets/image coordinates";

  private Data() {
  }

  /**
   * stores where in a texture the image is located. is stored in a hashmap, where the key is the name of the image.
   */
  private static class ImageData{
    String textureName;
    List<Float> textureCoordinates;
    ImageData(String name, List<Float> coords){
      textureName = name;
      textureCoordinates = coords;
    }
  }

  public static void init() {
    shaders = new HashMap<>(1);
    var shaderNames = new File(shaderDirectory).list();
    assert shaderNames != null : shaderDirectory + " is not a valid directory.";
    for (String shaderName : shaderNames) {
      loadShader(shaderName);
    }

    textures = new HashMap<>(1);
    images = new HashMap<>(1);
    String[] textureNames = new File(imageDataDirectory).list();
    assert textureNames != null : imageDataDirectory + " is not a valid directory.";
    for (String textureName : textureNames) {
      String shortenedTexName = textureName.substring(0, textureName.length()-4);
      loadTexture(shortenedTexName);
      try {
        String[] data = Files.readString(Paths.get(imageDataDirectory + '/' + textureName)).split("\n");  // array of "Farm21 1.0 0.0 0.0 1.0 1.0 1.0 0.0 0.0"
        for(String dat : data) {
          loadImage(shortenedTexName, dat.split(" "));
        }
      } catch (IOException e) {
        System.out.println("could not read file " + imageDataDirectory + '/' + textureName);
        e.printStackTrace();
      }
    }
  }

  public static void loadTexture(String name) {
      if (textures.containsKey(name)) {
        System.out.println("warning: attempting to load duplicate texture "+name);
        return;
      }
      textures.put(name,
          new Texture(imageDirectory+ '/' + name + (name.endsWith(".png") ? "" : ".png")));
    }

  /**
   * @param data expects [Farm21 1.0 0.0 0.0 1.0 1.0 1.0 0.0 0.0]
   */
  public static void loadImage(String tex, String[] data){
    assert data.length == 9 : "invalid image location data : "+ Arrays.toString(data);
    images.put(data[0], new ImageData(tex, List.of(data).subList(1, 9).stream().map(
        Float::parseFloat).collect(Collectors.toList())));
      List.of(data).subList(1, 8);
    }

  /**
   * the coordinates of the image in its texture
   */
  public static List<Float> getImageCoordinates(String name){
    return images.get(name).textureCoordinates;
  }

  /**
   * the batch texture where the image is located
   */
  public static String getImageTexture(String name){
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

  //public static void loadTexture(String name) {
  //  if (textures.containsKey(name)) {
  //    return;
  //  }
  //  textures.put(name,
  //      new Texture("assets/final_images/" + name + (name.endsWith(".png") ? "" : ".png")));
  //}
//
  ///**
  // * for images other than png
  // */
  //public static void loadTexture(String name, String affix) {
  //  if (textures.containsKey(name)) {
  //    return;
  //  }
  //  textures.put(name, new Texture("assets/final_images/" + name + affix));
  //}
//
  public static Texture getTexture(String name) {

    var result = textures.get(name);
    assert result != null : "Texture " + name + " was not loaded at load time";
    return result;
  }

  public static Collection<Shader> getAllShaders() {
    return shaders.values();
  }

  public static void updateShaders() {
    getShader("colorCycle").uploadUniform("time", (int) (System.nanoTime() / 10000));
  }
}
