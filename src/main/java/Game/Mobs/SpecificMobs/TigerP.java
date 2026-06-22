package Game.Mobs.SpecificMobs;

import Game.Mobs.SpecificMobs.basicaf.Pink;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class TigerP extends TdMob {

    private static final List<ChildSpawner> spawns = List.of(Pink::new);

    public TigerP(TdWorld world, int wave) {
        super(world, "TigerP", wave);
    }

    public TigerP(TdMob parent) {
        super(parent.world, "TigerP", parent, parent.getChildrenSpread());
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 80.0f;
    stats[Stats.speed] = 7f;
    stats[Stats.health] = 2f;
    stats[Stats.value] = 1f;
    stats[Stats.damageTaken] = 1f;
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
        return 40;
    }
}
