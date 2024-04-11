package Game.Turrets;


import Game.Buffs.DelayedTrigger;
import Game.Buffs.Modifier;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.Tag;
import Game.BulletLauncher;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import general.Data;
import general.Util;
import java.util.ArrayList;
import java.util.List;

public class EatingTurret extends Turret {

  public static final String image = "EatingTower";
  static final long EatImmuneTag = Util.getUid();

  public EatingTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Shockwave"));
    onStatsUpdate();
    bulletLauncher.setSpread(45);
    bulletLauncher.addProjectileModifier(this::modProjectile);
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Eating", () -> new EatingTurret(world, -1000, -1000));
  }


  @Override
  protected Upgrade up100() {
    return new Upgrade("Meteor",
        () -> "2x pierce. Eaten projectiles get 2 additional power if full.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.pierce, 2));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.fullEatBuff, 2));
        }, 500);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Meteor",
        () -> "50x more pierce. Eaten projectiles get 10 additional power if full.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 50));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.fullEatBuff, 10));
        }, 1500);
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("Meteor", () -> "projectiles last longer",
        () -> {
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.projectileDuration, 2));
        }, 200);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Meteor",
        () -> "eaten projectiles have 4s additional duration and +4 pierce",
        () -> {
          eatenMods.add(proj -> proj.addBuff(
              new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.duration, 4)));
          eatenMods.add(proj -> proj.addBuff(
              new StatBuff<Projectile>(Type.ADDED, Projectile.Stats.pierce, 4)));
        }, 2000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Meteor", () -> "eaten projectiles gain 1 additional power",
        () -> {
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.power, 1f));
        }, 200);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Meteor", () -> "eaten projectiles have 20% more power",
        () -> {
          eatenMods.add(proj -> proj.addBuff(
              new StatBuff<Projectile>(Type.MORE, Projectile.Stats.power, 1.2f)));
        }, 3000);
  }


  private void modProjectile(Projectile p) {
    eater e = new eater((int) stats[Stats.pierce]);
    p.addProjectileCollide((p1, p2) -> e.eat(p2));
    p.addBuff(new Tag<Projectile>(EatImmuneTag, p1 -> {
    }));
    p.addBuff(new OnTickBuff<Projectile>(Projectile::bounce));
    p.addBuff(new DelayedTrigger<Projectile>(
        p1 -> e.perish(p1.getX(), p1.getY(), p1.getStats()[Projectile.Stats.pierce] <= 0), true));
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 11;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 8f;
    stats[Stats.aspd] = 1f;
    stats[Stats.projectileDuration] = 4f;
    stats[Stats.bulletSize] = 220f;
    stats[Stats.speed] = 3.5f;
    stats[Stats.cost] = 50f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.fullEatBuff] = 2f;
  }

  public static final class ExtraStats {

    public static final int fullEatBuff = 10;

    private ExtraStats() {
    }
  }
  // end of generated stats

  private final List<Modifier<Projectile>> eatenMods = new ArrayList<>(1);

  private class eater {

    final List<Projectile> eaten;

    eater(int maxEat) {
      eaten = new ArrayList<>(Math.min(maxEat, 1000));
    }

    public boolean eat(Projectile other) {
      if (!other.addBuff(new Tag<Projectile>(EatImmuneTag, p1 -> {
      }))) {
        return false;
      }
      eaten.add(other);
      other.setActive(false);
      other.setRotation(Data.gameMechanicsRng.nextFloat() * 360);
      other.addBuff(
          new StatBuff<Projectile>(Type.FINALLY_ADDED, Projectile.Stats.power,
              stats[Stats.power]));
      other.addBuff(new OnTickBuff<Projectile>(Projectile::bounce));
      for (var mod : eatenMods) {
        mod.mod(other);
      }
      return true;
    }

    public void perish(float x, float y, boolean full) {
      for (var p : eaten) {
        p.move(x, y);
        p.setActive(true);
        if (full) {
          p.addBuff(new StatBuff<Projectile>(Type.FINALLY_ADDED, Projectile.Stats.power,
              stats[ExtraStats.fullEatBuff]));
        }
      }
    }
  }
}
