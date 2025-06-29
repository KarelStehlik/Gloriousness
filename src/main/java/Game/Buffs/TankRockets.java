package Game.Buffs;

import Game.Projectile;
import windowStuff.Graphics;
import windowStuff.ImageData;

public class TankRockets implements Modifier<Projectile> {

  private final Explosive<Projectile> explosive = new Explosive<>(10, 150);
  private static final ImageData image = Graphics.getImage("Bomb-0");

  @Override
  public void mod(Projectile target) {
    target.getSprite().setImage(image);
    target.addBeforeDeath(explosive);
  }
}
