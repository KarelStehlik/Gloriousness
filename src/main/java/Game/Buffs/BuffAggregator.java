package Game.Buffs;

import Game.GameObject;

public interface BuffAggregator<T extends GameObject> {

  void add(Buff<T> b, T target);

  void tick(T target);

  void delete(T target);
}
