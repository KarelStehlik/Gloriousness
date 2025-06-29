package Game.Buffs;

import Game.BulletLauncher;
import Game.Projectile;
import org.joml.Vector2f;

public class SideWeapon implements AttackEffect {

  private final float displacement_x, displacement_y;
  private final boolean relative;// positive X,Y goes to the left upper corner if facing up

  public SideWeapon(Vector2f displacement) {
    this(displacement, true);
  }

  public SideWeapon(Vector2f displacement, boolean displaceRelative) {
    this(displacement.x, displacement.y, displaceRelative);
  }

  public SideWeapon(float x, float y){
    this(x, y, true);
  }

  public SideWeapon(float x, float y, boolean relative){
    displacement_x = x;
    displacement_y = y;
    this.relative = relative;
  }

  @Override
  public void mod(BulletLauncher target, boolean cooldown, float angle) {
    if (cooldown) {
      Projectile p = target.attack(angle, false);
      if (!relative) {
        p.moveRelative(displacement_x, displacement_y);
      } else {
        float sin = (float) Math.sin(2 * Math.PI * angle / 360);
        float cos = (float) Math.cos(2 * Math.PI * angle / 360);
        float displaceX = displacement_x * sin + displacement_y * cos;
        float displaceY = displacement_y * sin - displacement_x * cos;

        p.moveRelative(displaceX
            , displaceY);
      }
    }
  }
}
