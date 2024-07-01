package Game.Mobs;

import Game.World;
import java.util.List;

public class Ceramic extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(Lead::new, Lead::new);

  public Ceramic(World world, int wave) {
    super(world, "BloonCeramic", wave);
  }

  public Ceramic(TdMob parent) {
    super(parent.world, "BloonCeramic", parent, parent.getChildrenSpread());
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 78.0f;
    stats[Stats.speed] = 5.8f;
    stats[Stats.health] = 20f;
    stats[Stats.value] = 0f;
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
    return 60;
  }
}
