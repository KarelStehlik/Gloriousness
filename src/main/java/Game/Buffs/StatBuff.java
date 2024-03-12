package Game.Buffs;

import Game.Game;
import Game.GameObject;
import general.RefFloat;
import general.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class StatBuff<T extends GameObject> implements Buff<T> {

  private final Type type;
  private final RefFloat stat;
  private final float value;
  private final long id;
  private final float expiry;

  public StatBuff(Type type, float duration, RefFloat stat, float value) {
    this.expiry = Game.get().getTicks() + duration / Game.tickIntervalMillis;
    this.type = type;
    this.stat = stat;
    this.value = value;
    id= Util.getUid();
  }

  private int compareByExpiry(StatBuff<T> b1, StatBuff<T> b2) {
    if (Float.compare(b1.expiry, b2.expiry) != 0) {
      return Float.compare(b1.expiry, b2.expiry);
    }
    return Long.compare(b1.id, b2.id);
  }

  @Override
  public BuffAggregator<T> makeAggregator() {
    return new Aggregator();
  }

  public enum Type {ADDED, INCREASED, MORE}

  // TODO: re-calculate "more" once in a while to avoid rounding errors
  private static class TotalModifier {

    final float ogValue;
    RefFloat target;
    float added = 0, increased = 1, more = 1;
    //Map<Float, Integer> moreModifiers = new HashMap<>(1);

    TotalModifier(RefFloat target) {
      this.target = target;
      ogValue = target.get();
    }

    void apply() {
      target.set(Math.max((ogValue + added) * increased * more, 0));
    }

    void addAdded(float value) {
      added += value;
    }

    void increase(float value) {
      increased += value;
    }

    void addMore(float value) {
      //int count = moreModifiers.computeIfAbsent(value,v->0);
      //moreModifiers.put(value, count+1);
      more *= value;
    }

    void removeMore(float value) {
      //int count = moreModifiers.get(value);
      //if(count==1){
      //  moreModifiers.remove(value);
      //}else{
      //  moreModifiers.put(value, count-1);
      //}
      more /= value;
    }

    void add(StatBuff<?> b) {
      switch (b.type) {
        case MORE -> addMore(b.value);
        case ADDED -> addAdded(b.value);
        case INCREASED -> increase(b.value);
      }
      apply();
    }

    void remove(StatBuff<?> b) {
      switch (b.type) {
        case MORE -> removeMore(b.value);
        case ADDED -> addAdded(-b.value);
        case INCREASED -> increase(-b.value);
      }
      apply();
    }

    void delete() {
      target.set(ogValue);
      //moreModifiers.clear();
    }
  }

  private class Aggregator implements BuffAggregator<T> {

    TreeSet<StatBuff<T>> buffsByExpiration = new TreeSet<>(StatBuff.this::compareByExpiry);

    Map<RefFloat, TotalModifier> modifiers = new HashMap<>(2);

    @Override
    public boolean add(Buff<T> b, T target) {
      assert b instanceof StatBuff<T>;
      StatBuff<T> buff = (StatBuff<T>) b;
      if (buff.expiry < Float.POSITIVE_INFINITY) {
        buffsByExpiration.add(buff);
      }
      TotalModifier mod = modifiers.computeIfAbsent(buff.stat, TotalModifier::new);
      mod.add(buff);
      target.onStatsUpdate();
      return true;
    }

    @Override
    public void tick(T target) {
      boolean changed = false;
      int tick = Game.get().getTicks();
      for (Iterator<StatBuff<T>> iterator = buffsByExpiration.iterator(); iterator.hasNext(); ) {
        StatBuff<T> buff = iterator.next();
        if (buff.expiry > tick) {
          break;
        }
        iterator.remove();
        modifiers.get(buff.stat).remove(buff);
        changed = true;
      }
      if (changed) {
        target.onStatsUpdate();
      }
    }

    @Override
    public void delete(T target) {
      modifiers.values().forEach(TotalModifier::delete);
      modifiers.clear();
      buffsByExpiration.clear();
    }
  }
}
