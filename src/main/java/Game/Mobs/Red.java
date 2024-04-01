package Game.Mobs;

import Game.World;
import java.util.List;

public class Red extends TdMob {

  public Red(World world) {
    super(world,  "BloonRed");
  }

  public Red(TdMob parent) {
    super(parent.world,"BloonRed", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 30.0f;
    stats[Stats.speed] = 0.8f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
  }
  // end of generated stats

  private static final List<ChildSpawner> spawns = List.of();
  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 1;
  }
}
