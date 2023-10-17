package Game;

public class BasicMob extends TdMob {

  public final ExtraStats extraStats = new ExtraStats();


  public BasicMob(World world) {
    super(world, "Basic", "Golem", new Stats());
  }

  // generated stats
  public static final class ExtraStats {


    public void init() {
      
    }

    public ExtraStats() {init();}
  }

  public static final class Stats extends BaseStats {
    @Override
    public Stats() {
      init();
    }
  }
    public void init() {
      size=20.0f;
      speed=5f;
      health=100f;
      value=1f;
    }
  // end of generated stats
}
