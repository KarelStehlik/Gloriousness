package Game.Buffs;

@FunctionalInterface
public interface Modifier<T> {

  void mod(T target);
}
