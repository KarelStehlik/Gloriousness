package Game.Common.Buffs.Modifier;

@FunctionalInterface
public interface Modifier<T> {

  void mod(T target);
}
