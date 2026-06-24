package Game.Mobs.SpecificMobs;

import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Mobs.SpecificMobs.basicaf.Blue;
import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

import java.util.List;

public class ShieldBloon extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Blue::new);

    public ShieldBloon(TdWorld world, int wave) {
    super(world, wave);
  }

  public ShieldBloon(TdMob parent) {
    super(parent);
  }

  @Override
  public void init(){
    createImage( "BloonShield");
    sprite.setLayer(3);
  }

  @Override
  protected void miscTickActions(int tick) {
    int frequency = 20;
    if ((tick + id) % frequency == 0) {
      float f = frequency - .5f;
      world.getMobsGrid().callForEachCircle(x, y, stats[Stats.size] / 2,
          m -> m.addBuff(new StatBuff<TdMob>(
              Type.INCREASED, f * Game.tickIntervalMillis, Stats.health, 1))
      );
    }
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 400.0f;
    stats[Stats.speed] = 1f;
    stats[Stats.health] = 60f;
    stats[Stats.value] = 100f;
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
