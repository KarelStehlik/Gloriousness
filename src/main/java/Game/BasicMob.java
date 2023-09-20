package Game;

public class BasicMob extends TdMob {

  public BasicMob(World world) {
    super(world, "Basic", "Golem", new Stats());
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
  } // end of generated stats
}
