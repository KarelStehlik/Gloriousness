package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Enums.DamageType;
import Game.Game;
import Game.Projectile;
import Game.Projectile.Guided;
import Game.TurretGenerator;
import Game.World;
import general.Data;
import general.Description;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import windowStuff.DragableButton;
import windowStuff.Sprite;

public class Plane extends Turret {

  public static final String image = "BasicTower";

  private final float speed = 10;

  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    bulletLauncher.tickCooldown();
    bulletLauncher.move(x, y);
    while (bulletLauncher.canAttack()) {
      bulletLauncher.attack(rotation);
    }
    if (dropsPineapples) {
      pineappleLauncher.move(x, y);
      pineappleLauncher.tickCooldown();
      while (pineappleLauncher.canAttack()) {
        pineappleLauncher.attack(rotation);
      }
    }
    buffHandler.tick();
    move(x + speed * Util.cos(rotation), y + speed * Util.sin(rotation));
    var next = flyPoints.get(currentFlyPoint);
    turnTowards(next.getSprite().getX(), next.getSprite().getY(), 1.7f);
    if (Util.distanceSquared(x - next.getSprite().getX(), y - next.getSprite().getY()) < 5000) {
      next.getSprite().setColors(Util.getColors(0, 0, 0));
      currentFlyPoint = (currentFlyPoint + 1) % flyPoints.size();
      flyPoints.get(currentFlyPoint).getSprite().setColors(Util.getColors(1, 0, 0.3f));
    }
    for (int i = 0; i < carried.size(); i++) {
      Turret c = carried.get(i);
      if (c.WasDeleted()) {
        carried.remove(i);
        i--;
      } else {
        float offset = 100;
        float angle = 360f * i / carried.size();
        c.move(x + Util.cos(angle) * offset, y + Util.sin(angle) * offset);
      }
    }
  }

  private static final List<Point> defaultFlyPoints = List.of(
      new Point(950, 540),
      new Point(1620, 540),
      new Point(970, 540),
      new Point(300, 540)
  );

  private final List<DragableButton> flyPoints = new ArrayList<>(4);
  private int currentFlyPoint = 0;
  private final BulletLauncher pineappleLauncher = new BulletLauncher(world, "Duck");
  private boolean dropsPineapples = false;

  public Plane(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Dart"));
    onStatsUpdate();
    sprite.setLayer(3);
    bulletLauncher.addMobCollide(BasicCollides.damage);
    pineappleLauncher.addProjectileModifier(p -> p.addBeforeDeath(pineapple -> {
      world.aoeDamage(
          (int) pineapple.getX(), (int) pineapple.getY(), (int) stats[ExtraStats.PineRadius],
          pineapple.getPower(),
          DamageType.TRUE);
      world.explosionVisual((int) pineapple.getX(), (int) pineapple.getY(),
          (int) stats[ExtraStats.PineRadius], false, "Explosion2");
    }));

    for (int i = 0; i < 4; i++) {
      Point loc = defaultFlyPoints.get(i);
      var b = new DragableButton(new Sprite("Button", 100).setPosition(loc.x, loc.y).setSize(50, 50)
          .addToBs(world.getBs()), (x, y) -> {
      });
      Game.get().addMouseDetect(b);
      b.hide();
      flyPoints.add(b);
    }
    bulletLauncher.cannons = BulletLauncher.radial(8);
  }

  @Override
  public void place() {
    super.place();
    move(960, 0);
    setRotation(90);
  }

  @Override
  public void onStatsUpdate() {
    super.onStatsUpdate();
    bulletLauncher.cannons = BulletLauncher.radial((int) stats[ExtraStats.Radial]);
    if (pineappleLauncher != null) {
      pineappleLauncher.setPower(stats[ExtraStats.PinePower] * stats[Stats.power]);
      pineappleLauncher.setCooldown(1000f / (stats[ExtraStats.PineAspd] * stats[Stats.aspd]));
      pineappleLauncher.setDuration(stats[ExtraStats.PineDuration]);
      pineappleLauncher.setSpeed(0);
      pineappleLauncher.setSize(65);
      pineappleLauncher.setSpread(360);
    }
  }

  @Override
  protected void openUpgradeMenu() {
    super.openUpgradeMenu();
    for (var s : flyPoints) {
      s.show();
    }
  }

  @Override
  protected void closeUpgradeMenu() {
    super.closeUpgradeMenu();
    for (var s : flyPoints) {
      s.hide();
    }
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Basic", () -> new Plane(world, -1000, -1000));
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("Duck", new Description("drops pineapples"),
        () -> {
          dropsPineapples = true;
        }, 200);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Button", new Description("more pineapples"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.PineAspd, 3));
        }, 1000);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("FastDart",
        new Description("pineapples have bigger explosions that do more damage"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.PineRadius, 1.5f));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.PinePower, 20));
        }, 5000);
  }

  @Override
  protected Upgrade up040() {
    return new Upgrade("BeefierDart",
        new Description("Pineapples fire darts 10 times. darts do more damage."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 3));
          pineappleLauncher.addProjectileModifier(dart -> {
            dart.addBeforeDeath(p -> {
              bulletLauncher.move(p.getX(), p.getY());

              for (int i = 0; i < 10; i++) {
                bulletLauncher.attack(Data.gameMechanicsRng.nextFloat(0, 360), false);
              }

            });
          });
        }, 20000);
  }

  @Override
  protected Upgrade up050() {
    return new Upgrade("FastDart",
        new Description("pineapples have massive explosions with crazy damage"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.PineRadius, 2f));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.PinePower, 5000));
        }, 50000);
  }

  private static final long dartEatId = Util.getUid();

  private final Guided g = new Projectile.Guided(1000, 3);

  @Override
  protected Upgrade up100() {
    return new Upgrade("Radar",
        new Description("neva miss"),
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new OnTickBuff<Projectile>(g::tick));
          });
        }, 1000);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("MoreRadar", new Description("fires 2x more often."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 2));
        }, 2000);
  }

  @Override
  public void delete() {
    super.delete();
    for (DragableButton b : flyPoints) {
      b.delete();
    }
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("Meteor", new Description("Beefy darts"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.bulletSize, 1.0f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 3f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, .8f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 2.3f));
        }, 5000);
  }

  private final List<Turret> carried = new ArrayList<>();

  @Override
  protected Upgrade up500() {
    return new Upgrade("InfiniDart",
        new Description(
            "Grabs the nearest turret except plane or necromancer. Can be bought repeatedly."),
        () -> {
          Turret best = null;
          float dist = Float.POSITIVE_INFINITY;
          for (Turret t : world.getTurrets()) {
            if (
                !carried.contains(t) &&
                    !(t instanceof Plane || t instanceof Necromancer || t instanceof EngiTurret
                        || t.isNotYetPlaced()) &&
                    Util.distanceSquared(x - t.getX(), y - t.getY()) < dist) {
              best = t;
              dist = Util.distanceSquared(x - t.getX(), y - t.getY());
            }
          }
          if (best != null) {
            carried.add(best);
          }
          path1Tier = 3;
        }, 12000);
  }

  private static final float[] red = Util.getColors(0.9f, 0, 0);

  @Override
  protected Upgrade up400() {
    return new Upgrade("Button", new Description("Enemies hit explode"),
        () -> bulletLauncher.addProjectileModifier(p -> {
          p.getSprite().setColors(red);
          p.addMobCollide((proj, mob) -> {
            world.aoeDamage((int) mob.getX(),
                (int) mob.getY(),
                100,
                proj.getPower() * 6,
                DamageType.TRUE
            );
            world.lesserExplosionVisual((int) mob.getX(),
                (int) mob.getY(),
                100);
            return true;
          });
        }), 20000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("DoubleDart",
        new Description(() -> "",
            () -> "shoots 2x more darts, currently " + (int) stats[ExtraStats.Radial], () -> ""),
        () -> addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.Radial, 2f)), 550);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("TripleDart", new Description("shoots 3x more often."),
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 3f)), 4000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("QuadDart", new Description("shoots 4x more darts."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 4f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.speed, 2f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 0.5f));
        }, 25000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("QuinDart", new Description("2 added damage. shoots 5x more often."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 5f));
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.power, 2f));
          bulletLauncher.setSpread(100);
        }, 80000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("TenDart", new Description("5x damage. shoots 5x more darts."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 5f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 5f));
        }, 960000);
  }

  @Override
  public boolean blocksPlacement() {
    return false;
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 15;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 350f;
    stats[Stats.pierce] = 8f;
    stats[Stats.aspd] = 0.8f;
    stats[Stats.projectileDuration] = 1.1f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 25f;
    stats[Stats.cost] = 700f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.Radial] = 8f;
    stats[ExtraStats.PinePower] = 6f;
    stats[ExtraStats.PineDuration] = 3f;
    stats[ExtraStats.PineRadius] = 200f;
    stats[ExtraStats.PineAspd] = 0.76f;
  }

  public static final class ExtraStats {

    public static final int Radial = 10;
    public static final int PinePower = 11;
    public static final int PineDuration = 12;
    public static final int PineRadius = 13;
    public static final int PineAspd = 14;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
