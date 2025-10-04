package Game.Common.Buffs.Buff;

import Game.Misc.Game;
import Game.Misc.GameObject;
import GlobalUse.Util;

public abstract class DefaultBuff<T extends GameObject> implements Buff<T>, Comparable<DefaultBuff<T>>{
    //empty buff so that you don't have to make an aggregator and all that stuff when making a buff


    protected final long id;

    public final float expiryTime;
    public final boolean spreads;

    public DefaultBuff() {
        this(Float.POSITIVE_INFINITY, true);
    }

    public DefaultBuff(float duration) {
        this(duration, true);
    }

    public DefaultBuff(float duration, boolean spreadsToChildren) {
        this(duration, true,Util.getUid());
    }

    protected DefaultBuff(float duration, boolean spreads, long id) {
        this.id = id;
        expiryTime = Game.get().getTicks() + duration / Game.tickIntervalMillis;
        this.spreads = spreads;
    }
    
    @Override
    public int compareTo(DefaultBuff<T> o) {
        int floatComp = Float.compare(expiryTime, o.expiryTime);
        if (floatComp != 0) {
            return floatComp;
        }
        return Long.compare(id, o.id);
    }

    @Override
    public BuffAggregator<T> makeAggregator() {
        return new DefaultAggregator<T>();
    }
    
}
