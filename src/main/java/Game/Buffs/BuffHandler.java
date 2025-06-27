package Game.Buffs;

import Game.GameObject;
import java.util.HashMap;
import java.util.Map;

public class BuffHandler<T extends GameObject> {

  private final T target;
  private final Map<Class<? extends Buff>, BuffAggregator<T>> buffTypes = new HashMap<>(1);

  public BuffHandler(T target) {
    this.target = target;
  }

  public BuffHandler<T> copyForChild(T newTarget) {
    var copy = new BuffHandler<>(newTarget);
    for (var kvp : buffTypes.entrySet()) {
      copy.buffTypes.put(kvp.getKey(), kvp.getValue().copyForChild(newTarget));
    }
    return copy;
  }

  public boolean add(Buff<T> newBuff) {
    var aggr = buffTypes.computeIfAbsent(newBuff.getClass(), Id -> newBuff.makeAggregator());
    return aggr.add(newBuff, target);
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
