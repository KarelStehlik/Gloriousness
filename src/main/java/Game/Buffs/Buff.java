package Game.Buffs;

public interface Buff<T> {

  BuffAggregator<T> makeAggregator();
}
