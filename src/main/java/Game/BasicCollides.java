package Game;

import Game.Buffs.Ignite;
import Game.Buffs.StatBuff;

public class BasicCollides {

  public static final Projectile.OnCollideComponent<TdMob> damage = (proj, mob) -> mob.takeDamage(
      proj.getPower(), DamageType.PHYSICAL);
  public static final Projectile.OnCollideComponent<TdMob> fire = (proj, mob) ->
      mob.addBuff(new Ignite<TdMob>(2, 2000));
  public static final Projectile.OnCollideComponent<TdMob> slow = (proj, target) ->
      target.addBuff(new StatBuff<TdMob>(0, 2000, mob -> mob.baseStats.speed *= 0.995f));
  private static World _world;
  public static final Projectile.OnCollideComponent<TdMob> explode = (proj, target) -> {
    _world.aoeDamage((int) proj.x, (int) proj.y, (int) proj.getPower(), proj.getPower(),
        DamageType.TRUE);
    _world.explosionVisual(proj.x, proj.y, proj.getPower(), false, "Explosion1-0");
  };

  static void init(World world) {
    _world = world;
  }
}
