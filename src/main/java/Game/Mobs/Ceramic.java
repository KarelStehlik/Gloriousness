package Game.Mobs;

import Game.World;
import java.util.List;

public class Ceramic extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(Lead::new, Lead::new);

  public Ceramic(World world) {
    super(world, "BloonCeramic");
  }

  public Ceramic(TdMob parent) {
    super(parent.world, "BloonCeramic", parent, parent.getChildrenSpread());
  }
  // end of generated stats

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 3.8f;
    stats[Stats.health] = 10f;
    stats[Stats.value] = 0f;
  }

  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 60;
  }
}
