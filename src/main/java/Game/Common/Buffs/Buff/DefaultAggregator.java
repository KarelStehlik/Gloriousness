package Game.Common.Buffs.Buff;
import Game.Misc.Game;
import GlobalUse.Log;
import Game.Misc.GameObject;

import java.util.*;

public class DefaultAggregator<T extends GameObject> implements BuffAggregator<T> {

    protected final List<DefaultBuff<T>> effs = new ArrayList<>(1);
    protected SortedSet<DefaultBuff<T>> buffsByExpiration = new TreeSet<>(this::compareByexpiryTime);
    private int compareByexpiryTime(DefaultBuff<T> b1, DefaultBuff<T> b2) {
        if (Float.compare(b1.expiryTime, b2.expiryTime) != 0) {
            return Float.compare(b1.expiryTime, b2.expiryTime);
        }
        return Long.compare(b1.id, b2.id);
    }

    protected DefaultAggregator() {
    }

    @Override
    public boolean add(Buff<T> e, T target) {
        DefaultBuff<T> buff=(DefaultBuff<T>)e;
        if (buff.expiryTime != Float.POSITIVE_INFINITY) {
            buffsByExpiration.add(buff);
        }
        effs.add(buff);
        target.onStatsUpdate();
        return true;
    }

    @Override
    public void remove(Buff<T> b, T target) {
        DefaultBuff<T> buff=(DefaultBuff<T>)b;
        assert effs.contains(b) :"could not find "+b+" in buffs";
        buffsByExpiration.remove(buff);
        effs.remove(buff);
    }


    @Override
    public void tick(T target) {
        float time = Game.get().getTicks();

        for (Iterator<DefaultBuff<T>> iterator = effs.iterator(); iterator.hasNext(); ) {
            DefaultBuff<T> buff = iterator.next();
            if (buff.expiryTime > time) {
                break;
            }
            iterator.remove();
        }
    }

    @Override
    public void delete(T target) {
        effs.clear();
    }

    protected BuffAggregator<T> copy(){
        Log.write("Buff with inheritance but no copy method inheriting! Buff class: "+this.getClass());
        return null;
    }
    @Override
    public BuffAggregator<T> copyForChild(T newTarget) {
        for (DefaultBuff<T> buff : effs) {
            if (buff.spreads) {
                return copy();
            }
        }
        return null;
    }
}
