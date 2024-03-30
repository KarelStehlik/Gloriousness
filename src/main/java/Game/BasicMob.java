package Game;

import general.RefFloat;

public class BasicMob extends TdMob {

  public final ExtraStats extraStats = new ExtraStats();


  public BasicMob(World world) {
    super(world, "Basic", "Golem", new Stats());
  }

  public BasicMob(TdMob parent, int spread) {
    super(parent.world, "Basic", "Golem", new Stats(),parent, spread);
  }
  @Override
  public void onDeath(){
    world.addEnemy(new BasicMob(this,50));
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
      size = new RefFloat(20.0);
      speed = new RefFloat(5);
      health = new RefFloat(100);
      value = new RefFloat(1);
    }
  }
  // end of generated stats
}
