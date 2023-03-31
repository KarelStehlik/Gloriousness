package windowStuff;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    final String name;
    final int shaderID;

    public Shader(String path) {

        String[] split = path.split("/");
        name = split[split.length - 1].split("\\.")[0];

        String vertexSource = null;
        String fragmentSource = null;
        try {
            String[] sources = Files.readString(Paths.get(path)).split("#type ");
            for (String source : sources) {
                if (source.startsWith("fragment")) {
                    fragmentSource = source.substring(source.indexOf('#'));
                } else if (source.startsWith("vertex")) {
                    vertexSource = source.substring(source.indexOf('#'));
                }
            }
        } catch (IOException e) {
            System.out.println("could not read file " + path);
            e.printStackTrace();
        }

        assert vertexSource != null : "No vertex source found in " + path;
        assert fragmentSource != null : "No fragment source found in " + path;

        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("V shader compile failed. " + name);
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("F shader compile failed. " + name);
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        shaderID = glCreateProgram();
        glAttachShader(shaderID, vertexID);
        glAttachShader(shaderID, fragmentID);
        glLinkProgram(shaderID);

        if (glGetProgrami(shaderID, GL_LINK_STATUS) == GL_FALSE) {
            int len = glGetProgrami(shaderID, GL_INFO_LOG_LENGTH);
            System.out.println("Shader link failed. " + name);
            System.out.println(glGetProgramInfoLog(shaderID, len));
            assert false : "";
        }

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    public void use() {
        glUseProgram(shaderID);
    }

    public void detach() {
        glUseProgram(0);
    }

    private void uploadUniform(String name, Matrix4f inMatrix) {
        int varLocation = glGetUniformLocation(shaderID, name);
        use();
        FloatBuffer inMatrixBuffer = BufferUtils.createFloatBuffer(16);
        inMatrix.get(inMatrixBuffer);
        glUniformMatrix4fv(varLocation, false, inMatrixBuffer);
    }

    public void uploadUniform(String name, int value) {
        use();
        int varLocation = glGetUniformLocation(shaderID, name);
        glUniform1i(varLocation, value);
    }

    public void uploadUniform(String name, float value) {
        int varLocation = glGetUniformLocation(shaderID, name);
        use();
        glUniform1f(varLocation, value);
    }

    public void uploadTexture(String name, int slot) {
        int varLocation = glGetUniformLocation(shaderID, name);
        use();
        glUniform1f(varLocation, slot);
    }

    public void useCamera(Camera cam) {
        uploadUniform("projection", cam.getProjectionMatrix());
        uploadUniform("view", cam.getViewMatrix());
    }
}
