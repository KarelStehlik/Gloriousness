package Game.Mobs;


import Game.World;
import general.Util;
import java.util.ArrayList;
import java.util.List;

public class MultiMoabBridge extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Lead::new, Lead::new,
      Lead::new, Lead::new);


  public MultiMoabBridge(World world, int wave) {
    super(world, "SmBridge", wave);
    initComponents();
  }

  public MultiMoabBridge(TdMob parent) {
    super(parent.world, "SmBridge", parent, parent.getChildrenSpread());
    initComponents();
}

  private void initComponents() {
    TdMob b = new MultiMoabHead(world, waveNum);
    b.movement = new HardFollow<>(this, sprite.getWidth() + b.sprite.getWidth(), 0);
    world.addEnemy(b);
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 200f;
    stats[Stats.speed] = 1.3f;
    stats[Stats.health] = 400f;
    stats[Stats.value] = 200f;
    stats[Stats.damageTaken] = 1f;
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
