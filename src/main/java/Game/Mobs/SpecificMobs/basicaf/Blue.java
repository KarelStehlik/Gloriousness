package Game.Mobs.SpecificMobs.basicaf;

import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class Blue extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Red::new);

  public Blue(TdWorld world, int wave) {
    super(world, wave);
  }

  public Blue(TdMob parent) {
    super(parent);
  }

  @Override
  public void init(){
    createImage("BloonBlue");
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 60.0f;
    stats[Stats.speed] = 3.2f;
    stats[Stats.health] = 1f;
    stats[Stats.value] = 1f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
    stats[Stats.maxHealth] = 1f;
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
