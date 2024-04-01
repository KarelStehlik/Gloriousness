package Game.Mobs;

import Game.World;
import java.util.List;

public class Ceramic extends TdMob {

  public Ceramic(World world) {
    super(world,  "BloonCeramic");
  }

  public Ceramic(TdMob parent) {
    super(parent.world,  "BloonCeramic", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 34.0f;
    stats[Stats.speed] = 3f;
    stats[Stats.health] = 10f;
    stats[Stats.value] = 1f;
  }
  // end of generated stats

  private static final List<TdMob.ChildSpawner> spawns = List.of(Lead::new, Lead::new);
  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 60;
  }
}
