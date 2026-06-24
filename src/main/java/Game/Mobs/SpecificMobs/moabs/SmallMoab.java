package Game.Mobs.SpecificMobs.moabs;

import Game.Mobs.SpecificMobs.Ceramic;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class SmallMoab extends TdMob {

  private static final List<TdMob.ChildSpawner> spawns = List.of(Ceramic::new, Ceramic::new,
      Ceramic::new, Ceramic::new,
      Ceramic::new, Ceramic::new, Ceramic::new, Ceramic::new);

    public SmallMoab(TdWorld world, int wave) {
        super(world, wave);
    }

    public SmallMoab(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "BloonSmallMoab");
    }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 300.0f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 400f;
    stats[Stats.value] = 200f;
    stats[Stats.damageTaken] = 1f;
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
