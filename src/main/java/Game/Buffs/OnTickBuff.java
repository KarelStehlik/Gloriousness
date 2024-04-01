package Game.Buffs;

import Game.Game;
import Game.GameObject;
import general.Util;
import java.util.ArrayList;
import java.util.List;

public class OnTickBuff<T extends GameObject> implements Buff<T>, Comparable<OnTickBuff<T>> {

  private final long id;

  private final float expiryTime;
  private final Modifier<T> mod;
  private final boolean spreads;

  public OnTickBuff(Modifier<T> effect) {
    this(Float.POSITIVE_INFINITY, effect, true);
  }

  public OnTickBuff(float dur, Modifier<T> effect) {
    this(dur, effect, true);
  }

  public OnTickBuff(float dur, Modifier<T> effect, boolean spreadsToChildren) {
    mod = effect;
    expiryTime = Game.get().getTicks() + dur / Game.tickIntervalMillis;
    id = Util.getUid();
    spreads = spreadsToChildren;
  }

  private OnTickBuff(long id, float expiryTime, Modifier<T> mod, boolean spreads) {
    this.id = id;
    this.expiryTime = expiryTime;
    this.mod = mod;
    this.spreads = spreads;
  }

  private OnTickBuff<T> copy() {
    return new OnTickBuff<T>(Util.getUid(), expiryTime, mod, spreads);
  }

  @Override
  public int compareTo(OnTickBuff<T> o) {
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

    private final List<OnTickBuff<T>> effs = new ArrayList<>(1);

    protected Aggregator() {
    }

    @Override
    public boolean add(Buff<T> b, T target) {
      assert b instanceof OnTickBuff<T>;
      var buff = (OnTickBuff<T>) b;
      effs.add(buff);
      return true;
    }

    @Override
    public void tick(T target) {
      float time = Game.get().getTicks();

      int current = 0, undeleted = 0;
      for (; current < effs.size(); current++) {
        OnTickBuff<T> buff = effs.get(current);
        if (buff.expiryTime > time) {
          effs.set(undeleted, buff);
          undeleted++;
          buff.mod.mod(target);
        }
      }
      if (effs.size() > undeleted) {
        effs.subList(undeleted, effs.size()).clear();
      }
    }

    @Override
    public void delete(T target) {
      effs.clear();
    }

    @Override
    public BuffAggregator<T> copyForChild(T newTarget) {
      Aggregator copy = new Aggregator();
      for (var eff : effs) {
        copy.add(eff.copy(), newTarget);
      }
      return copy;
    }
  }
}