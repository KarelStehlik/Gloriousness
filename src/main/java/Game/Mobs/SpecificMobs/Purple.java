package Game.Mobs.SpecificMobs;

import Game.Mobs.SpecificMobs.basicaf.Pink;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class Purple extends TdMob {

    private static final List<ChildSpawner> spawns = List.of(Pink::new);

    public Purple(TdWorld world, int wave) {
        super(world, "BloonPurple", wave);
    }

    public Purple(TdMob parent) {
        super(parent.world, "BloonPurple", parent, parent.getChildrenSpread());
    }

    // generated stats
    @Override
    public void clearStats() {
        stats[Stats.size] = 44.0f;
        stats[Stats.speed] = 4.5f;
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
        return 1;
    }
}
