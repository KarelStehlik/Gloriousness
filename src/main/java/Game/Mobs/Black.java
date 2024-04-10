package Game.Mobs;

import Game.World;
import java.util.List;

public class Black extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Pink::new, Pink::new);

  public Black(World world) {
    super(world, "BloonBlack");
  }

  public Black(TdMob parent) {
    super(parent.world, "BloonBlack", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 44.0f;
    stats[Stats.speed] = 4.5f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
    stats[Stats.damageTaken] = 1f;
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
    return 40;
  }
}
