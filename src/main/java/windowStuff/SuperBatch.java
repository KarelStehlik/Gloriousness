package windowStuff;

import general.Constants;
import general.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class SuperBatch implements SpriteBatching {

    private final List<Sprite> spritesToAdd = new ArrayList<>(20);
    private final int ebo, vao, vbo;
    private final List<Batch> batches = new ArrayList<>(5);
    private final ImageSet images;
    private int eboSize = 1024;
    private Camera camera;
    private boolean visible = true;

    public SuperBatch() {
        this.images = Graphics.getLoadedImages();
        //batches = new LinkedList<>(); // is sorted
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        {
            int positionCount = 3;
            int floatBytes = Float.BYTES;
            int vertexBytes = Constants.VertexSizeFloats * floatBytes;

            vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            glVertexAttribPointer(0, positionCount, GL_FLOAT, false, vertexBytes, 0);
            glEnableVertexAttribArray(0);

            int colorCount = 4;
            glVertexAttribPointer(1, colorCount, GL_FLOAT, false, vertexBytes, positionCount * floatBytes);
            glEnableVertexAttribArray(1);

            int texCoords = 2;
            glVertexAttribPointer(2, texCoords, GL_FLOAT, false, vertexBytes,
                    (positionCount + colorCount) * floatBytes);
            glEnableVertexAttribArray(2);
        }

        glBindVertexArray(0);
    }

    private void growEbo() {
        eboSize = (int) (eboSize * 1.5);
        int[] elements = new int[6 * eboSize];
        for (int i = 0; i < eboSize; i++) {
            elements[6 * i] = 2 + 4 * i;
            elements[6 * i + 1] = 1 + 4 * i;
            elements[6 * i + 2] = 4 * i;
            elements[6 * i + 3] = 4 * i;
            elements[6 * i + 4] = 1 + 4 * i;
            elements[6 * i + 5] = 3 + 4 * i;
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    @Override
    public void addSprite(Sprite sprite) {
        synchronized (spritesToAdd) {
            spritesToAdd.add(sprite);
        }
    }

    private void _addSprite(Sprite sprite) {
        sprite.mustBeRebatched = false;
        int layer = sprite.layer;
        String tex = sprite.textureName;
        Shader shader = sprite.shader;

        //find the right batch. todo: maybe a bin search?
        int smallerBatches = 0;
        while (smallerBatches < batches.size()
                && batches.get(smallerBatches).compareTo(layer, tex, shader) < 0) {
            smallerBatches++;
        }
        if (smallerBatches < batches.size()
                && batches.get(smallerBatches).compareTo(layer, tex, shader) == 0) {
            batches.get(smallerBatches).addSprite(sprite);
            return;
        }
        Batch b = new Batch(layer, tex, shader);
        b.addSprite(sprite);
        synchronized (batches) {
            batches.add(smallerBatches, b);
        }
    }

    @Override
    public void draw() {

        if (!visible) {
            return;
        }

        for (Shader shader : Data.getAllShaders()) {
            shader.useCamera(camera);
        }

        List<Sprite> rebatchSprites = new ArrayList<>(5);
        for (Batch b : batches) {
            var spriterator = b.sprites.iterator();
            while (spriterator.hasNext()) {
                final Sprite s = spriterator.next();
                if (s.mustBeRebatched) {
                    spriterator.remove();
                    rebatchSprites.add(s);
                }
            }
        }
        rebatchSprites.forEach(this::_addSprite);
        rebatchSprites.clear();

        synchronized (spritesToAdd) {
            spritesToAdd.forEach(this::_addSprite);
            spritesToAdd.clear();
        }

        int drawStart = 0;
        while (drawStart < batches.size()) {
            String texture = batches.get(drawStart).texture;
            Shader shader = batches.get(drawStart).shader;
            int spriteCount = batches.get(drawStart).squishSize();

            int drawEnd = drawStart + 1;

            while (drawEnd < batches.size() && batches.get(drawEnd).texture.equals(texture)
                    && batches.get(drawEnd).shader == shader) {
                spriteCount += batches.get(drawEnd).squishSize();
                drawEnd++;
            }

            while (eboSize < spriteCount) {
                growEbo();
            }

            float[] vertexArray = new float[spriteCount * Constants.SpriteSizeFloats];
            int offset = 0;

            for (int i = drawStart; i < drawEnd; i++) {
                offset += batches.get(i).bufferToArray(vertexArray, offset);
            }

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            glBindVertexArray(this.vao);

            shader.use();
            images.getTexture(texture).bind();
            shader.uploadTexture("sampler", 0);
            glActiveTexture(GL_TEXTURE0);

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);

            glDrawElements(GL_TRIANGLES, 6 * spriteCount, GL_UNSIGNED_INT, 0);

            shader.detach();
            glBindVertexArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

            drawStart = drawEnd;
        }

    }

    @Override
    public void useCamera(Camera cam) {
        this.camera = cam;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    private static class Batch implements Comparable<Batch> {

        public final int layer;
        public final String texture;
        public final Shader shader;
        private final List<Sprite> sprites = new LinkedList<>();

        Batch(int layer, String texture, Shader shader) {
            this.layer = layer;
            this.texture = texture;
            this.shader = shader;
        }

        /**
         * removes deleted sprites.
         *
         * @return number of sprites
         */
        int squishSize() {
            synchronized (sprites) {
                int count = 0;
                final var spriterator = sprites.iterator();
                while (spriterator.hasNext()) {
                    if (spriterator.next().isDeleted()) {
                        spriterator.remove();
                    } else {
                        count++;
                    }
                }
                return count;
            }
        }

        void addSprite(Sprite s) {
            synchronized (sprites) {
                sprites.add(s);
            }
        }

        @Override
        public int compareTo(Batch o) {
            if (layer - o.layer != 0) {
                return layer - o.layer;
            }
            if (texture.compareTo(o.texture) != 0) {
                return texture.compareTo(o.texture);
            }
            return shader.shaderID - o.shader.shaderID;
        }

        public int compareTo(int lay, String tex, Shader shade) {
            if (layer - lay != 0) {
                return layer - lay;
            }
            if (texture.compareTo(tex) != 0) {
                return texture.compareTo(tex);
            }
            return shader.shaderID - shade.shaderID;
        }

        public int bufferToArray(float[] arr, int offset) {
            int spriteOffset = 0;
            synchronized (sprites) {
                for (var sprite : sprites) {
                    sprite.updateVertices();
                    sprite.bufferToArray(offset + spriteOffset, arr);
                    spriteOffset += Constants.SpriteSizeFloats;
                }
                return spriteOffset;
            }
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Batch && compareTo((Batch) o) == 0;
        }

        @Override
        public int hashCode() {
            int result = layer;
            result = 31 * result + (texture != null ? texture.hashCode() : 0);
            result = 31 * result + (shader != null ? shader.hashCode() : 0);
            result = 31 * result + sprites.hashCode();
            return result;
        }
    }
}
