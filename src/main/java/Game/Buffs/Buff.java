package Game.Buffs;

import Game.Game;
import Game.GameObject;

public class Buff<T extends GameObject> implements Comparable<Buff<T>> {

  public static final int INFINITE_DURATION = Integer.MIN_VALUE + 1;
  public static final int TRIGGER_ON_UPDATE = 0;
  public static final int TRIGGER_ON_TICK = 1;
  public static final int TRIGGER_ON_REMOVE = 2;
  public static final int POSSIBLE_TRIGGERS_COUNT = 3;
  public static final Modifier<GameObject> doNothing = target -> {
  };
  public final int priority;
  public final int triggerEvent;
  private final Modifier<T> mod;
  private int remainingDuration;
  private boolean hasEnded = false;

  public Buff(int priority, int durationMillis, int triggerEvent, Modifier<T> effect) {
    this.priority = priority;
    remainingDuration = durationMillis;
    this.triggerEvent = triggerEvent;
    mod = effect;
  }

  public int getRemainingDuration() {
    return remainingDuration;
  }

  public void tick() {
    remainingDuration -= Game.tickIntervalMillis;
    hasEnded |= remainingDuration <= 0;
  }

  public void remove(T target) {
    hasEnded = true;
  }

  public void trigger(T target) {
    mod.modify(target);
  }

  public boolean hasEnded() {
    return hasEnded;
  }

  @Override
  public int compareTo(Buff o) {
    return priority - o.priority;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Buff && compareTo((Buff) o) == 0;
  }


  @FunctionalInterface
  public interface Modifier<T> {

    void modify(T target);
  }
}