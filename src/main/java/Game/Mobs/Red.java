package Game.Mobs;

import Game.World;
import java.util.List;

public class Red extends TdMob {

  private static final List<ChildSpawner> spawns = List.of();

  public Red(World world, int wave) {
    super(world, "BloonRed", wave);
  }

  public Red(TdMob parent) {
    super(parent.world, "BloonRed", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 50.0f;
    stats[Stats.speed] = 3f;
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
    return 1;
  }
}
