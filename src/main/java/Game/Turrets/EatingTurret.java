package Game.Turrets;


import Game.Buffs.DelayedTrigger;
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


  private void modProjectile(Projectile p) {
    eater e = new eater((int) stats[Stats.pierce], stats[Stats.power]);
    p.addProjectileCollide((p1, p2) -> e.eat(p2));
    p.addBuff(new Tag<>(EatImmuneTag, p1 -> {
    }));
    p.addBuff(new OnTickBuff<Projectile>(Projectile::bounce));
    p.addBuff(new DelayedTrigger<Projectile>(
        p1 -> e.perish(p1.getX(), p1.getY()), true));
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 3f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 1000f;
    stats[Stats.aspd] = 1f;
    stats[Stats.projectileDuration] = 10f;
    stats[Stats.bulletSize] = 220f;
    stats[Stats.speed] = 3.5f;
    stats[Stats.cost] = 1000f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats

  private static class eater {

    final List<Projectile> eaten;
    final float powerMult;

    eater(int maxEat, float powerMult) {
      eaten = new ArrayList<>(maxEat);
      this.powerMult = powerMult;
    }

    public boolean eat(Projectile other) {
      if (!other.addBuff(new Tag<>(EatImmuneTag, p1 -> {
      }))) {
        return false;
      }
      eaten.add(other);
      other.setActive(false);
      other.setRotation(Data.gameMechanicsRng.nextFloat() * 360);
      other.addBuff(
          new StatBuff<Projectile>(Type.MORE, Projectile.Stats.power,
              powerMult));
      other.addBuff(new OnTickBuff<Projectile>(Projectile::bounce));
      return true;
    }

    public void perish(float x, float y) {
      for (var p : eaten) {
        p.move(x, y);
        p.setActive(true);
      }
    }
  }
}
