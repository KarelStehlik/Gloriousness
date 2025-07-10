package Game.Mobs;


import Game.TdWorld;
import java.util.List;

public class MultiMoabUpperFin extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Lead::new, Lead::new,
      Lead::new, Lead::new);

  public MultiMoabUpperFin(TdWorld world, int wave) {
    super(world, "SmUpFin", wave);
  }

  public MultiMoabUpperFin(TdMob parent) {
    super(parent.world, "SmUpFin", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300f;
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
  protected int getChildrenSpread() {
    return 150;
  }
}
