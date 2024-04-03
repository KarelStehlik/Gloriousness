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

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 78.0f;
    stats[Stats.speed] = 3.8f;
    stats[Stats.health] = 10f;
    stats[Stats.value] = 0f;
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
    return 60;
  }
}
