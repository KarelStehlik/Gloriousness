package GlobalUse;

public class Log {

  public static void write(Object o) {
    System.out.println(o);
  }

  public static void conditional(Object o, boolean c) {
    if (c) {
      write(o);
    }
  }

  public static class Timer {

    public long saved = 0;
    private long timer = 0;

    public long elapsedNano(boolean resetTimer) {
      long dt = System.nanoTime() - timer;
      if (resetTimer) {
        timer = System.nanoTime();
      }
      return dt;
    }

    public int elapsed(boolean resetTimer) {
      return (int) (elapsedNano(resetTimer) / 1000000);
    }
  }
}
