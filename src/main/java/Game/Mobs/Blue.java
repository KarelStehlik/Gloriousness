package Game.Mobs;

import Game.World;
import java.util.List;

public class Blue extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Red::new);

  public Blue(World world) {
    super(world, "BloonBlue");
  }

  public Blue(TdMob parent) {
    super(parent.world, "BloonBlue", parent, parent.getChildrenSpread());
  }
  // end of generated stats

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 2.2f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
  }

  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 1;
  }
}
