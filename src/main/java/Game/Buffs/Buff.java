package Game.Buffs;

import Game.GameObject;

public interface Buff<T extends GameObject> {

  BuffAggregator<T> makeAggregator();
}
