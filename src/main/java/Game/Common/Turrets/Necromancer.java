package Game.Common.Turrets;

import Game.Misc.Ability;
import Game.Misc.BasicCollides;
import Game.Common.Buffs.Buff.DelayedTrigger;
import Game.Common.Buffs.Buff.Ignite;
import Game.Common.Buffs.Modifier.Modifier;
import Game.Common.Buffs.Buff.OnTickBuff;
import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Common.Buffs.Buff.Tag;
import Game.Common.BulletLauncher;
import Game.Enums.TargetingOption;
import Game.Misc.Game;
import Game.Mobs.MobClasses.TdMob;
import Game.Mobs.MobClasses.TdMob.MoveAlongTrack;
import Game.Common.Projectile;
import Game.Misc.TdWorld;
import Game.Misc.TdWorld.TrackPoint;
import Game.Misc.TurretGenerator;
import GlobalUse.Data;
import GlobalUse.Description;
import GlobalUse.RefFloat;
import GlobalUse.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.Sprite.Sprite.FrameAnimation;

public class Necromancer extends Turret {

  @Override
  protected ImageData getImageUpdate(){
    return Graphics.getImage("Necromancer");
  }

  private final List<TrackPoint> spawnPoints = new ArrayList<>(1);
  private boolean walking = true;

  private static final class Inheritor {

    private int uses;
    private final Modifier<Necromancer> effect;

    private Inheritor(int uses, Modifier<Necromancer> effect) {
      this.uses = uses;
      this.effect = effect;
    }

    boolean isDepleted() {
      return uses <= 0;
    }

    void apply(Necromancer target) {
      effect.mod(target);
      uses--;
    }

    public int uses() {
      return uses;
    }

    public Modifier<Necromancer> effect() {
      return effect;
    }
  }

  private static final List<Inheritor> inheritors = new ArrayList<>(1);

