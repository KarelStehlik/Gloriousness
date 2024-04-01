package Game.Mobs;

import Game.World;
import java.util.List;

public class Lead extends TdMob {

  public Lead(World world) {
    super(world,  "BloonLead");
  }

  public Lead(TdMob parent) {
    super(parent.world, "BloonLead", parent, parent.getChildrenSpread());
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 0.15f;
    stats[Stats.health] = 2f;
    stats[Stats.value] = 1f;
  }
  // end of generated stats

  private static final List<ChildSpawner> spawns = List.of(Black::new, Black::new);
  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 50;
  }
}
