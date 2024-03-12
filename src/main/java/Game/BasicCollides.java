package Game;

import Game.Buffs.Ignite;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;

public final class BasicCollides {

  public static final Projectile.OnCollideComponent<TdMob> damage = (proj, mob) -> {
    mob.takeDamage(
        proj.getPower(), DamageType.PHYSICAL);
    return true;
  };
  public static final Projectile.OnCollideComponent<TdMob> fire = (proj, mob) ->
  {
    mob.addBuff(new Ignite<TdMob>(proj.getPower() * 0.02f, 2000));
    return true;
  };
  public static final Projectile.OnCollideComponent<TdMob> slow = (proj, target) ->
  {
    target.addBuff(new StatBuff<TdMob>(Type.MORE, 2000, target.baseStats.speed, 0.995f));
    return true;
  };
  private static World _world;
  public static final Projectile.OnCollideComponent<TdMob> explode = (proj, target) -> {
    _world.aoeDamage((int) proj.x, (int) proj.y, (int) proj.getPower(), proj.getPower(),
        DamageType.TRUE);
    _world.explosionVisual(proj.x, proj.y, proj.getPower(), false, "Explosion1-0");
    return true;
  };

  private BasicCollides() {
  }

  static void init(World world) {
    _world = world;
  }
}
