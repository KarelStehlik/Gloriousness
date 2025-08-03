package Game.Buffs;

import Game.GameObject;

public interface Buff<T> {

  BuffAggregator<T> makeAggregator();
}
