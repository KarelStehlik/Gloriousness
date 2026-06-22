package Game.Mobs.SpecificMobs;

import Game.Mobs.SpecificMobs.basicaf.Green;
import Game.Mobs.SpecificMobs.basicaf.Pink;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.ArrayList;
import java.util.List;

public class TigerG extends TdMob {
    private static final int spawnCount=6;

    private static final List<ChildSpawner> spawns=new ArrayList<>(spawnCount);;

    public TigerG(TdWorld world, int wave) {
        super(world, "TigerG", wave);
        populateSpawns();
    }

    public TigerG(TdMob parent) {
        super(parent.world, "TigerG", parent, parent.getChildrenSpread());
        populateSpawns();
    }
    private void populateSpawns(){
            spawns.add(Black::new);
            while(spawns.size()<spawnCount)
                spawns.add(Green::new);
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 80.0f;
    stats[Stats.speed] = 6f;
    stats[Stats.health] = 4f;
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
        return 90;
    }
}
