package Game.Mobs.SpecificMobs.moabs;

import Game.Mobs.MobClasses.TdMob;
import Game.Mobs.SpecificMobs.Ceramic;
import Game.Mobs.SpecificMobs.Purple;
import Game.Mobs.SpecificMobs.TigerG;
import Game.Mobs.SpecificMobs.TigerP;
import Game.WorldStuff.TdWorld;

import java.util.ArrayList;
import java.util.List;

public class Purpcart extends TdMob {
    private static final int spawnCount=5;
    private static final List<ChildSpawner> spawns = getSpawns();

    public Purpcart(TdWorld world, int wave) {
        super(world, wave);
    }

    public Purpcart(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage("purpcart");
        //default moab is at 25
        sprite.setLayer(22);
    }

    private static List<ChildSpawner> getSpawns() {
        List<ChildSpawner> spawn=new ArrayList<>(spawnCount);
        for (int i = 0; i < spawnCount; i++) {
            spawn.add(Purple::new);
        }
        return spawn;
    }


    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 200.0f;
    stats[Stats.speed] = 1.5f;
    stats[Stats.health] = 20f;
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
    protected List<TdMob.ChildSpawner> children() {
        return spawns;
    }

    @Override
    public int getChildrenSpread() {
        return 150;
    }
}
