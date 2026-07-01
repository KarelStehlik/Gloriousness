package Game.Mobs.SpecificMobs.moabs;

import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.TdWorld;

import java.util.List;

public class BlueMoab extends TdMob {
    private static final List<TdMob.ChildSpawner> spawns = List.of(Capsule::new, Capsule::new,
            Capsule::new, Capsule::new);

    public BlueMoab(TdWorld world, int wave) {
        super(world, wave);
    }

    public BlueMoab(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "Blnboss");
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 325.0f;
    stats[Stats.speed] = 1.5f;
    stats[Stats.health] = 350f;
    stats[Stats.value] = 100f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
    stats[Stats.maxHealth] = 1f;
  }
  // end of generated stats

    @Override
    public boolean isMoab() {
        return true;
    }


    @Override
    protected List<TdMob.ChildSpawner> children() {
        return spawns;
    }

    @Override
    public int getChildrenSpread() {
        return 150;
    }
}
