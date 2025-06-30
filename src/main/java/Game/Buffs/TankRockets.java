package Game.Buffs;

import Game.Projectile;
import general.Data;
import windowStuff.Graphics;
import windowStuff.ImageData;

public class TankRockets implements Modifier<Projectile> {

  private final Explosive<Projectile> explosive = new Explosive<>(10, 150);
  private static final ImageData image = Graphics.getImage("Bomb-0");
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
