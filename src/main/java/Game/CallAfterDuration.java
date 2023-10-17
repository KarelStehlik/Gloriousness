package Game;

public class CallAfterDuration implements TickDetect {

  private final Callable event;
  private float duration;

  public CallAfterDuration(Callable c, float durationMillis) {
    duration = durationMillis;
    event = c;
  }

  @Override
  public void onGameTick(int tick) {
    duration -= Game.tickIntervalMillis;
    if (duration <= 0) {
      event.call();
    }
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean WasDeleted() {
    return duration <= 0;
  }

  @FunctionalInterface
  public interface Callable {

    void call();
  }
}
