package Game.Turrets;

import Game.Animation;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.DamageType;
import Game.Game;
import Game.Projectile;
import Game.TickDetect;
import Game.TurretGenerator;
import Game.World;
import general.RefFloat;
import general.Util;
import java.util.List;
import org.joml.Vector2f;
import windowStuff.Sprite;

public class Druid extends Turret {

  public static final String image = "ggg";

  public Druid(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Egg"));
    onStatsUpdate();
    bulletLauncher.setSpread(45);
    bulletLauncher.setProjectileModifier(this::modProjectile);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Druid", () -> new Druid(world, -1000, -1000));
  }

  private void modProjectile(Projectile p) {
    p.addBuff(new OnTickBuff<Projectile>(Float.POSITIVE_INFINITY, Projectile::bounce));

    final RefFloat triggers = new RefFloat(stats[ExtraStats.respawns]);

    p.addMobCollide((proj, mob) -> {
      mob.takeDamage(proj.getPower(), DamageType.TRUE);

      if (proj.getStats()[Projectile.Stats.pierce] == 2 && triggers.get() > 0) {
        triggers.add(-1);
        proj.setActive(false);
        proj.addBuff(
            new StatBuff<Projectile>(Type.MORE, Float.POSITIVE_INFINITY, Projectile.Stats.power,
                1.5f));
        proj.addBuff(
            new StatBuff<Projectile>(Type.INCREASED, Float.POSITIVE_INFINITY, Projectile.Stats.size,
                0.5f));
        proj.addBuff(
            new StatBuff<Projectile>(Type.ADDED, Float.POSITIVE_INFINITY, Projectile.Stats.pierce,
                3f));
        proj.clearCollisions();
        Game.get().addTickable(new RespawningProjectile(proj));
      }
      return true;
    });

  }

  @Override
  protected List<Upgrade> getUpgradePath1() {
    return List.of();
  }

  @Override
  protected List<Upgrade> getUpgradePath2() {
    return List.of();
  }

  @Override
  protected List<Upgrade> getUpgradePath3() {
    return List.of();
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 11;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 3f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 5f;
    stats[Stats.cd] = 1000f;
    stats[Stats.projectileDuration] = 8f;
    stats[Stats.bulletSize] = 220f;
    stats[Stats.speed] = 3.5f;
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.respawns] = 5f;
  }

  public static final class ExtraStats {

    public static final int respawns = 10;

    private ExtraStats() {
    }
  }
  // end of generated stats

  class RespawningProjectile implements TickDetect {

    Animation sprite;
    Projectile p;

    RespawningProjectile(Projectile proj) {
      p = proj;
      float size = proj.getStats()[Projectile.Stats.size];

      this.sprite = new Animation(
          new Sprite("Egg", proj.getX(), proj.getY(), size * .8f, size * .8f, 3, "colorCycle2")
              .addToBs(Game.get().getSpriteBatching("main"))
              .setOpacity(0.0f)
              .setRotation(proj.getRotation() - 90).
              setColors(Util.getCycle2colors(1f)), 1
      ).setLinearScaling(new Vector2f(size * .01f, size * .01f)).setOpacityScaling(0.015f);
    }

    @Override
    public void onGameTick(int tick) {
      sprite.onGameTick(tick);
      if (sprite.WasDeleted()) {
        delete();
      }
    }

    @Override
    public void delete() {
      p.setActive(true);
    }

    @Override
    public boolean WasDeleted() {
      return sprite.WasDeleted();
    }
  }
}
