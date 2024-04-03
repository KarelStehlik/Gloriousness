package Game.Mobs;

import Game.World;
import java.util.List;

public class Green extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Blue::new);

  public Green(World world) {
    super(world, "BloonGreen");
  }

  public Green(TdMob parent) {
    super(parent.world, "BloonGreen", parent, 50);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 68.0f;
    stats[Stats.speed] = 2.5f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
  }

  // end of generated stats
  @Override
  public boolean isMoab() {
    return false;
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
