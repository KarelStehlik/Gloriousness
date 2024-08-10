package Game.Mobs;

import Game.DamageType;
import Game.World;
import java.util.List;

public class Lead extends TdMob {

  private static final List<ChildSpawner> spawns = List.of(Black::new, Black::new);

  public Lead(World world, int wave) {
    super(world, "BloonLead", wave);
  }


  public Lead(TdMob parent) {
    super(parent.world, "BloonLead", parent, parent.getChildrenSpread());
  }

  @Override
  public void takeDamage(float amount, DamageType type) {
    double resistance = stats[Stats.damageTaken];
    amount -= 1;
    amount = Math.max(0, amount);
    double eDamage = amount * resistance / stats[Stats.health];
    healthPart -= eDamage;
    handleDeath();
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 80.0f;
    stats[Stats.speed] = 2f;
    stats[Stats.health] = 10f;
    stats[Stats.value] = 1f;
    stats[Stats.damageTaken] = 1f;
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
  protected int getChildrenSpread() {
    return 50;
  }
}
