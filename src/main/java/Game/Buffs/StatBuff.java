package Game.Buffs;

import Game.Game;
import Game.GameObject;
import java.util.Iterator;
import java.util.TreeSet;

public class StatBuff<T extends GameObject> implements Buff<T> {

  private static long staticId = 0;
  private final long id;
  private final int priority;
  private final Modifier<T> mod;
  private final float expiry;

  public StatBuff(int priority, float duration, Modifier<T> effect) {
    this.priority = priority;
    this.expiry = Game.get().getTicks() + duration / Game.tickIntervalMillis;
    this.mod = effect;
    id = staticId;
    staticId++;
  }

  private int compareByExpiry(StatBuff<T> b1, StatBuff<T> b2) {
    if (Float.compare(b1.expiry, b2.expiry) != 0) {
      return Float.compare(b1.expiry, b2.expiry);
    }
    return Long.compare(b1.id, b2.id);
  }

  private int compareByPrio(StatBuff<T> b1, StatBuff<T> b2) {
    if (Float.compare(b1.priority, b2.priority) != 0) {
      return Float.compare(b1.expiry, b2.expiry);
    }
    return Long.compare(b1.id, b2.id);
  }

  @Override
  public BuffAggregator<T> makeAggregator() {
    return new Aggregator();
  }

  private class Aggregator implements BuffAggregator<T> {

    TreeSet<StatBuff<T>> buffsByExpiration = new TreeSet<>(StatBuff.this::compareByExpiry);
    TreeSet<StatBuff<T>> buffsByPriority = new TreeSet<>(StatBuff.this::compareByPrio);

    @Override
    public void add(Buff<T> b, T target) {
      assert b instanceof StatBuff<T>;
      buffsByExpiration.add((StatBuff<T>) b);
      buffsByPriority.add((StatBuff<T>) b);
      update(target);
    }

    @Override
    public void tick(T target) {
      boolean mustUpdate = false;
      int tick = Game.get().getTicks();
      for (Iterator<StatBuff<T>> iterator = buffsByExpiration.iterator(); iterator.hasNext(); ) {
        StatBuff<T> buff = iterator.next();
        if (buff.expiry > tick) {
          break;
        }
        iterator.remove();
        buffsByPriority.remove(buff);
        mustUpdate = true;
      }
      if (mustUpdate) {
        update(target);
      }
    }

    @Override
    public void delete(T target) {
      buffsByExpiration.clear();
      buffsByPriority.clear();
      target.clearStats();
    }

    private void update(T target) {
      target.clearStats();
      for (var buff : buffsByPriority) {
        buff.mod.modify(target);
      }
      target.onStatsUpdate();
    }
  }
}
