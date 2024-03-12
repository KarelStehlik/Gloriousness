package Game.Buffs;

import Game.DamageType;
import Game.Game;
import Game.GameObject;
import java.util.Iterator;
import java.util.TreeSet;
import windowStuff.Sprite;

public class DelayedTrigger<T extends GameObject> implements Buff<T>, Comparable<DelayedTrigger<T>>{

  private static long staticId = 0;
  private final long id;

  private final float expiryTime;
  private final Modifier<T> mod;
  private boolean onDeath;

  public DelayedTrigger(float dur, Modifier<T> effect, boolean triggerOnDeath) {
    mod=effect;
    expiryTime = Game.get().getTicks() + dur / Game.tickIntervalMillis;
    id = staticId;
    staticId++;
    onDeath=triggerOnDeath;
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
    public void delete(T target) {
      for(var eff:effs){
        if(eff.onDeath){
          eff.mod.mod(target);
        }
      }
      effs.clear();
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

      for (Iterator<DelayedTrigger<T>> iterator = effs.iterator(); iterator.hasNext(); ) {
        DelayedTrigger<T> ig = iterator.next();
        if (ig.expiryTime > time) {
          break;
        }
        iterator.remove();
        ig.mod.mod(target);
      }
      }
    }
}