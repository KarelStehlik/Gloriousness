package Game.Mobs.SpecificMobs.moabs;

import Game.Mobs.MobClasses.TdMob;
import Game.Mobs.SpecificMobs.BigBlue;
import Game.WorldStuff.TdWorld;

import java.util.List;

public class BigBalloon extends TdMob {
    private static final List<ChildSpawner> spawns = List.of(BigBlue::new);

    public BigBalloon(TdWorld world, int wave) {
        super(world, wave);
    }

    public BigBalloon(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage("bigballoon");
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 330.0f;
    stats[Stats.speed] = 1f;
    stats[Stats.health] = 2000f;
    stats[Stats.damageTaken] = 2f;
    stats[Stats.value] = 100f;
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

