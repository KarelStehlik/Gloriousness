package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.*;
import Game.BulletLauncher;
import Game.Enums.DamageType;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Projectile;
import Game.TdWorld;
import Game.TurretGenerator;
import general.Data;
import general.Description;
import general.Util;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Engineer8 extends Turret {


  public static final String image = "engineer";
  private final BulletLauncher turretLauncher;

  private final List<Modifier<EngiTurret8>> turretMods = new ArrayList<>(1);
  public Engineer8(TdWorld world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "spanner"));
      onStatsUpdate();
      turretLauncher = new BulletLauncher(world, "nail");
      bulletLauncher.addMobCollide(BasicCollides.damage);
      bulletLauncher.setRemainingCooldown(Float.MAX_VALUE);
  }

    public static TurretGenerator generator(TdWorld world) {
        return new TurretGenerator(world, image, "engineer", () -> new Engineer8(world, -1000, -1000));
    }

  private float turretPlaceTimer = 0;

  @Override
  public void onGameTick(int tick) {
      if (notYetPlaced) {
          return;
      }
      bulletLauncher.tickCooldown();
      if(bulletLauncher.canAttack()) {
          TdMob target = target();
          if (target != null) {
              float enemyRotat=Util.get_rotation(target.getX() - x, target.getY() - y);
              while (bulletLauncher.canAttack()) {
                  bulletLauncher.attack(enemyRotat);
              }
          }
      }
      turretPlaceTimer += Game.tickIntervalMillis * stats[Stats.aspd] * stats[Engineer8.ExtraStats.spawnSpd];
      while (turretPlaceTimer >= 1000) {
          turretPlaceTimer -= 1000;
          float dist = (float) Math.sqrt(
                  Data.gameMechanicsRng.nextDouble(Util.square(stats[Stats.range])));
          float angle = Data.gameMechanicsRng.nextFloat(360);
          EngiTurret8 t = new EngiTurret8(world,
                  (int) (x + dist * Util.cos(angle)),
                  (int) (y + dist * Util.sin(angle)),
                  turretLauncher);
          t.place();
          turretMods.forEach(m -> m.mod(t));
      }
      buffHandler.tick();
  }

    @Override
    protected Upgrade up010() {
        return new Upgrade("turretmenu",  new Description( "turrets have an additional firing slit to shoot two projectiles at once"),
                () -> {
                    turretMods.add(t -> {
                        t.bulletLauncher.cannons.add(new BulletLauncher.Cannon(5,5));
                    });
                }, 50);
    }
    protected Upgrade up001() {
        return new Upgrade("spannermen",  new Description( "turrets are built faster and occasionally throws le spanner"),
                () -> {
                    bulletLauncher.setRemainingCooldown(0);
                    bulletLauncher.addProjectileModifier(new ProcTrigger<Projectile>(prog ->));
                }, 50);
    }

    // generated stats
  @Override
  public int getStatsCount() {
    return 11;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 250f;
    stats[Stats.pierce] = 2f;
    stats[Stats.aspd] = 0.7f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = 15f;
    stats[Stats.cost] = 75f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.spawnSpd] = 0.6f;
  }

  public static final class ExtraStats {

    public static final int spawnSpd = 10;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
