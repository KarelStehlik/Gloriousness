package Game;

public class BasicMob extends TdMob {

  public final ExtraStats extraStats = new ExtraStats();


  public BasicMob(World world) {
    super(world, "Basic", "Golem", new Stats());
  }

  // generated stats
  public static final class ExtraStats {

    public ExtraStats() {
      init();
    }

    public void init() {

    }
  }

  public static final class Stats extends BaseStats {

    public Stats() {
      init();
    }

    @Override
    public void init() {
      size = 20.0f;
      speed = 5f;
      health = 100f;
      value = 1f;
    }
  }
  // end of generated stats
}
