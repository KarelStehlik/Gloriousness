package Game.Buffs;

import Game.Game;
import Game.GameObject;
import java.util.Collection;
import java.util.TreeSet;

public class Tag<T extends GameObject> implements Buff<T> {

  private final long id;
  protected final float expiryTime;
  private final Modifier<T> mod;
  private boolean isTest = false;

  public Tag(long id) {
    this(id, t -> {
    }, Float.POSITIVE_INFINITY);
  }

  public Tag(long id, float durMillis) {
    this(id, t -> {
    }, durMillis);
  }

  public Tag(long id, Modifier<T> effect) {
    this(id, effect, Float.POSITIVE_INFINITY);
  }

  public Tag(long id, Modifier<T> effect, float durMillis) {
    expiryTime = Game.get().getTicks() + durMillis / Game.tickIntervalMillis;
    this.id = id;
    mod = effect;
  }

  public static <T extends GameObject> Tag<T> Test(long id) {
    var t = new Tag<T>(id);
    t.isTest = true;
    return t;
  }

  private record AppliedTag(long id, float expiryTime) implements Comparable<AppliedTag> {

    @Override
    public int compareTo(AppliedTag o) {
      if (expiryTime != o.expiryTime) {
        return Float.compare(expiryTime, o.expiryTime);
      }
      return Long.compare(id, o.id);
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof AppliedTag && compareTo((AppliedTag) obj) == 0;
    }
  }

  @Override
  public BuffAggregator<T> makeAggregator() {
    return new Aggregator();
  }

  private class Aggregator implements BuffAggregator<T> {

    private final Collection<AppliedTag> alreadyApplied = new TreeSet<>();

    Aggregator(Aggregator og) {
      alreadyApplied.addAll(og.alreadyApplied);
    }

    Aggregator() {
    }

    @Override
    public boolean add(Buff<T> b, T target) {
      assert b instanceof Tag<T>;
      Tag<T> buff = (Tag<T>) b;
      AppliedTag buffTag = new AppliedTag(buff.id, buff.expiryTime);
      if (alreadyApplied.contains(buffTag)) {
        return false;
      }
      if (!buff.isTest) {
        alreadyApplied.add(buffTag);
        buff.mod.mod(target);
      }
      return true;
    }

    @Override
    public void tick(T target) {
      float time = Game.get().getTicks();
      for (var iterator = alreadyApplied.iterator(); iterator.hasNext(); ) {
        AppliedTag ig = iterator.next();
        if (ig.expiryTime > time) {
          break;
        }
        iterator.remove();
      }
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
