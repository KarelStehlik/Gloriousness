package Game.Mobs;


import Game.World;
import general.Util;
import java.util.ArrayList;
import java.util.List;

public class MultiMoabLowerFin extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Lead::new, Lead::new,
      Lead::new, Lead::new);

  public MultiMoabLowerFin(World world, int wave) {
    super(world, "SmDownFin", wave);
  }

  public MultiMoabLowerFin(TdMob parent) {
    super(parent.world, "SmDownFin", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 400f;
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
