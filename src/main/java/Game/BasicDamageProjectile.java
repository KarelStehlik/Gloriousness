package Game;

public class BasicDamageProjectile extends Projectile {

  protected BasicDamageProjectile(World world, String image, float X, float Y, float speed,
      float rotation, int W, int H, int pierce, float size, float duration, float damage) {
    super(world, image, X, Y, speed, rotation, W, H, pierce, size, duration);
    addMobCollide(e -> e.takeDamage(damage, DamageType.PHYSICAL));
  }
}
