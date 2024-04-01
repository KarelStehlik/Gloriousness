package Game.Mobs;

import Game.World;
import java.util.List;

public class Yellow extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Green::new);

  public Yellow(World world) {
    super(world, "BloonYellow");
  }

  public Yellow(TdMob parent) {
    super(parent.world, "BloonYellow", parent, 50);
  }
  // end of generated stats

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 5f;
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
