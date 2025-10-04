package Game.Common.Buffs.Buff;

public interface Buff<T> {

  BuffAggregator<T> makeAggregator();
}
