package Game.Mobs.SpecificMobs.moabs;


import Game.Mobs.SpecificMobs.Lead;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class MultiMoabTail extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Lead::new, Lead::new,
      Lead::new, Lead::new);

    public MultiMoabTail(TdWorld world, int wave) {
        super(world, wave);
    }

    public MultiMoabTail(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "SmTail");
    }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 175f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 10000f;
    stats[Stats.damageTaken] = 0.7f;
    stats[Stats.value] = 200f;
    stats[Stats.spawns] = 1f;
    stats[Stats.maxHealth] = 1f;
  }
  // end of generated stats

  @Override
  public boolean isMoab() {
    return true;
  }


  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  public int getChildrenSpread() {
    return 150;
  }
}
