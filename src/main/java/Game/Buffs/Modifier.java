package Game.Buffs;

import Game.GameObject;

@FunctionalInterface
public interface Modifier<T> {

  void mod(T target);
}
