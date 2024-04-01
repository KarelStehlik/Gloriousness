package Game.Mobs;

import Game.World;
import java.util.List;

public class Lead extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Black::new, Black::new);

  public Lead(World world) {
    super(world, "BloonLead");
  }


  public Lead(TdMob parent) {
    super(parent.world, "BloonLead", parent, parent.getChildrenSpread());
  }
  // end of generated stats

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 1f;
    stats[Stats.health] = 2f;
    stats[Stats.value] = 1f;
  }

  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 50;
  }
}
