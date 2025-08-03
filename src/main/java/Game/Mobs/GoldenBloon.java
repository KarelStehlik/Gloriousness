package Game.Mobs;

import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.CallAfterDuration;
import Game.Enums.DamageType;
import Game.Game;
import Game.TdWorld;
import general.Constants;
import general.Util;
import java.util.List;
import windowStuff.SimpleText;

public class GoldenBloon extends TdMob {

  private void gainMoney(long amount, float duration) {
    if (WasDeleted()) {
      return;
    }
    world.setMoney(world.getMoney() + amount);
    var t = new SimpleText("+" + amount, "Calibri", 500, (int) x,
        (int) y, 60, 50, world.getBs());
    t.move((int) Util.clamp(t.getX(), 50, Constants.screenSize.x - 50),
        (int) Util.clamp(t.getY(), 30, Constants.screenSize.y - 30));
    t.setColors(Util.getColors(1.5f, 1.5f, 0));
    Game.get().addTickable(new CallAfterDuration(t::delete, duration));
  }

  private static final List<ChildSpawner> spawns = List.of();

  public GoldenBloon(TdWorld world, int wave) {
    super(world, "Buff", wave);
  }

  public GoldenBloon(TdMob parent) {
    super(parent.world, "Buff", parent, 50);
  }

  @Override
  public void takeDamage(float amount, DamageType type) {
    super.takeDamage(amount, type);
    addBuff(new StatBuff<TdMob>(Type.ADDED, Stats.value,
        (long) stats[ExtraStats.moneyPerDamage] * amount));
  }

  @Override
  public void onDeath() {
    gainMoney((long) stats[Stats.value], 5000);
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 7;
  }

  @Override
  public void clearStats() {
    stats[Stats.size] = 68.0f;
    stats[Stats.speed] = 7f;
    stats[Stats.health] = 200000f;
    stats[Stats.value] = 1f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
    stats[ExtraStats.moneyPerDamage] = 1f;
  }

  public static final class ExtraStats {

    public static final int moneyPerDamage = 6;

    private ExtraStats() {
    }
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
    return 1;
  }
}
