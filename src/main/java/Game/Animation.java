package Game;

import org.joml.Vector2f;
import windowStuff.SingleAnimationSprite;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;

public class Animation implements TickDetect {

    private final Sprite sprite;

    private float scaling = 1;
    private Vector2f linearScaling = new Vector2f(0, 0);
    private float duration;

    public Animation(String anim, SpriteBatching bs, float duration, float x, float y, float width,
                     float height, int layer) {
        sprite = new SingleAnimationSprite(anim, duration, x, y, width, height, layer, "basic");

        bs.addSprite(sprite);
        this.duration = duration;
    }

    public Animation(Sprite anim, float duration) {
        sprite = anim;
        this.duration = duration;
    }

    public Animation setScaling(float value) {
        scaling = value;
        return this;
    }

    public Animation setLinearScaling(Vector2f value) {
        linearScaling = value;
        return this;
    }

    @Override
    public void onGameTick(int tick) {
        sprite.scale(scaling);
        sprite.setSize(2 * sprite.getWidth() + linearScaling.x,
                2 * sprite.getHeight() + linearScaling.y);
        duration -= Game.tickIntervalMillis / 1000f;
        if (duration < 0) {
            delete();
        }
    }

    @Override
    public void delete() {
        sprite.delete();
    }

    @Override
    public boolean WasDeleted() {
        return sprite.isDeleted();
    }
}
