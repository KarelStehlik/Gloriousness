package Game.Mobs;

import Game.World;
import java.util.List;

public class Moab extends TdMob {

  public Moab(World world) {
    super(world,  "BloonMoab");
    sprite.setSize(getStats()[Stats.size]*1.5f, getStats()[Stats.size]*0.7f);
  }

  public Moab(TdMob parent) {
    super(parent.world,  "BloonMoab", parent, parent.getChildrenSpread());
    sprite.setSize(getStats()[Stats.size]*1.5f, getStats()[Stats.size]*0.7f);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300.0f;
    stats[Stats.speed] = 10f;
    stats[Stats.health] = 100f;
    stats[Stats.value] = 100f;
  }
  // end of generated stats

  private static final List<TdMob.ChildSpawner> spawns = List.of(SmallMoab::new, SmallMoab::new,SmallMoab::new, SmallMoab::new,
      SmallMoab::new, SmallMoab::new,SmallMoab::new, SmallMoab::new);
  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 150;
  }
}
