package Game.Buffs;

import Game.Game;
import Game.GameObject;
import general.Util;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class StatBuff<T extends GameObject> implements Buff<T> {

  private final Type type;
  private final int statModified;
  private final float value;
  private final long id;
  private final float expiry;

  public StatBuff(Type type, int statModified, float value) {
    this(type, Float.POSITIVE_INFINITY, statModified, value);
  }

  public StatBuff(Type type, float durationMillis, int statModified, float value) {
    this.expiry = Game.get().getTicks() + durationMillis / Game.tickIntervalMillis;
    this.type = type;
    this.statModified = statModified;
    this.value = value;
    id = Util.getUid();
  }

  private StatBuff(Type type, int statModified, float value, long id, float expiry) {
    this.type = type;
    this.statModified = statModified;
    this.value = value;
    this.id = id;
    this.expiry = expiry;
  }

  public StatBuff<T> copy() {
    return new StatBuff<T>(type, statModified, value, Util.getUid(), expiry);
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

  public enum Type {ADDED, INCREASED, MORE, FINALLY_ADDED}

  // TODO: re-calculate "more" once in a while to avoid rounding errors
  private static class TotalModifier {

    final float ogValue;
    public final int target;
    float added = 0, increased = 1, finallyAdded = 0;
    double more = 1;
    //Map<Float, Integer> moreModifiers = new HashMap<>(1);

    TotalModifier(float[] stats, int target) {
      this.target = target;
      ogValue = stats[target];
    }

    TotalModifier(float ogValue, int target, float added, float increased, double more,
        float finallyAdded) {
      this.ogValue = ogValue;
      this.target = target;
      this.added = added;
      this.increased = increased;
      this.more = more;
      this.finallyAdded = finallyAdded;
    }

    TotalModifier copy(GameObject newTarget) {
      var co = new TotalModifier(newTarget.getStats()[target], target, added, increased, more,
          finallyAdded);
      co.apply(newTarget);
      return co;
    }

    void apply(GameObject tar) {
      tar.getStats()[target] = Math.max((ogValue + added) * increased * (float) more + finallyAdded,
          0);
    }

    void addAdded(float value) {
      added += value;
    }

    void addFinallyAdded(float value) {
      finallyAdded += value;
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

    void add(StatBuff<?> b, GameObject tar) {
      switch (b.type) {
        case MORE -> addMore(b.value);
        case ADDED -> addAdded(b.value);
        case INCREASED -> increase(b.value);
        case FINALLY_ADDED -> addFinallyAdded(b.value);
      }
      apply(tar);
    }

    void remove(StatBuff<?> b, GameObject tar) {
      switch (b.type) {
        case MORE -> removeMore(b.value);
        case ADDED -> addAdded(-b.value);
        case INCREASED -> increase(-b.value);
        case FINALLY_ADDED -> addFinallyAdded(-b.value);
      }
      apply(tar);
    }

    void delete(GameObject tar) {
      tar.getStats()[target] = ogValue;
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
      if (buff.expiry != Float.POSITIVE_INFINITY) {
        buffsByExpiration.add(buff);
      }
      TotalModifier mod = modifiers.computeIfAbsent(buff.statModified,
          s -> new TotalModifier(target.getStats(), buff.statModified));
      mod.add(buff, target);
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
        modifiers.get(buff.statModified).remove(buff, target);
        changed = true;
      }
      if (changed) {
        target.onStatsUpdate();
      }
    }

    @Override
    public void delete(T target) {
      modifiers.values().forEach(m -> m.delete(target));
      modifiers.clear();
      buffsByExpiration.clear();
    }

    @Override
    public BuffAggregator<T> copyForChild(T newTarget) {
      Aggregator copy = new Aggregator();
      for (var eff : buffsByExpiration) {
        copy.buffsByExpiration.add(eff.copy());
      }
      for (var eff : modifiers.entrySet()) {
        copy.modifiers.put(eff.getKey(), eff.getValue().copy(newTarget));
      }
      return copy;
    }
  }

  @Override
  public String toString() {
    return "StatBuff{"
        + "type=" + type
        + ", statModified=" + statModified
        + ", value=" + value
        + ", expiry=" + expiry
        + '}';
  }
}
