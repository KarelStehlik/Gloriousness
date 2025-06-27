package Game.Mobs;

import Game.World;
import general.Log;
import java.util.List;

public class Moab extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(SmallMoab::new, SmallMoab::new,
      SmallMoab::new, SmallMoab::new);

  public Moab(World world, int wave) {
    super(world, "BloonMoab", wave);
    sprite.setSize(getStats()[Stats.size] * 1.5f, getStats()[Stats.size] * 0.7f);
    sprite.setLayer(2);
  }

  public Moab(TdMob parent) {
    super(parent.world, "BloonMoab", parent, parent.getChildrenSpread());
    sprite.setSize(getStats()[Stats.size] * 1.5f, getStats()[Stats.size] * 0.7f);
    sprite.setLayer(2);
  }

  @Override
  public void onDeath() {
    Log.write(stats[Stats.health]);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300.0f;
    stats[Stats.speed] = 5f;
    stats[Stats.health] = 1200f;
    stats[Stats.value] = 100f;
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
