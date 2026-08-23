package Game.Common.Buffs.Buff;

import Game.Misc.GameObject;
import java.util.HashMap;
import java.util.Map;

public class BuffHandler<T extends GameObject> {

  private final T target;
  public final Map<Class<? extends Buff>, BuffAggregator<T>> buffTypes = new HashMap<>(1);

  public BuffHandler(T target) {
    this.target = target;
  }

  public BuffHandler<T> addAll(BuffHandler<T> buffHandler, T target) {
    for (var key : buffTypes.keySet()) {
      BuffAggregator<T> existing=buffHandler.buffTypes.get(key);
      buffHandler.buffTypes.put(key, buffTypes.get(key).copyForChild(existing,target));
    }
    return buffHandler;
  }

  public boolean add(Buff<T> newBuff) {
    var aggr = buffTypes.computeIfAbsent(newBuff.getClass(), Id -> newBuff.makeAggregator());
    return aggr.add(newBuff, target);
  }
  public void remove(Buff<T> newBuff) {
    var aggr = buffTypes.computeIfAbsent(newBuff.getClass(), Id -> newBuff.makeAggregator());
    aggr.remove(newBuff, target);
  }

  public BuffAggregator<T> find(Class<?> type) {
    return buffTypes.get(type);
  }

  public void tick() {
    for (var buffs : buffTypes.values()) {
      buffs.tick(target);
    }
  }

  public void delete() {
    var onDeath = find(DelayedTrigger.class);
    if (onDeath != null) {
      onDeath.delete(target);
    }
    for (var buffs : buffTypes.values()) {
      buffs.delete(target);
    }
  }
}
