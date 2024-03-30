package Game;

import general.RefFloat;

public class BasicMob extends TdMob {


  public BasicMob(World world) {
    super(world, "Basic", "Golem");
  }

  public BasicMob(TdMob parent, int spread) {
    super(parent.world, "Basic", "Golem",parent, spread);
  }
  @Override
  public void onDeath(){
    world.addEnemy(new BasicMob(this,50));
  }

  // generated stats
  public final float[] extraStats = new float[0];

  @Override
  public void clearStats(){
    stats[Stats.size] = 20f;
    stats[Stats.speed] = 5f;
    stats[Stats.health] = 100f;
    stats[Stats.value] = 1f;
  }
  // end of generated stats
}