  public Necromancer(TdWorld world, int X, int Y) {
    super(world, X, Y, new BulletLauncher(world, "Zombie"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.addProjectileModifier(p -> {
      TrackPoint initPoint = spawnPoints.isEmpty() ? new TrackPoint(0, 0, 0)
          : spawnPoints.get(Data.gameMechanicsRng.nextInt(0, spawnPoints.size()));
      Point offset = new Point(Data.gameMechanicsRng.nextInt(-10, 10),
          Data.gameMechanicsRng.nextInt(-10, 10));
      p.move(initPoint.getX() + offset.x, initPoint.getY() + offset.y);
      p.setRotation(Data.unstableRng.nextFloat() * 360);
      p.setMultihit(true);
      if (!walking) {
        return;
      }
      TdMob.MoveAlongTrack<Projectile> mover = new MoveAlongTrack<>(true,
          world.getMapData(), offset, stats, Stats.speed, Projectile::delete,
          Math.max(initPoint.getNode() - 1, 0));
      p.addBuff(new OnTickBuff<Projectile>(mover::tick));
    });
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, "Necromancer", "Necromancer",
        () -> new Necromancer(world, -1000, -1000));
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("ZombieDead",
        new Description("zombies are dead. The don't move but are more powerful."),
        () -> {
          bulletLauncher.setImage(path1Tier >= 2 ? "ZombieDeadPierce" : "ZombieDead");
          walking = false;
          addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.bulletSize, 1));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 5f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 2f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 3f));
        }, 500);
  }

  private boolean rotates = true;

  @Override
  protected Upgrade up020() {
    return new Upgrade("Zombie",
        new Description(
            "zombies spin in their graves. this causes nearby non-MOAB bloons to slow down"),
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            if (rotates) {
              p.addBuff(
                  new OnTickBuff<Projectile>(proj -> proj.setRotation(proj.getRotation() + 5)));
            }
          });
          bulletLauncher.addProjectileModifier(
              p -> p.addBuff(new OnTickBuff<Projectile>(proj -> world.getMobsGrid()
                  .callForEachCircle((int) proj.getX(), (int) proj.getY(), 150, mob -> {
                    if (!mob.isMoab()) {
                      mob.addBuff(
                          new StatBuff<TdMob>(Type.INCREASED, 20, TdMob.Stats.speed, -0.03f));
                    }
                  })))

          );
        }, 1000);
  }

  private static final float respawnChance = .75f;

  @Override
  protected Upgrade up040() {
    return new Upgrade("Zombie",
        new Description("The first bloon that a zombie touches will spawn a new zombie on death."),
        () -> bulletLauncher.addProjectileModifier(p -> {

              RefFloat alreadyHit = new RefFloat(0);
              p.addMobCollide((zombie, bloon) -> {
                if (alreadyHit.get() > 0) {
                  return false;
                }
                alreadyHit.set(1);
                bloon.addBuff(new DelayedTrigger<TdMob>(Float.POSITIVE_INFINITY, mob -> {
                  var newZombie = bulletLauncher.attack(0, false);
                  newZombie.move(mob.getX(), mob.getY());
                  world.lesserExplosionVisual(mob.getX(), mob.getY(), 100)
                      .setColors(Util.getColors(0, 2.5f, 0.5f));
                }, true, false));
                return true;
              }, 0);

            }
        ), 25000);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("Zombie",
        new Description("Zombies use mummification to preserve themselves 5x longer."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 5));
          bulletLauncher.setImage("Mummy");
        }
        , 10000);
  }


  private static final long megaMineId = Util.getUid();

  @Override
  protected Upgrade up050() {
    return new Upgrade("Mine",
        new Description("Cool never-before-seen ability"),
        () -> {
          Ability a = Ability.add("fire", 120000,
              () -> "Probably weaker, smaller dick and lower balls than the one in btd8",
              () -> {
                var ui = Game.get().getUserInputListener();
                float dx = ui.getX() - x;
                float dy = ui.getY() - y;
                float rot = Util.get_rotation(dx, dy);
                bulletLauncher.setImage("Bomb-0");
                rotates = false;
                Projectile mine = bulletLauncher.attack(rot, false);
                rotates = true;
                bulletLauncher.setImage("Mummy");
                mine.move(x, y);
                float dist = (float) Math.sqrt(Util.distanceSquared(dx, dy));
                float speed = 500 / 1000f;
                float vx = Util.cos(rot) * speed * Game.tickIntervalMillis;
                float vy = Util.sin(rot) * speed * Game.tickIntervalMillis;
                mine.addBuff(
                    new OnTickBuff<Projectile>(dist / speed,
                        p -> p.move(p.getX() + vx, p.getY() + vy)));
                mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.size, 5));
                mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.power, 20));
                mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.pierce, 200));
                mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.duration,
                    Float.POSITIVE_INFINITY));
                mine.addBeforeDeath(p -> BasicCollides.explodeFunc((int) p.getX(),
                    (int) p.getY(), p.getPower() * 500, 2000));
                mine.addBuff(new Tag<Projectile>(EatingTurret.EatImmuneTag));
                Sprite ms = mine.getSprite();
                ms.setLayer(50);
                ms.playAnimation(new FrameAnimation("Bomb", .1f).loop());

              }, megaMineId
          );
          addBuff(new DelayedTrigger<Turret>(t -> a.delete(), true));
        }, 150000);
  }

  private boolean ignites = false;

  private void explode(Projectile proj, float power, float radius, String img, int centreX,
      int centreY) {
    BasicCollides.explodeFunc(centreX, centreY, proj.getPower() * power,
        radius, img);
    if (ignites) {
      world.getMobsGrid().callForEachCircle(centreX, centreY,
          (int) (radius * 2.2f), m -> m.addBuff(new Ignite<>(proj.getPower() * power, 3000)));
    }
  }

  private void explode(Projectile proj, float power, float radius, String img) {
    explode(proj, power, radius, img, (int) proj.getX(), (int) proj.getY());
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Bomb-0", new Description("zombies explode when destroyed."),
        () -> bulletLauncher.addProjectileModifier(
            p -> p.addBeforeDeath(proj -> explode(proj, 5, 150, "Explosion1"))), 500);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Duck", new Description("zombies also explode on contact."),
        () -> bulletLauncher.addProjectileModifier(p -> p.addMobCollide((proj, mob) -> {
          explode(proj, 7F, 100F, "Explosion2", (int) mob.getX(), (int) mob.getY());
          return true;
        })), 2000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("Zombie",
        new Description(
            "zombies also explode all the time. this is mostly for show. They get more pierce."),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Turret.Stats.pierce, 2));
          bulletLauncher.addProjectileModifier(p -> p.addBuff(
              new OnTickBuff<Projectile>(proj -> explode(proj, 20, 50, "Explosion2"))));
        }, 12000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("Zombie", new Description("explosions also ignite things"),
        () -> ignites = true, 20000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("Zombie", new Description("constantly ignites everything"),
        () -> addBuff(new OnTickBuff<Turret>(t -> world.getMobsList().forEach(
            mob -> mob.addBuff(new Ignite<>(stats[Stats.power] * stats[Stats.pierce], 3000))))),
        50000);
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("MoreZombies", new Description("produces zombies faster."),
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 1.8f)), 500);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("ZombiePierce", new Description("zombies have more pierce"),
        () -> {
          bulletLauncher.setImage(path2Tier >= 1 ? "ZombieDeadPierce" : "ZombiePierce");
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 2));
        }, 500);
  }

  private static Modifier<Necromancer> getRandomInheritorEffect() {
    return switch (Data.gameMechanicsRng.nextInt(1, 5)) {
      case 1 -> nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.aspd, .5f));
      case 2 -> nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.pierce, .5f));
      case 3 ->
          nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.projectileDuration, .5f));
      case 4 -> nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.power, .5f));
      default -> nec -> {
      };
    };
  }

  public static final int MAX_INHERITORS = 5;

  @Override
  protected Upgrade up300() {
    return new Upgrade("Inheritor",
        new Description("use genetic engineering to buff the next 3 necromancers. "
            + "Each tower can only have " + MAX_INHERITORS + " genetic modifications at once."),
        () -> inheritors.add(new Inheritor(3, getRandomInheritorEffect())), 2000);
  }


  private static final long rangeBuffId = Util.getUid();

  @Override
  protected Upgrade up400() {
    return new Upgrade("Inheritor2",
        new Description("apply 12 more genetic modifications to this (bypassing the normal limit)"),
        () -> {
          for (int i = 0; i < 12; i++) {
            getRandomInheritorEffect().mod(this);
          }
        }, 9000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("Inheritor3",
        new Description(
            "sacrifices nearby towers, gain a genetic modification for every 2500 money sacrificed"),
        () -> {
          float sacced = 0;
          for (Turret t : world.getTurrets()) {
            if (t != this && Util.distanceSquared(t.getX() - x, t.getY() - y) <= Util.square(
                stats[Stats.range])) {
              sacced += t.totalCost;
              t.delete();
            }
          }
          for (; sacced > 2500; sacced -= 2500) {
            getRandomInheritorEffect().mod(this);
          }
          // to reduce lag
          while (stats[Stats.aspd] > 100) {
            addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 0.5f));
            addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 2f));
          }
        }, 25000);
  }

  @Override
  public void place() {
    super.place();
    updateRange();
    int applied = 0;
    for (Iterator<Inheritor> iterator = inheritors.iterator();
        iterator.hasNext() && applied < MAX_INHERITORS; ) {
      Inheritor inh = iterator.next();
      if (inh.isDepleted()) {
        iterator.remove();
      } else {
        inh.apply(this);
        applied++;
      }
    }
  }


  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced || spawnPoints.isEmpty()) {
      return;
    }
    bulletLauncher.tickCooldown();
    TdMob target = world.getMobsGrid()
        .search(new Point((int) x, (int) y), (int) stats[Turret.Stats.range],
            TargetingOption.FIRST);
    if (target != null) {
      setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
    }
    while (bulletLauncher.canAttack()) {
      bulletLauncher.attack(rotation);
    }

    buffHandler.tick();
  }

  @Override
  public void onStatsUpdate() {
    super.onStatsUpdate();
    bulletLauncher.setSpeed(0);
  }

  private void updateRange() {
    spawnPoints.clear();
    for (TrackPoint p : world.spacPoints) {
      if (Util.distanceSquared(p.getX() - x, p.getY() - y)
          < stats[Stats.range] * stats[Stats.range]) {
        spawnPoints.add(p);
      }
    }
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 200f;
    stats[Stats.pierce] = 3f;
    stats[Stats.aspd] = 1f;
    stats[Stats.projectileDuration] = 20f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 10f;
    stats[Stats.cost] = 300f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
