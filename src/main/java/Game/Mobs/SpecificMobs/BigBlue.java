package Game.Mobs.SpecificMobs;

import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.TdWorld;

import java.util.List;

public class BigBlue extends TdMob {

    private static final List<ChildSpawner> spawns = List.of();

    public BigBlue(TdWorld world, int wave) {
        super(world, wave);
    }

    public BigBlue(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "bigblue");
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 75.0f;
    stats[Stats.speed] = 1.5f;
    stats[Stats.health] = 200f;
    stats[Stats.damageTaken] = 2f;
    stats[Stats.value] = 100f;
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
        return 0;
    }
}
