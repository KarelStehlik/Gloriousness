package Game.Turrets;

import Game.Ability;
import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.Ignite;
import Game.Buffs.Modifier;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.Tag;
import Game.BulletLauncher;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Mobs.TdMob.MoveAlongTrack;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import Game.World.TrackPoint;
import general.Data;
import general.Log;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import windowStuff.Sprite;

public class Necromancer extends Turret {

  public static final String image = "Necromancer";
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

  public Necromancer(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Zombie"));
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

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Necromancer",
        () -> new Necromancer(world, -1000, -1000));
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("ZombieDead", () -> "zombies are dead. The don't move but are more powerful.",
        () -> {
          bulletLauncher.setImage(path1Tier>=2?"ZombieDeadPierce":"ZombieDead");
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
        () -> "zombies spin in their graves. this causes nearby non-MOAB bloons to slow down",
        () -> {
          bulletLauncher.addProjectileModifier(p -> {
            if (rotates) {
              p.addBuff(
                  new OnTickBuff<Projectile>(proj -> proj.setRotation(proj.getRotation() + 5)));
            }
          });
          bulletLauncher.addProjectileModifier(
              p -> p.addBuff(new OnTickBuff<Projectile>(proj -> world.getMobsGrid()
                  .callForEachCircle((int) proj.getX(), (int) proj.getY(), 200, mob -> {
                    if (!mob.isMoab()) {
                      mob.addBuff(new StatBuff<TdMob>(Type.MORE, 20, TdMob.Stats.speed, 0.95f));
                    }
                  })))

          );
        }, 500);
  }

  private static final float respawnChance = .75f;

  @Override
  protected Upgrade up040() {
    return new Upgrade("Zombie",
        () -> "The first bloon that a zombie touches will spawn a new zombie on death.",
        () -> bulletLauncher.addProjectileModifier(p -> {

              RefFloat alreadyHit = new RefFloat(0);
              p.addMobCollide((zombie, bloon) -> {
                if (alreadyHit.get() > 0) {
                  return false;
                }
                alreadyHit.set(1);
                bloon.addBuff(new DelayedTrigger<TdMob>(mob -> {
                  var newZombie = bulletLauncher.attack(0, false);
                  newZombie.move(mob.getX(), mob.getY());
                }, true));
                return true;
              }, 0);

            }
        ), 21000);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("Zombie",
        () -> "Zombies use mummification to preserve themselves 5x longer.",
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
        () -> "Cool never-before-seen ability",
        () -> Ability.add("fire", 120000, () -> "Probably bigger than the one in btd8", () -> {
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
                  new OnTickBuff<Projectile>(dist / speed, p -> p.move(p.getX() + vx, p.getY() + vy)));
              mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.size, 5));
              mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.power, 20));
              mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.pierce, 200));
              mine.addBuff(new StatBuff<Projectile>(Type.MORE, Projectile.Stats.duration,
                  Float.POSITIVE_INFINITY));
              mine.addBeforeDeath(p -> BasicCollides.explodeFunc((int) p.getX(),
                  (int) p.getY(), p.getPower() * 500, 2000));
              mine.addBuff(new Tag<Projectile>(EatingTurret.EatImmuneTag));
              Sprite ms = mine.getSprite();
              ms.setLayer(5);
              ms.playAnimation(ms.new BasicAnimation("Bomb-0", .1f).loop());

            }, megaMineId
        ), 150000);
  }

  private boolean ignites = false;

  private void explode(Projectile proj, float power, float radius, String img, int centreX, int centreY) {
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
    return new Upgrade("Bomb-0", () -> "zombies explode when destroyed.",
        () -> bulletLauncher.addProjectileModifier(
            p -> p.addBeforeDeath(proj -> explode(proj, 5, 150, "Explosion1-0"))), 500);
  }

  @Override
  protected Upgrade up002() {
    return new Upgrade("Duck", () -> "zombies also explode on contact.",
        () -> bulletLauncher.addProjectileModifier(p -> p.addMobCollide((proj, mob) -> {
          explode(proj, 7F, 100F, "Explosion2-0", (int) mob.getX(), (int) mob.getY());
          return true;
        })), 2000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("Zombie",
        () -> "zombies also explode all the time. this is mostly for show. They get more pierce.",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Turret.Stats.pierce, 2));
          bulletLauncher.addProjectileModifier(p -> p.addBuff(
              new OnTickBuff<Projectile>(proj -> explode(proj, 20, 50, "Explosion2-0"))));
        }, 12000);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("Zombie", () -> "explosions also ignite things",
        () -> ignites = true, 20000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("Zombie", () -> "constantly ignites everything",
        () -> addBuff(new OnTickBuff<Turret>(t -> world.getMobsList().forEach(
            mob -> mob.addBuff(new Ignite<>(stats[Stats.power] * stats[Stats.pierce], 3000))))),
        50000);
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("MoreZombies", () -> "produces zombies faster.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 1.8f)), 500);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("ZombiePierce", () -> "zombies have more pierce",
        () -> {
      bulletLauncher.setImage(path2Tier>=1? "ZombieDeadPierce": "ZombiePierce");
      addBuff(new StatBuff<Turret>(Type.MORE, Stats.pierce, 2));
      }, 500);
  }

  private static Modifier<Necromancer> getRandomInheritorEffect() {
    return switch (Data.gameMechanicsRng.nextInt(1, 5)) {
      case 1 -> nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.aspd, .5f));
      case 2 -> nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.pierce, .5f));
      case 3 -> nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.projectileDuration, .5f));
      case 4 -> nec -> nec.addBuff(new StatBuff<Turret>(Type.INCREASED, Stats.power, .5f));
      default -> nec -> {
      };
    };
  }

  public static final int MAX_INHERITORS = 5;

  @Override
  protected Upgrade up300() {
    return new Upgrade("Inheritor", () -> "use genetic engineering to buff the next 3 necromancers. "
        + "Each tower can only have " + MAX_INHERITORS + " genetic modifications at once.",
        () -> inheritors.add(new Inheritor(3, getRandomInheritorEffect())), 2000);
  }


  private static final long rangeBuffId=Util.getUid();
  @Override
  protected Upgrade up400() {
    return new Upgrade("Inheritor2",
        () -> "apply 10 more genetic modifications to this (bypassing the normal limit)",
        () -> {
          for (int i = 0; i < 10; i++) {
            getRandomInheritorEffect().mod(this);
          }
          addBuff(new OnTickBuff<Turret>(necro->{
            for (Turret t : world.getTurrets()) {
              if (t!=necro && Util.distanceSquared(t.getX() - necro.getX(), t.getY() - necro.getY()) <= Util.square(
                  necro.getStats()[Stats.range])) {
                if(t.addBuff(new Tag<Turret>(rangeBuffId,50))){
                  t.addBuff(new StatBuff<Turret>(Type.INCREASED,50,Stats.range,.1f));
                }
              }
            }
          }));
        }, 10000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("Inheritor3",
        () -> "sacrifices nearby towers, gain a genetic modification for every 2500 money sacrificed",
        () -> {
          float sacced = 0;
          for (Turret t : world.getTurrets()) {
            if (t!=this && Util.distanceSquared(t.getX() - x, t.getY() - y) <= Util.square(
                stats[Stats.range])) {
              sacced += t.totalCost;
              t.delete();
            }
          }
          for(;sacced>2500;sacced-=2500){
            getRandomInheritorEffect().mod(this);
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
        .getFirst(new Point((int) x, (int) y), (int) stats[Turret.Stats.range]);
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
    Log.write(stats[Stats.range] * stats[Stats.range]);
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
