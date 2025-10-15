package Game.Mobs.SpecificMobs;

import Game.Misc.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class Green extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Blue::new);

  public Green(TdWorld world, int wave) {
    super(world, "BloonGreen", wave);
  }

  public Green(TdMob parent) {
    super(parent.world, "BloonGreen", parent, 50);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 68.0f;
    stats[Stats.speed] = 3.7f;
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
  public int getChildrenSpread() {
    return 1;
  }
}
