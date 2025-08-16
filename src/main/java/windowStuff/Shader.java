package windowStuff;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import general.Log;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.BufferUtils;

public class Shader {

  private static final Pattern COMPILE = Pattern.compile(
      "#type ");
  final int shaderID;
  private final String name;

  public Shader(String path) {

    // compile shaders
    {
      String[] split = path.split("/");
      name = split[split.length - 1].split("\\.")[0];

      String vertexSource = null;
      String fragmentSource = null;
      String geometrySource=null;
      try {
        String[] sources = COMPILE.split(Files.readString(Paths.get(path)));
        for (String source : sources) {
          if (source.startsWith("fragment")) {
            fragmentSource = source.substring(source.indexOf('#'));
          } else if (source.startsWith("vertex")) {
            vertexSource = source.substring(source.indexOf('#'));
          }else if (source.startsWith("geometry")) {
            geometrySource = source.substring(source.indexOf('#'));
          }
        }
      } catch (IOException e) {
        Log.write("could not read file " + path);
        e.printStackTrace();
      }

      assert vertexSource != null : "No vertex source found in " + path;
      assert fragmentSource != null : "No fragment source found in " + path;

      int vertexID = glCreateShader(GL_VERTEX_SHADER);
      glShaderSource(vertexID, vertexSource);
      glCompileShader(vertexID);

      if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
        int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
        Log.write("V shader compile failed. " + name);
        Log.write(glGetShaderInfoLog(vertexID, len));
        assert false : "";
      }
      int geoID=0;
      if(geometrySource!=null){
        geoID=glCreateShader(GL_GEOMETRY_SHADER);
        glShaderSource(geoID, geometrySource);
        glCompileShader(geoID);
      }

      int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
      glShaderSource(fragmentID, fragmentSource);
      glCompileShader(fragmentID);

      if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
        int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
        Log.write("F shader compile failed. " + name);
        Log.write(glGetShaderInfoLog(fragmentID, len));
        assert false : "";
      }
      if (geometrySource!=null && (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE)) {
        int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
        Log.write("F shader compile failed. " + name);
        Log.write(glGetShaderInfoLog(fragmentID, len));
        assert false : "";
      }

      shaderID = glCreateProgram();
      glAttachShader(shaderID, vertexID);
      glAttachShader(shaderID, fragmentID);
      if (geometrySource!=null){
        glAttachShader(shaderID, geoID);
      }
      glLinkProgram(shaderID);

      if (glGetProgrami(shaderID, GL_LINK_STATUS) == GL_FALSE) {
        int len = glGetProgrami(shaderID, GL_INFO_LOG_LENGTH);
        Log.write("Shader link failed. " + name);
        Log.write(glGetProgramInfoLog(shaderID, len));
        assert false : "";
      }

      glDeleteShader(vertexID);
      glDeleteShader(fragmentID);
      if (geometrySource!=null){
        glDeleteShader(geoID);
      }
    }
  }

  public void use() {
    glUseProgram(shaderID);
  }

  public void detach() {
    glUseProgram(0);
  }

  private void uploadUniform(String uniformName, Matrix4fc inMatrix) {
    int varLocation = glGetUniformLocation(shaderID, uniformName);
    use();
    FloatBuffer inMatrixBuffer = BufferUtils.createFloatBuffer(16);
    inMatrix.get(inMatrixBuffer);
    glUniformMatrix4fv(varLocation, false, inMatrixBuffer);
  }

  public void uploadUniform(String uniformName, int value) {
    use();
    int varLocation = glGetUniformLocation(shaderID, uniformName);
    glUniform1i(varLocation, value);
  }

  public void uploadUniform(String uniformName, float value) {
    int varLocation = glGetUniformLocation(shaderID, uniformName);
    use();
    glUniform1f(varLocation, value);
  }
  public void uploadMatrix(String uniformName, Matrix4f value) {
    uploadUniform(uniformName, value);
  }
  public void uploadTexture(String uniformName, int slot) {
    use();
    int varLocation = glGetUniformLocation(shaderID, uniformName);
    glUniform1f(varLocation, slot);
  }

  public void useCamera(Camera cam) {
    uploadUniform("projection", cam.getProjectionMatrix());
    uploadUniform("view", cam.getViewMatrix());
  }

  @Override
  public String toString() {
    return "Shader{"
        + "name='" + name + '\''
        + '}';
  }
}
