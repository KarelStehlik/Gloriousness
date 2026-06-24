package Game.Mobs.SpecificMobs;

import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class Ceramic extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(Lead::new, Lead::new);

    public Ceramic(TdWorld world, int wave) {
        super(world, wave);
    }

    public Ceramic(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "blackrockbloon");
    }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 85.0f;
    stats[Stats.speed] = 5.8f;
    stats[Stats.health] = 30f;
    stats[Stats.value] = 0f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
  }
  // end of generated stats

  @Override
  public boolean isMoab() {
    return false;
  }


  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  public int getChildrenSpread() {
    return 60;
  }
}
