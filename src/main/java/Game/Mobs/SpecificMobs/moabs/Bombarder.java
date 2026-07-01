package Game.Mobs.SpecificMobs.moabs;

import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.TdWorld;

import java.util.List;

public class Bombarder extends TdMob {
    private static final List<TdMob.ChildSpawner> spawns = List.of(BlueMoab::new, BlueMoab::new,
            OrkShip::new, OrkShip::new);

    public Bombarder(TdWorld world, int wave) {
        super(world, wave);
    }

    public Bombarder(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "Bombarder");
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 650.0f;
    stats[Stats.speed] = 1.2f;
    stats[Stats.health] = 1500f;
    stats[Stats.damageTaken] = 0.7f;
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
