package Game.Buffs;

import Game.GameObject;

@FunctionalInterface
public interface Modifier<T extends GameObject> {

  void mod(T target);
}
