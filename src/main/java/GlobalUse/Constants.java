package GlobalUse;

import static org.lwjgl.opengl.GL11C.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11C.glGetIntegerv;

import java.nio.IntBuffer;

import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

public final class Constants {

    public static final int SpriteSizeFloats = 25;
    public static final Vector2i screenSize;

    //gameplay
    public static final int StartingHealth = 100;
    public static final int MobSpread = 10;

    public static enum layerInterval {
        bloon(10, 18, 15),
        groundMoab(21, 25),
        monkey(27, 40, 30),
        projectile(41, 49, 44),
        flyingMoab(50, 57, 55),
        flyingMonkey(61, 67),
        ui(70, 120);

        public int max;
        public int min;
        public int defalt; //default is a keyword in java...

        layerInterval(int min, int max) {
            this(max, min, (max + min) / 2);
        }

        layerInterval(int max, int min, int def) {
            this.max = max;
            this.min = min;
            this.defalt = def;
        }
    }

    static {
        IntBuffer b = BufferUtils.createIntBuffer(4);
        glGetIntegerv(GL_VIEWPORT, b);
        screenSize = new Vector2i(b.get(2), b.get(3));
    }

    private Constants() {
    }
}
