package Game.Mobs;

import Game.TdWorld;
import java.util.List;

public class Pink extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Yellow::new);

  public Pink(TdWorld world, int wave) {
    super(world, "BloonPink", wave);
  }

  public Pink(TdMob parent) {
    super(parent.world, "BloonPink", parent, 50);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 76.0f;
    stats[Stats.speed] = 8f;
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
