package Game.Buffs;

import Game.Game;
import Game.GameObject;
import general.RefFloat;
import general.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class StatBuff<T extends GameObject> implements Buff<T> {

  private final Type type;
  private final float[] stats;
  private final int statModified;
  private final float value;
  private final long id;
  private final float expiry;

  public StatBuff(Type type, float duration, float[] stats, int statModified, float value) {
    this.expiry = Game.get().getTicks() + duration / Game.tickIntervalMillis;
    this.type = type;
    this.stats = stats;
    this.statModified=statModified;
    this.value = value;
    id = Util.getUid();
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
    final int target;
    final float[] stats;
    float added = 0, increased = 1;
    double more = 1;
    //Map<Float, Integer> moreModifiers = new HashMap<>(1);

    TotalModifier(float[] stats, int target) {
      this.target = target;
      this.stats=stats;
      ogValue = stats[target];
    }

    void apply() {
      stats[target]= Math.max((ogValue + added) * increased * (float) more, 0);
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
      stats[target]= ogValue;
      //moreModifiers.clear();
    }
  }

  private class Aggregator implements BuffAggregator<T> {

    SortedSet<StatBuff<T>> buffsByExpiration = new TreeSet<>(StatBuff.this::compareByExpiry);

    Map<Integer, TotalModifier> modifiers = new HashMap<>(2);

    @Override
    public boolean add(Buff<T> b, T target) {
      assert b instanceof StatBuff<T>;
      StatBuff<T> buff = (StatBuff<T>) b;
      if (buff.expiry < Float.POSITIVE_INFINITY) {
        buffsByExpiration.add(buff);
      }
      TotalModifier mod = modifiers.computeIfAbsent(buff.statModified, s ->new TotalModifier(stats, statModified));
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
        modifiers.get(buff.statModified).remove(buff);
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
