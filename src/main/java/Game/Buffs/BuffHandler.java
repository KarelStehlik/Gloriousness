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

  public void add(Buff<T> newBuff) {
    var aggr = buffTypes.computeIfAbsent(newBuff.getClass(), Id -> newBuff.makeAggregator());
    aggr.add(newBuff, target);
  }

  public void tick() {
    for (var buffs : buffTypes.values()) {
      buffs.tick(target);
    }
  }

  public void delete() {
    for (var buffs : buffTypes.values()) {
      buffs.delete(target);
    }
  }
}
