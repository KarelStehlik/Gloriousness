package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.Explosive;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Mobs.TdMob;
import Game.Projectile;
import Game.TdWorld;
import Game.TurretGenerator;
import general.Data;
import general.Description;
import general.Util;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Graphics;
import windowStuff.ImageData;

public class Wizard extends Turret {

  @Override
  protected ImageData getImageUpdate(){
    return Graphics.getImage("wizard");
  }

  private final List<BulletLauncher> spells = new ArrayList<>(1);

  public Wizard(TdWorld world, int X, int Y) {
    super(world, X, Y, new BulletLauncher(world, "skull"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    spells.add(bulletLauncher);
  }

  @Override
  public void setRotation(float f) {
    rotation = f % 360;
  }

  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    for (var spell : spells) {
      spell.tickCooldown();
      spell.move(x, y);
    }
    TdMob target = target();
    if (target != null) {
      setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
      for (var spell : spells) {
        while (spell.canAttack()) {
          spell.attack(rotation);
        }
      }
    }

    buffHandler.tick();
  }

  @Override
  public void onStatsUpdate() {
    if (spells == null) {
      super.onStatsUpdate();
      return;
    }
    for (var spell : spells) {
      spell.updateStats(stats);
    }
    rangeDisplay.setSize(stats[Stats.range] * 2, stats[Stats.range] * 2);
    extraStatsUpdate();
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, "wizard", "Wizard", () -> new Wizard(world, -1000, -1000));
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("Fireball-0", new Description("fireball"),
        () -> {
          Explosive<Projectile> explosive = new Explosive<>(2, 100);
          BulletLauncher fireballs = new BulletLauncher(world, "Fireball-0") {
            @Override
            public void updateStats(float[] stats) {
              setDuration(stats[Turret.Stats.projectileDuration] * 0.5f);
              setPierce(0);
              setPower(0);
              explosive.damage = stats[Stats.pierce];
              setSize(stats[Turret.Stats.bulletSize] * 1.8f);
              explosive.setRadius((int) (stats[Turret.Stats.bulletSize] * 5f));
              setSpeed(stats[Turret.Stats.speed] * 2);
              setCooldown(1000f / stats[Turret.Stats.aspd] * 3);
            }
          };
          fireballs.addProjectileModifier(
              p -> p.getSprite().playAnimation(
                  p.getSprite().new BasicAnimation(
                      "Fireball", p.getStats()[Projectile.Stats.duration]
                  )
              )
          );
          fireballs.addMobCollide(BasicCollides.damage);
          fireballs.updateStats(stats);
          fireballs.addProjectileModifier(p -> p.addBeforeDeath(explosive));
          spells.add(fireballs);
        }, 1000);
  }


  @Override
  protected Upgrade up001() {
    return new Upgrade("DoubleDart", new Description("shoots 1.5x faster."),
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 1.5f)), 100);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("bluray", new Description("lightning"),
        () -> {
          BulletLauncher lightning = new BulletLauncher(world, "bluray") {
            @Override
            public void updateStats(float[] stats) {
              setPierce((int) (stats[Stats.range] / 20));
              setPower(stats[Stats.power] * stats[Stats.bulletSize]);
              setSize(8);
              setSpeed(stats[Stats.range]);
              setCooldown(1000f / stats[Stats.speed] * 100);
              setDuration(0.1f);
            }
          };
          lightning.setLauncher(Lightning::new);
          lightning.addMobCollide(BasicCollides.damage);
          lightning.updateStats(stats);
          lightning.setAspectRatio(5);
          spells.add(lightning);
        }, 1000);
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 2f;
    stats[Stats.range] = Data.gameMechanicsRng.nextFloat(250f, 500f);
    stats[Stats.pierce] = Data.gameMechanicsRng.nextFloat(2f, 4f);
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.8f, 1.5f);
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(20f, 100f);
    stats[Stats.speed] = 15f;
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
