package Game.Mobs.SpecificMobs;


import Game.Misc.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class MultiMoabTail extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Lead::new, Lead::new,
      Lead::new, Lead::new);

  public MultiMoabTail(TdWorld world, int wave) {
    super(world, "SmTail", wave);
  }

  public MultiMoabTail(TdMob parent) {
    super(parent.world, "SmTail", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 175f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 10000f;
    stats[Stats.value] = 200f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
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
