package Game.Mobs;


import Game.TdWorld;
import java.util.ArrayList;
import java.util.List;

public class MultiMoabHead extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Lead::new, Lead::new,
      Lead::new, Lead::new);

  private final List<TdMob> components = new ArrayList<>();

  public MultiMoabHead(TdWorld world, int wave) {
    super(world, "SmHead", wave);
  }

  public MultiMoabHead(TdMob parent) {
    super(parent.world, "SmHead", parent, parent.getChildrenSpread());
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 150f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 10000f;
    stats[Stats.value] = 200f;
    stats[Stats.damageTaken] = 1f;
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
