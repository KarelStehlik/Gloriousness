package Game.Buffs;

import Game.Game;
import Game.GameObject;
import general.Log;
import general.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class DelayedTrigger<T extends GameObject> implements Buff<T>,
    Comparable<DelayedTrigger<T>> {

  private final long id;

  private final float expiryTime;
  private final Modifier<T> mod;
  private final boolean onDeath, spreads;

  public DelayedTrigger(Modifier<T> effect, boolean triggerOnDeath) {
    this(Float.POSITIVE_INFINITY, effect, triggerOnDeath, true);
  }

  public DelayedTrigger(float dur, Modifier<T> effect, boolean triggerOnDeath) {
    this(dur, effect, triggerOnDeath, true);
  }

  public DelayedTrigger(float dur, Modifier<T> effect, boolean triggerOnDeath,
      boolean spreadsToChildren) {
    mod = effect;
    expiryTime = Game.get().getTicks() + dur / Game.tickIntervalMillis;
    id = Util.getUid();
    onDeath = triggerOnDeath;
    spreads = spreadsToChildren;
  }

  private DelayedTrigger(long id, float expiryTime, Modifier<T> mod, boolean onDeath,
      boolean spreads) {
    this.id = id;
    this.expiryTime = expiryTime;
    this.mod = mod;
    this.onDeath = onDeath;
    this.spreads = spreads;
  }

  private DelayedTrigger<T> copy() {
    return new DelayedTrigger<T>(Util.getUid(), this.expiryTime, this.mod, this.onDeath,
        this.spreads);
  }

  @Override
  public int compareTo(DelayedTrigger<T> o) {
    int floatComp = Float.compare(expiryTime, o.expiryTime);
    if (floatComp != 0) {
      return floatComp;
    }
    return Long.compare(id, o.id);
  }

  @Override
  public BuffAggregator<T> makeAggregator() {
    return new Aggregator();
  }

  private class Aggregator implements BuffAggregator<T> {

    private final TreeSet<DelayedTrigger<T>> effs = new TreeSet<>();

    protected Aggregator() {
    }

    @Override
    public boolean add(Buff<T> b, T target) {
      assert b instanceof DelayedTrigger<T>;
      var buff = (DelayedTrigger<T>) b;
      effs.add(buff);
      return true;
    }

    @Override
    public void tick(T target) {
      float time = Game.get().getTicks();

      List<DelayedTrigger<T>> removed = new ArrayList<>(1);
      for (Iterator<DelayedTrigger<T>> iterator = effs.iterator(); iterator.hasNext(); ) {
        DelayedTrigger<T> ig = iterator.next();
        if (ig.expiryTime > time) {
          break;
        }
        iterator.remove();
        removed.add(ig);
      }
      for (var ig : removed) {
        ig.mod.mod(target);
      }
    }

    @Override
    public void delete(T target) {
      for (var eff : effs) {
        if (eff.onDeath) {
          target.world.addEvent(() -> eff.mod.mod(target));
        }
      }
      effs.clear();
    }

    @Override
    public BuffAggregator<T> copyForChild(T newTarget) {
      Aggregator copy = new Aggregator();
      for (var eff : effs) {
        if (eff.spreads) {
          copy.add(eff.copy(), newTarget);
        }
      }
      return copy;
    }
  }
}