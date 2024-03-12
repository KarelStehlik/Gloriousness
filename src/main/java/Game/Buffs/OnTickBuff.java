package Game.Buffs;

import Game.DamageType;
import Game.Game;
import Game.GameObject;
import java.util.Iterator;
import java.util.TreeSet;
import windowStuff.Sprite;

public class OnTickBuff<T extends GameObject> implements Buff<T>, Comparable<OnTickBuff<T>>{

  private static long staticId = 0;
  private final long id;

  private final float expiryTime;
  private final Modifier<T> mod;

  public OnTickBuff(float dur, Modifier<T> effect) {
    mod=effect;
    expiryTime = Game.get().getTicks() + dur / Game.tickIntervalMillis;
    id = staticId;
    staticId++;
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

    private final TreeSet<OnTickBuff<T>> effs = new TreeSet<>();

    protected Aggregator() {
    }

    @Override
    public void delete(T target) {
      effs.clear();
    }

    @Override
    public void add(Buff<T> b, T target) {
      assert b instanceof OnTickBuff<T>;
      var buff = (OnTickBuff<T>) b;
      effs.add(buff);
    }

    @Override
    public void tick(T target) {
      float time = Game.get().getTicks();

      for (Iterator<OnTickBuff<T>> iterator = effs.iterator(); iterator.hasNext(); ) {
        OnTickBuff<T> ig = iterator.next();
        if (ig.expiryTime > time) {
          ig.mod.mod(target);
        }else {
          iterator.remove();
          ig.mod.mod(target);
        }
      }
    }
  }
}