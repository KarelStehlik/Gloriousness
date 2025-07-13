package Game.Buffs;

import Game.GameObject;
import general.Data;

public class ProcTrigger<T> implements Modifier<T> {//happens sometimes

  private final Modifier<T> mod;
  private final float chance;//between 0 and 1

  public ProcTrigger(Modifier<T> effect, float chance) {
    mod = effect;
    this.chance = chance;
  }

  @Override
  public void mod(T target) {
    if (Data.gameMechanicsRng.nextFloat(0, 1f) <= chance) {
      mod.mod(target);
    }
  }
}
