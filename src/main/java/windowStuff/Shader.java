package windowStuff;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
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
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
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
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import general.Constants;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Shader {

    private static final Pattern COMPILE = Pattern.compile(
        "#type ");
    final String name;
    final int shaderID;
    final int staticDataPerVertex = 6;
    private final int vao, vbo;
    private long vboSize = 1024;
    private boolean rebufferAllStatic = true;

    public Shader(String path) {

        // compile shaders
        {
            String[] split = path.split("/");
            name = split[split.length - 1].split("\\.")[0];

            String vertexSource = null;
            String fragmentSource = null;
            try {
                String[] sources = COMPILE.split(Files.readString(Paths.get(path)));
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

        //setup vao
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        {
            int positionCount = 2;
            int floatBytes = Float.BYTES;
            int vertexBytes = Constants.VertexSizeFloats * floatBytes;

            glBindBuffer(GL_ARRAY_BUFFER, Graphics.streamVbo);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, positionCount, GL_FLOAT, false, vertexBytes, 0);

            int colorCount = 4;
            glEnableVertexAttribArray(2);
            int texCoords = 2;
            glVertexAttribPointer(2, texCoords, GL_FLOAT, false, vertexBytes,
                (positionCount + colorCount) * floatBytes);

            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER,vboSize, GL_DYNAMIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, colorCount, GL_FLOAT, false, vertexBytes, positionCount*floatBytes);
        }

        glBindVertexArray(0);
    }

    private float[] vertexArray;

    protected class DrawCall {
        private int offset = 0;
        private final int spriteCount;
        protected DrawCall(int numSprites) {
            while (numSprites * 4L * staticDataPerVertex > vboSize) {
                growVbo();
            }
            vertexArray = new float[numSprites * Constants.SpriteSizeFloats];
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            spriteCount=numSprites;
        }

        protected void addSprite(Sprite sprite) {
            sprite.updateVertices();
            sprite.bufferPositions(offset, vertexArray);

            if(sprite.rebufferStatic || rebufferAllStatic) {
                sprite.bufferStatic((long) offset * 2);
            }
            offset += Constants.SpriteSizeFloats;
        }

        protected void draw(Texture texture) {
            rebufferAllStatic=false;
            glBindBuffer(GL_ARRAY_BUFFER, Graphics.streamVbo);
            glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_STREAM_DRAW);

            glBindVertexArray(vao);

            use();
            texture.bind();
            uploadTexture("sampler", 0);
            glActiveTexture(GL_TEXTURE0);

            glDrawElements(GL_TRIANGLES, 6 * spriteCount, GL_UNSIGNED_INT, 0);

            detach();
            glBindVertexArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    private void growVbo(){
        vboSize *=2L;
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vboSize, GL_DYNAMIC_DRAW);
        rebufferAllStatic=true;
        System.out.println("new vbo size: "+vboSize);
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
