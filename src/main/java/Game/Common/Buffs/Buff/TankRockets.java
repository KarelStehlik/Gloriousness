package Game.Common.Buffs.Buff;

import Game.Common.Buffs.Modifier.Explosive;
import Game.Common.Buffs.Modifier.Modifier;
import Game.Common.Projectile;
import GlobalUse.Data;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;

public class TankRockets implements Modifier<Projectile> {

  private final Explosive explosive = new Explosive(10, 150);
  private static final ImageData image = Graphics.getImage("bomb");
  private int lastFiringTick;
  private boolean rocketsThisTick;
  private final float bombchance;

  public TankRockets(float bombchance) {
    this.bombchance = bombchance;
  }

  @Override
  public void mod(Projectile target) {
    if (target.world.getTick() != lastFiringTick) {
      lastFiringTick = target.world.getTick();
      rocketsThisTick = Data.gameMechanicsRng.nextFloat() < bombchance;
    }
    if (rocketsThisTick) {
      target.getSprite().setImage(image);
      target.addBeforeDeath(explosive);
    }
  }
}
