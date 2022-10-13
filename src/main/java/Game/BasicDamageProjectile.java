package Game;

import java.util.Collection;
import java.util.LinkedList;

public class BasicDamageProjectile extends Projectile {

  private final Collection<Mob> alreadyHit = new LinkedList<Mob>();
  private final float damage;
  private boolean playerHit = false;

  protected BasicDamageProjectile(World world, String image, float X, float Y, float speed,
      float rotation, int W, int H, int pierce, float size, float duration, boolean enemies,
      boolean players, float damage) {
    super(world, image, X, Y, speed, rotation, W, H, pierce, size, duration, enemies, players,
        false);
    this.damage = damage;
  }

  @Override
  protected void collide(Player e) {
    if (!playerHit) {
      playerHit = true;
      e.takeDamage(damage, DamageType.PHYSICAL);
      changePierce(-1);
    }
  }

  @Override
  protected void collide(Mob e) {
    if (!alreadyHit.contains(e)) {
      alreadyHit.add(e);
      e.takeDamage(damage, DamageType.PHYSICAL);
      changePierce(-1);
    }
  }
}
