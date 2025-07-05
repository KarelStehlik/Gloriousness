package Game.Mobs;

import Game.TdWorld;
import java.util.List;

public class Blue extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Red::new);

  public Blue(TdWorld world, int wave) {
    super(world, "BloonBlue", wave);
  }

  public Blue(TdMob parent) {
    super(parent.world, "BloonBlue", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 60.0f;
    stats[Stats.speed] = 3.2f;
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
