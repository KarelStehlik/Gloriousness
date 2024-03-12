package Game.Turrets;


import Game.Buffs.DelayedTrigger;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.UniqueBuff;
import Game.BulletLauncher;
import Game.Projectile;
import Game.World;
import general.Data;
import general.RefFloat;
import general.Util;
import java.util.ArrayList;
import java.util.List;

public class EatingTurret extends Turret {

  static final int EatImmuneTag = Util.getUid();
  public final ExtraStats extraStats = new ExtraStats();

  public EatingTurret(World world, int X, int Y) {
    super(world, X, Y, "Button",
        new BulletLauncher(world, "Shockwave"),
        new Stats());
    onStatsUpdate();
    bulletLauncher.setSpread(45);
    bulletLauncher.setProjectileModifier(this::modProjectile);
  }

  private void modProjectile(Projectile p) {
    eater e = new eater((int) baseStats.pierce.get(), baseStats.power.get());
    p.addProjectileCollide((p1, p2) -> e.eat(p2));
    p.addBuff(new UniqueBuff<>(EatImmuneTag, p1 -> {
    }));
    p.addBuff(new OnTickBuff<Projectile>(Float.POSITIVE_INFINITY, Projectile::bounce));
    p.addBuff(new DelayedTrigger<Projectile>(Float.POSITIVE_INFINITY,
        p1 -> e.perish(p1.getX(), p1.getY()), true));
  }

  private static class eater {

    final List<Projectile> eaten;
    final float powerMult;

    eater(int maxEat, float powerMult) {
      eaten = new ArrayList<>(maxEat);
      this.powerMult = powerMult;
    }

    public boolean eat(Projectile other) {
      if (!other.addBuff(new UniqueBuff<>(EatImmuneTag, p1 -> {
      }))) {
        return false;
      }
      eaten.add(other);
      other.setActive(false);
      other.setRotation(Data.gameMechanicsRng.nextFloat() * 360);
      other.addBuff(
          new StatBuff<Projectile>(Type.MORE, Float.POSITIVE_INFINITY, other.power, powerMult));
      return true;
    }

    public void perish(float x, float y) {
      for (var p : eaten) {
        p.move(x, y);
        p.setActive(true);
      }
    }
  }

  // generated stats
  public static final class ExtraStats {

    public ExtraStats() {
      init();
    }

    public void init() {

    }
  }

  public static final class Stats extends BaseStats {

    public Stats() {
      init();
    }

    @Override
    public void init() {
      power = new RefFloat(2);
      range = new RefFloat(500);
      pierce = new RefFloat(100);
      cd = new RefFloat(1000);
      projectileDuration = new RefFloat(5);
      bulletSize = new RefFloat(200);
      speed = new RefFloat(3.5);
    }
  }
  // end of generated stats
}
