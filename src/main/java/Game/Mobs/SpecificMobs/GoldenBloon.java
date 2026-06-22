package Game.Mobs.SpecificMobs;

import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Misc.CallAfterDuration;
import Game.Enums.DamageType;
import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;
import GlobalUse.Constants;
import GlobalUse.Util;
import java.util.List;
import windowStuff.GraphicsOnly.Text.SimpleText;
import windowStuff.GraphicsOnly.Text.TextModifiers;

public class GoldenBloon extends TdMob {
  private float originalValue=0;
  private void gainMoney(long amount, float duration) {
    world.addIncome(amount);
    var t = new SimpleText(TextModifiers.green + "+" + amount, "Calibri", 500, (int) x,
        (int) y, 60, 50, world.getBs());
    t.move((int) Util.clamp(t.getX(), 50, Constants.screenSize.x - 50),
        (int) Util.clamp(t.getY(), 30, Constants.screenSize.y - 30));
    Game.get().addTickable(new CallAfterDuration(t::delete, duration));
  }

  private static final List<ChildSpawner> spawns = List.of();

  public GoldenBloon(TdWorld world, int wave) {
    super(world, "Buff", wave);
  }
  public GoldenBloon(TdWorld world, int wave,float incomePerDamage,float baseValue) {
    super(world, "Buff", wave);
    this.originalValue=baseValue;
    addBuff(new StatBuff<TdMob>(StatBuff.Type.ADDED, TdMob.Stats.value,
            baseValue));
    addBuff(new StatBuff<TdMob>(Type.FINALLY_ADDED, ExtraStats.moneyPerDamage,
             incomePerDamage- stats[ExtraStats.moneyPerDamage]));
  }
  public GoldenBloon(TdMob parent) {
    super(parent.world, "Buff", parent, 50);
  }

  @Override
  public void takeDamage(float amount, DamageType type) {
    super.takeDamage(amount, type);
    addBuff(new StatBuff<TdMob>(Type.ADDED, Stats.value,
         stats[ExtraStats.moneyPerDamage] * amount));
  }
  @Override
  public void addProgress(int addProgress){
    return;
  }

  @Override
  public void onDeath() {
    float approxValue=(long) stats[Stats.value];
    long finalValue;
    if(originalValue!=0){
      float valueGained=approxValue/originalValue;
      finalValue=(long)(originalValue*Math.pow(valueGained,0.4));
    }else{
      finalValue=(long)approxValue;
    }
    gainMoney(finalValue, 5000);
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
    stats[Stats.value] = 0f;
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
  public int getChildrenSpread() {
    return 1;
  }
}
