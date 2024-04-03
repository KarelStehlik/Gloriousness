package Game.Mobs;

import Game.World;
import java.util.List;

public class Moab extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(SmallMoab::new, SmallMoab::new,
      SmallMoab::new, SmallMoab::new);

  public Moab(World world) {
    super(world, "BloonMoab");
    sprite.setSize(getStats()[Stats.size] * 1.5f, getStats()[Stats.size] * 0.7f);
    sprite.setLayer(2);
  }

  public Moab(TdMob parent) {
    super(parent.world, "BloonMoab", parent, parent.getChildrenSpread());
    sprite.setSize(getStats()[Stats.size] * 1.5f, getStats()[Stats.size] * 0.7f);
    sprite.setLayer(2);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300.0f;
    stats[Stats.speed] = 10f;
    stats[Stats.health] = 100f;
    stats[Stats.value] = 100f;
  }

  @Override
  public boolean isMoab() {
    return true;
  }
  // end of generated stats

  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 150;
  }
}
