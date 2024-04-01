package Game.Mobs;

import Game.World;
import java.util.List;

public class Black extends TdMob {

  public Black(World world) {
    super(world, "BloonBlack");
  }

  public Black(TdMob parent) {
    super(parent.world, "BloonBlack", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 3f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
  }
  // end of generated stats

  private static final List<ChildSpawner> spawns = List.of(Pink::new, Pink::new);
  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 40;
  }
}
