package Game;

import Game.Buffs.Ignite;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.TdMob.Stats;

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
    target.addBuff(
        new StatBuff<TdMob>(Type.INCREASED, 2000, Stats.speed,
            -proj.stats[Projectile.Stats.power]));
    return true;
  };
  private static World _world;
  public static final Projectile.OnCollideComponent<TdMob> explode = (proj, target) -> explodeFunc(
      proj, proj.getPower());

  private BasicCollides() {
  }

  public static boolean explodeFunc(Projectile proj, float power) {
    _world.aoeDamage((int) proj.x, (int) proj.y, (int) power, power,
        DamageType.TRUE);
    _world.explosionVisual(proj.x, proj.y, power, false, "Explosion1-0");
    return true;
  }

  static void init(World world) {
    _world = world;
  }
}
