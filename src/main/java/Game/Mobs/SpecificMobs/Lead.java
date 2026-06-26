package Game.Mobs.SpecificMobs;

import Game.Enums.DamageType;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class Lead extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Black::new, Black::new);

    public Lead(TdWorld world, int wave) {
        super(world, wave);
    }

    public Lead(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "platebloon");
    }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 109.0f;
    stats[Stats.speed] = 2f;
    stats[Stats.health] = 10f;
    stats[Stats.damageTaken] = 0.4f;
    stats[Stats.value] = 1f;
    stats[Stats.spawns] = 1f;
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
  public int getChildrenSpread() {
    return 50;
  }
}
