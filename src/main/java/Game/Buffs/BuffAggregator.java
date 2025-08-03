package Game.Buffs;

import Game.GameObject;

public interface BuffAggregator<T> {

  boolean add(Buff<T> b, T target);

  void tick(T target);

  void delete(T target);

  // to make a new aggregator for child bloons when popped.
  // Spreads stuff like scaling, maybe throws away some other stuff.
  BuffAggregator<T> copyForChild(T newTarget);
}
