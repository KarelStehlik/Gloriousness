package Game.Mobs.SpecificMobs.moabs;

import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class Moab extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(SmallMoab::new, SmallMoab::new,
      SmallMoab::new, SmallMoab::new);

    public Moab(TdWorld world, int wave) {
        super(world, wave);
    }

    public Moab(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "BloonMoab");
    }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300.0f;
    stats[Stats.speed] = 5f;
    stats[Stats.health] = 1200f;
    stats[Stats.damageTaken] = 0.7f;
    stats[Stats.value] = 100f;
    stats[Stats.spawns] = 1f;
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
  public int getChildrenSpread() {
    return 150;
  }
}
