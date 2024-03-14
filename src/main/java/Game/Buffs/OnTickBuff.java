package Game.Buffs;

import Game.Game;
import Game.GameObject;
import general.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class OnTickBuff<T extends GameObject> implements Buff<T>, Comparable<OnTickBuff<T>> {

  private final long id;

  private final float expiryTime;
  private final Modifier<T> mod;

  public OnTickBuff(float dur, Modifier<T> effect) {
    mod = effect;
    expiryTime = Game.get().getTicks() + dur / Game.tickIntervalMillis;
    id = Util.getUid();
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

      int current=0,undeleted=0;
      for(; current<effs.size();current++){
        OnTickBuff<T> buff = effs.get(current);
        if (buff.expiryTime > time) {
          effs.set(undeleted,buff);
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
  }
}