package Game.Mobs.SpecificMobs.moabs;

import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.TdWorld;

import java.util.List;

public class OrkShip extends TdMob {
    private static final List<TdMob.ChildSpawner> spawns = List.of(SmallMoab::new, SmallMoab::new,
            SmallMoab::new, SmallMoab::new);

    public OrkShip(TdWorld world, int wave) {
        super(world, wave);
    }

    public OrkShip(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "orkship");
    }

    // generated stats
    @Override
    public void clearStats() {
        stats[TdMob.Stats.size] = 300.0f;
        stats[TdMob.Stats.speed] = 5f;
        stats[TdMob.Stats.health] = 1200f;
        stats[TdMob.Stats.damageTaken] = 0.7f;
        stats[TdMob.Stats.value] = 100f;
        stats[TdMob.Stats.spawns] = 1f;
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
