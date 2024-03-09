package Game;

import Game.Buffs.Buff;
import java.util.Map;
import windowStuff.Sprite;
import windowStuff.Sprite.BasicAnimation;
import windowStuff.SpriteBatching;
import windowStuff.SuperBatch;

public class BasicCollides {
  private static World _world;
  static void init(World world){
    _world=world;
  }
  public static final Projectile.OnCollideComponent<TdMob> damage = (proj, mob) -> mob.takeDamage(
      proj.getPower(), DamageType.PHYSICAL);

  public static final Projectile.OnCollideComponent<TdMob> fire = (proj, mob) -> {
    mob.ignite.add(2,2000);
  };

  public static final Projectile.OnCollideComponent<TdMob> explode = (proj, target) -> {
    _world.aoeDamage((int) proj.x, (int) proj.y, (int) proj.getPower(), proj.getPower(),
        DamageType.TRUE);
    _world.explosionVisual(proj.x, proj.y, proj.getPower(), false, "Explosion1-0");
  };
}
