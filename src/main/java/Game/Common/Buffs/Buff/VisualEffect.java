package Game.Common.Buffs.Buff;

import Game.Common.Buffs.Modifier.Modifier;
import Game.Misc.GameObject;
import Game.WorldStuff.Game;
import GlobalUse.Util;
import windowStuff.GraphicsOnly.Sprite.Sprite;

import java.util.ArrayList;
import java.util.List;

public class VisualEffect<T extends GameObject> extends OnTickBuff<T> {
    private final Sprite image;
    private final SpriteGenerator imageGenerator;
    private final String idString;

    public VisualEffect(String _id, Sprite image) {
        super((T target) -> image.setPosition(target.x, target.y));
        idString = _id;
        this.image = image;
        imageGenerator = null;
    }
    public VisualEffect(String _id, float dur, SpriteGenerator image, boolean spreadsToChildren) {
        this(_id,dur,image,image.get(),spreadsToChildren);
    }
    public VisualEffect(String _id, float dur,SpriteGenerator image, Sprite spr, boolean spreadsToChildren) {
        super(dur, (T target) -> spr.setPosition(target.x, target.y), spreadsToChildren);
        idString = _id;
        this.image = spr;
        imageGenerator = image;
    }

    protected VisualEffect(String _id, long id, float expiryTime, SpriteGenerator image, boolean spreads) {
        this(_id, id, expiryTime, image, image.get(), spreads);
    }

    protected VisualEffect(String _id, long id, float expiryTime, SpriteGenerator image,Sprite spr, boolean spreads) {
        super(id, expiryTime, (T target) -> spr.setPosition(target.x, target.y), spreads);
        idString = _id;
        imageGenerator = image;
        this.image=spr;
    }

    @FunctionalInterface
    public interface SpriteGenerator {
        Sprite get();
    }

    private VisualEffect<T> copy() {
        return new VisualEffect<T>(idString, Util.getUid(), expiryTime, imageGenerator, spreads);
    }

    @Override
    public BuffAggregator<T> makeAggregator() {
        return new Aggregator();
    }

    public class Aggregator implements BuffAggregator<T> {

        private final List<VisualEffect<T>> effs = new ArrayList<>(1);

        protected Aggregator() {
        }

        public boolean hasExtendEffect(String _id,float duration) {
            for (VisualEffect<T> eff : effs) {
                if (eff.idString.equals(_id)){
                    eff.expiryTime=Math.max(Game.get().getTicks() +duration / Game.tickIntervalMillis,eff.expiryTime);
                    effs.sort(OnTickBuff::compareTo);
                    return true;
                }

            }
            return false;
        }

        @Override
        public boolean add(Buff<T> b, T target) {
            assert b instanceof VisualEffect<T>;
            var buff = (VisualEffect<T>) b;
            effs.add(buff);
            buff.image.setLayer(target.getSprite().getLayer() + 2);
            effs.sort(OnTickBuff::compareTo);
            return true;
        }

        @Override
        public void remove(Buff<T> b, T target) {
            VisualEffect<T> buff = (VisualEffect<T>) b;
            buff.image.delete();
            effs.remove((VisualEffect<T>) b);
        }

        @Override
        public void tick(T target) {
            float time = Game.get().getTicks();

            int current = 0, undeleted = 0;
            for (; current < effs.size(); current++) {
                VisualEffect<T> buff = effs.get(current);
                if (buff.expiryTime > time) {
                    effs.set(undeleted, buff);
                    undeleted++;
                    buff.mod.mod(target);
                }
            }
            if (effs.size() > undeleted) {
                for(int i=undeleted;i<effs.size();i++){
                    remove(effs.get(i),target);
                }
            }
        }

        @Override
        public void delete(T target) {
            for (VisualEffect<T> eff : effs) {
                eff.image.delete();
            }
            effs.clear();
        }

        @Override
        public BuffAggregator<T> copyForChild(T newTarget) {
            Aggregator copy = new VisualEffect.Aggregator();
            for (var eff : effs) {
                copy.add(eff.copy(), newTarget);
            }
            return copy;
        }
    }

}
