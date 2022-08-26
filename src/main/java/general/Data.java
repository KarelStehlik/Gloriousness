package general;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import windowStuff.Shader;
import windowStuff.Texture;

public final class Data {

  private static Map<String, Shader> shaders;
  private static Map<String, Texture> textures;

  public static void init(String shaderDirectory, String imageDirectory) {
    shaders = new HashMap<>(1);
    var shaderNames = new File(shaderDirectory).list();
    assert shaderNames != null : shaderDirectory + " is not a valid directory.";
    for (String shaderName : shaderNames) {
      loadShader(shaderName);
    }

    textures = new HashMap<>(1);
    var textureNames = new File(imageDirectory).list();
    assert textureNames != null : shaderDirectory + " is not a valid directory.";
    for (String textureName : textureNames) {
      loadTexture(textureName);
    }
  }

  public static void loadShader(String name) {
    if (shaders.containsKey(name)) {
      return;
    }
    System.out.println(name);
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

  public static void loadTexture(String name) {
    if (textures.containsKey(name)) {
      return;
    }
    textures.put(name,
        new Texture("assets/final_images/" + name + (name.endsWith(".png") ? "" : ".png")));
  }

  /**
   * for images other than png
   */
  public static void loadTexture(String name, String affix) {
    if (textures.containsKey(name)) {
      return;
    }
    textures.put(name, new Texture("assets/final_images/" + name + affix));
  }

  public static Texture getTexture(String name) {
    var result = textures.get(name + (name.endsWith(".png") ? "" : ".png"));
    assert result != null : "Texture " + name + " was not loaded at load time";
    return result;
  }

  private Data() {
  }
}
