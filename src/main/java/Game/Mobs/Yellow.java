package Game.Mobs;

import Game.TdWorld;
import java.util.List;

public class Yellow extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Green::new);

  public Yellow(TdWorld world, int wave) {
    super(world, "BloonYellow", wave);
  }

  public Yellow(TdMob parent) {
    super(parent.world, "BloonYellow", parent, 50);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 68.0f;
    stats[Stats.speed] = 7f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
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
