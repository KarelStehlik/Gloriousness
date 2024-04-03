package Game.Buffs;

import Game.GameObject;
import java.util.Collection;
import java.util.HashSet;

public class UniqueBuff<T extends GameObject> implements Buff<T> {

  private final long id;
  private final Modifier<T> mod;
  private boolean isTest = false;

  public UniqueBuff(long id, Modifier<T> effect) {
    this.id = id;
    mod = effect;
  }

  public static <T extends GameObject> UniqueBuff<T> Test(long id) {
    var t = new UniqueBuff<T>(id, tar -> {
    });
    t.isTest = true;
    return t;
  }

  @Override
  public BuffAggregator<T> makeAggregator() {
    return new Aggregator();
  }

  private class Aggregator implements BuffAggregator<T> {

    private final Collection<Long> alreadyApplied = new HashSet<>(1);

    Aggregator(Aggregator og) {
      alreadyApplied.addAll(og.alreadyApplied);
    }

    Aggregator() {
    }

    @Override
    public boolean add(Buff<T> b, T target) {
      assert b instanceof UniqueBuff<T>;
      UniqueBuff<T> buff = (UniqueBuff<T>) b;
      if (alreadyApplied.contains(buff.id)) {
        return false;
      }
      if (!buff.isTest) {
        alreadyApplied.add(buff.id);
        buff.mod.mod(target);
      }
      return true;
    }

    @Override
    public void tick(T target) {

    }

    @Override
    public void delete(T target) {
      alreadyApplied.clear();
    }

    @Override
    public BuffAggregator<T> copyForChild(T newTarget) {
      return new Aggregator(this);
    }
  }
}
