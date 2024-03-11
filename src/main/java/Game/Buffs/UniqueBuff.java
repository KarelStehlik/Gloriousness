package Game.Buffs;

import Game.GameObject;
import java.util.Collection;
import java.util.HashSet;

public class UniqueBuff <T extends GameObject> implements Buff<T>{

  private final int id;
  private final Modifier<T> mod;
  public UniqueBuff(int id, Modifier<T> effect){
    this.id=id;
    mod=effect;
  }

  @Override
  public BuffAggregator<T> makeAggregator() {
    return new Aggregator();
  }

  private class Aggregator implements BuffAggregator<T>{
    private final Collection<Integer> alreadyApplied=new HashSet<>(1);

    @Override
    public void add(Buff<T> b, T target) {
      assert b instanceof UniqueBuff<T>;
      UniqueBuff<T> buff = (UniqueBuff<T>)b;
      if(!alreadyApplied.contains(buff.id)){
        alreadyApplied.add(buff.id);
        buff.mod.mod(target);
      }
    }

    @Override
    public void tick(T target) {

    }

    @Override
    public void delete(T target) {
      alreadyApplied.clear();
    }
  }
}
