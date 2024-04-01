package Game.Mobs;

import Game.World;
import java.util.List;

public class Pink extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Yellow::new);

  public Pink(World world) {
    super(world, "BloonPink");
  }

  public Pink(TdMob parent) {
    super(parent.world, "BloonPink", parent, 50);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 7f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
  }
  // end of generated stats

  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 1;
  }
}
