package Game.Mobs;


import Game.Common.Buffs.Buff.DelayedTrigger;
import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Misc.TdWorld;
import java.util.List;

public class MultiMoabCore extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Lead::new, Lead::new,
      Lead::new, Lead::new);


  public MultiMoabCore(TdWorld world, int wave) {
    super(world, "SmCore", wave);
    initComponents();
  }

  public MultiMoabCore(TdMob parent) {
    super(parent.world, "SmCore", parent, parent.getChildrenSpread());
    initComponents();
  }

  private void initComponents() {
    TdMob b = new MultiMoabBridge(world, waveNum);
    b.movement = new HardFollow<>(this, sprite.getWidth() + b.sprite.getWidth(), 0);
    world.addEnemy(b);

    TdMob tail = new MultiMoabTail(world, waveNum);
    tail.movement = new HardFollow<>(this, -sprite.getWidth() - tail.sprite.getWidth(), 0);
    world.addEnemy(tail);
    tail.addBuff(new DelayedTrigger<TdMob>(
        Float.POSITIVE_INFINITY,
        m -> this.addBuff(new StatBuff<TdMob>(Type.INCREASED, Stats.speed, -.3f)),
        true, false
    ));

    TdMob uf = new MultiMoabUpperFin(world, waveNum);
    uf.movement = new HardFollow<>(this, -sprite.getWidth(),
        sprite.getHeight() + uf.sprite.getHeight());
    world.addEnemy(uf);
    uf.addBuff(new DelayedTrigger<TdMob>(
        Float.POSITIVE_INFINITY,
        m -> this.addBuff(new StatBuff<TdMob>(Type.INCREASED, Stats.speed, -.3f)),
        true, false
    ));

    TdMob lf = new MultiMoabLowerFin(world, waveNum);
    lf.movement = new HardFollow<>(this, -sprite.getWidth(),
        -sprite.getHeight() - lf.sprite.getHeight());
    world.addEnemy(lf);
    lf.addBuff(new DelayedTrigger<TdMob>(
        Float.POSITIVE_INFINITY,
        m -> this.addBuff(new StatBuff<TdMob>(Type.INCREASED, Stats.speed, -.3f)),
        true, false
    ));
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 100f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 13500f;
    stats[Stats.value] = 200f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
  }
  // end of generated stats

  @Override
  public boolean isMoab() {
    return true;
  }


  @Override
  protected List<ChildSpawner> children() {
    return spawns;
  }

  @Override
  protected int getChildrenSpread() {
    return 150;
  }
}
