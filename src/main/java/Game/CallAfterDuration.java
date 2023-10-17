package Game;

import general.Constants;
import java.lang.reflect.Method;

public class CallAfterDuration implements TickDetect{
  private final Callable event;
  private float duration;
  public CallAfterDuration(Callable c, float durationMillis){
    duration=durationMillis;
    event=c;
  }

  @FunctionalInterface
  public interface Callable {
    void call();
  }

  @Override
  public void onGameTick(int tick) {
    duration-= Game.tickIntervalMillis;
    if(duration<=0){
      event.call();
    }
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean WasDeleted() {
    return duration<=0;
  }
}
