package Game.Mobs;

import Game.World;
import java.util.List;

public class SmallMoab extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(Ceramic::new, Ceramic::new,
      Ceramic::new, Ceramic::new,
      Ceramic::new, Ceramic::new, Ceramic::new, Ceramic::new);

  public SmallMoab(World world, int wave) {
    super(world, "BloonSmallMoab", wave);
    sprite.setSize(getStats()[Stats.size] * 1.1f, getStats()[Stats.size] * 0.8f);
    sprite.setLayer(2);
  }

  public SmallMoab(TdMob parent) {
    super(parent.world, "BloonSmallMoab", parent, parent.getChildrenSpread());
    sprite.setSize(getStats()[Stats.size] * 1.1f, getStats()[Stats.size] * 0.8f);
    sprite.setLayer(2);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300.0f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 100f;
    stats[Stats.value] = 200f;
    stats[Stats.damageTaken] = 1f;
  }
  // end of generated stats

  @Override
  public boolean isMoab() {
    return true;
  }


  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 150;
  }
}
