package windowStuff;

import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengles.GLES20.GL_ARRAY_BUFFER;

import general.Data;
import general.Util;
import java.util.Objects;

public class Sprite implements AbstractSprite {

    private final float[] positions = new float[8];
    protected boolean hasUnsavedChanges = true;
    protected String textureName;
    protected int layer;
    protected Shader shader;
    protected boolean deleted = false;
    protected boolean mustBeRebatched = false;
    private boolean hidden = false;
    private float x;
    private float y;
    private float[] colors = Util.getBaseColors(1);
    private float[] texCoords = new float[8];
    private float rotation = 0;
    private float width, height;
    private int imageId;
    private Animation animation;
    protected boolean rebufferStatic = true;

    private Short[] staticData;

    public Sprite(String imageName, int layer) {
        this(imageName, 0, 0, 100, 100, layer, "basic");
    }

    public Sprite(String imageName, float sizeX, float sizeY, int layer,
                  String shader) {
        this(imageName, 0, 0, sizeX, sizeY, layer, shader);
    }

    public Sprite(String imageName, float sizeX, float sizeY, int layer,
                  String shader, SpriteBatching bs) {
        this(imageName, 0, 0, sizeX, sizeY, layer, shader);
        this.addToBs(bs);
    }

    public Sprite(String imageName, float sizeX, float sizeY, int layer) {
        this(imageName, 0, 0, sizeX, sizeY, layer, "basic");
    }

    public Sprite(String imageName, float sizeX, float sizeY, int layer, SpriteBatching bs) {
        this(imageName, 0, 0, sizeX, sizeY, layer, "basic");
        this.addToBs(bs);
    }

    public Sprite(String imageName, float x, float y, float sizeX, float sizeY, int layer,
                  String shader) {
        this.setX(x);
        this.setY(y);
        width = sizeX / 2;
        height = sizeY / 2;
        this.shader = Data.getShader(shader);
        this.layer = layer;
        setImage(imageName);
        this.animation = () -> {
        };
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public int getLayer() {
        return layer;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Sprite addToBs(SpriteBatching bs) {
        bs.addSprite(this);
        return this;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    protected void onAnimationEnd() {
        animation = () -> {
        };
    }

    @Override
    public Sprite playAnimation(Animation anim) {
        animation = anim;
        return this;
    }

    @Override
    public Sprite setShader(String shader) {
        this.shader = Data.getShader(shader);
        this.mustBeRebatched = true;
        return this;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public Sprite setRotation(float r) {
        rotation = r;
        hasUnsavedChanges = true;
        return this;
    }

    @Override
    public Sprite setImage(String name) {
        imageId = Graphics.getLoadedImages().getImageId(name);
        String newTexture = Graphics.getLoadedImages().getImageTexture(imageId);
        if (!Objects.equals(this.textureName, newTexture)) {
            this.textureName = newTexture;
            mustBeRebatched = true;
        }
        setUV();
        return this;
    }

    private void setUV() {
        texCoords = Graphics.getLoadedImages().getImageCoordinates(imageId);
    }

    @Override
    public Sprite setPosition(float X, float Y) {
        setX(X);
        setY(Y);
        hasUnsavedChanges = true;
        return this;
    }

    @Override
    public Sprite scale(float multiplier) {
        width *= multiplier;
        height *= multiplier;
        hasUnsavedChanges = true;
        return this;
    }

    @Override
    public Sprite setSize(float w, float h) {
        width = w / 2;
        height = h / 2;
        hasUnsavedChanges = true;
        return this;
    }

    public synchronized void updateVertices() {
        animation.update();
        if (!hasUnsavedChanges || hidden) {
            return;
        }
        float rotationSin = Util.sin(rotation);
        float rotationCos = Util.cos(rotation);
        float XC = width * rotationCos, YC = height * rotationCos,
                XS = width * rotationSin, YS = height * rotationSin;

        //+-
        positions[0] = getX() + XC - YS;
        positions[1] = getY() + XS + YC;
        //-+
        positions[2] = getX() - XC + YS;
        positions[3] = getY() - XS - YC;
        //++
        positions[4] = getX() + XC + YS;
        positions[5] = getY() + XS - YC;
        //--
        positions[6] = getX() - XC - YS;
        positions[7] = getY() - XS + YC;

        hasUnsavedChanges = false;
    }

    protected synchronized void bufferPositions(int offset, float[] vertices) {
        if (hidden) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            int off = offset + 8 * i;
            vertices[off] = positions[2 * i];
            vertices[off + 1] = positions[2 * i + 1];

            //vertices[off + 2] = colors[4 * i];
            //vertices[off + 3] = colors[4 * i + 1];
            //vertices[off + 4] = colors[4 * i + 2];
            //vertices[off + 5] = colors[4 * i + 3];

            vertices[off + 6] = texCoords[2 * i];
            vertices[off + 7] = texCoords[2 * i + 1];
        }
    }

    protected synchronized void bufferStatic(long offsetBytes){
        glBufferSubData(GL_ARRAY_BUFFER,offsetBytes,colors);
        rebufferStatic=false;
    }

    @Override
    public Sprite setColors(float[] colors) {
        assert colors.length == 16 : "expected 16 colors for sprite.";
        this.colors = colors;
        rebufferStatic=true;
        return this;
    }

    @Override
    public void delete() {
        deleted = true;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public Sprite setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public Sprite setY(float y) {
        this.y = y;
        return this;
    }

    @FunctionalInterface
    interface Animation {

        void update();
    }

    public class BasicAnimation implements Animation {

        private final int length;
        private final float frameLengthNano;
        private final double startTime;
        private final int end;
        private final int first;

        public BasicAnimation(int first, float duration) {
            this(first, duration, first);
        }

        public BasicAnimation(int first, float duration, int endImageId) {
            length = Graphics.getLoadedImages().getAnimationLength(first);
            frameLengthNano = duration / length * 1000000000;
            startTime = System.nanoTime();
            end = endImageId;
            this.first = first;
        }

        @Override
        public void update() {
            int frame = (int) ((System.nanoTime() - startTime) / frameLengthNano);
            hasUnsavedChanges = true;
            if (frame > length) {
                imageId = end;
                onAnimationEnd();
            } else {
                imageId = first + frame;
            }
            setUV();
        }
    }
}
