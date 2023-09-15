package Game.Buffs;

import Game.GameObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BuffHandler<T extends GameObject> {

  private final List<TreeSet<Buff<T>>> buffs = new ArrayList<>(Buff.POSSIBLE_TRIGGERS_COUNT);
  private final List<Buff<T>> buffsWithDuration = new ArrayList<>(5);
  private final T target;
  private boolean deleted = false;

  public BuffHandler(T target) {
    this.target = target;
    for (int i = 0; i < Buff.POSSIBLE_TRIGGERS_COUNT; i++) {
      buffs.add(new TreeSet<>());
    }
  }

  public void updateStats() {
    target.stats.clear();
    target.stats.putAll(target.baseStats);
    for (Iterator<Buff<T>> iterator = buffs.get(Buff.TRIGGER_ON_UPDATE).iterator();
        iterator.hasNext(); ) {
      Buff<T> eff = iterator.next();
      if (eff.hasEnded()) {
        iterator.remove();
      } else {
        eff.trigger(target);
      }
    }
    target.onStatsUpdate();
  }

  public void add(Buff<T> e) {
    buffs.get(e.triggerEvent).add(e);
    if (e.triggerEvent == Buff.TRIGGER_ON_UPDATE) {
      updateStats();
    }
    if (e.getRemainingDuration() != Buff.INFINITE_DURATION) {
      buffsWithDuration.add(e);
    }
  }

  // TODO : optimize this shit
  public void tick() {
    for (Iterator<Buff<T>> iterator = buffs.get(Buff.TRIGGER_ON_TICK).iterator();
        iterator.hasNext() && !deleted; ) {
      Buff<T> eff = iterator.next();
      if (eff.hasEnded()) {
        iterator.remove();
      } else {
        eff.trigger(target);
      }
    }

    boolean mustUpdateStats = false;
    for (Iterator<Buff<T>> iterator = buffsWithDuration.iterator();
        iterator.hasNext() && !deleted; ) {
      Buff<T> eff = iterator.next();
      eff.tick();
      if (eff.hasEnded()) {
        iterator.remove();
        if (eff.triggerEvent == Buff.TRIGGER_ON_REMOVE) {
          eff.trigger(target);
        }
        mustUpdateStats |= eff.triggerEvent == Buff.TRIGGER_ON_UPDATE;
      }
    }
    if (mustUpdateStats && !deleted) {
      updateStats();
    }
  }

  public void delete() {
    for (Set<Buff<T>> category : buffs) {
      category.clear();
    }
    buffs.clear();
    buffsWithDuration.clear();
    deleted = true;
  }
}
