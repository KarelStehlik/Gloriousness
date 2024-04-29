package Game.Turrets;

import Game.Ability;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.Tag;
import Game.BulletLauncher;
import Game.CallAfterDuration;
import Game.DamageType;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Player;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import general.Data;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import windowStuff.Sprite;
import windowStuff.Text;

public class EmpoweringTurret extends Turret {

  public static final String image = "EmpoweringTower";
  private static final long projBuffId = Util.getUid();

  public EmpoweringTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Buff"));
    onStatsUpdate();
    bulletLauncher.addProjectileCollide(this::collide);
    bulletLauncher.addProjectileModifier(p -> p.addBuff(new Tag<Projectile>(projBuffId, p1 -> {
    })));
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Empowering",
        () -> new EmpoweringTurret(world, -1000, -1000));
  }

  private void addBuff(Projectile p2, float pow) {
    p2.addMobCollide(
        (proj2, mob) -> {
          world.aoeDamage((int) proj2.getX(), (int) proj2.getY(), (int) stats[ExtraStats.radius],
              pow, DamageType.TRUE);
          world.lesserExplosionVisual(proj2.getX(), proj2.getY(), stats[ExtraStats.radius])
              .getSprite().setOpacity(.8f);
          return true;
        });
  }


  private boolean collide(Projectile p1, Projectile p2) {
    final float pow = p1.getPower();
    p2.addBuff(new Tag<Projectile>(projBuffId, proj2 -> addBuff(proj2, pow)));
    return true;
  }


  RefFloat assaCooldown = new RefFloat(0);

  @Override
  protected Upgrade up010() {
    return new Upgrade("Button", () -> "Hires edgy assassins to destroy targets",
        () -> addBuff(new OnTickBuff<Turret>(turr -> {
          if (assaCooldown.get() > 0) {
            assaCooldown.add(-1);
          }
          while (assaCooldown.get() <= 0) {
            var mob = target();
            if (mob == null) {
              return;
            }
            assaCooldown.add(stats[ExtraStats.assaCd]);
            float angle = Data.unstableRng.nextFloat() * 360;
            Sprite assa = new Sprite("Assassin", 1).setPosition(-1000, -1000)
                .addToBs(world.getBs()).setSize(200, 200).setRotation(angle);
            mob.addBuff(new DelayedTrigger<TdMob>(stats[ExtraStats.assaDuration], m -> {
              m.takeDamage(stats[Stats.power] * stats[ExtraStats.assaDamageMult],
                  DamageType.TRUE);
              assa.delete();
              world.explosionVisual(m.getX(), m.getY(), 70, true, "Explosion1-0");
            }, true));
            mob.addBuff(new OnTickBuff<TdMob>(stats[ExtraStats.assaDuration],
                m -> assa.setPosition(
                    m.getX() + m.getStats()[TdMob.Stats.size] * .4f * Util.sin(-angle)
                    , m.getY() + m.getStats()[TdMob.Stats.size] * .4f * Util.cos(angle)
                ),
                false));
          }
        })), 5000);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Button", () -> "Assassins are more edgy and do 100x more damage.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.assaDamageMult, 100)), 20000);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("Button",
        () -> "Assassins are more patient and do 1000000x more damage.",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.assaDamageMult, 1000000))
        , 40000);
  }

  @Override
  protected Upgrade up040() {
    return new Upgrade("Button", () -> "Assassins are more aggressive and appear more often",
        () -> addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.assaCd, 0.3f))
        , 50000);
  }

  private static final long abilityId = Util.getUid();

  @Override
  protected Upgrade up050() {
    return new Upgrade("Button", () -> "Ability: hire a super edgy assassin "
        + "with a 5000-year-old katana that has been folded a billion and one times, "
        + "forged illegally by a now extinct shadowy order of super ninjas inside a space volcano, "
        + "paid for with the blood of a thousand innocents and having cut down a thousand more, "
        + "which now contains the suffering and desecrated souls of the Shadowy Fog Skull Slayer Assassin's "
        + "family that he betrayed and killed by his own hand for no reason. It is said that any who dare "
        + "pronounce the name of this katana will suffer the doom of an eternal torment "
        + "forever in the dark abyss of death, however no one has been able to verify this, as "
        + "its name is some unpronounceable Japanese bullshit that you physically can not utter.",
        () -> {
          var a = Ability.add("Assassin", 10000, () -> "Agony! Torment! Retribution! This is the creed of the Shadowy Assassin Demonic Blade Slayer Super Ninjas, warriors forged in the soul-scorching fires of despair and fueled by the unrelenting anguish that courses through our veins. Our suffering, matched only by the suffering we yearn to inflict upon our foes, is the very essence of our existence, the driving force that propels us forward in our dark and violent crusade. Without this torment, this unending torment that threatens to consume us whole, we Shadowy Assassin Demonic Blade Slayer Super Ninjas would be nothing but hollow shells, mere shadows of the warriors we now are.\n"
              + '\n'
              + "Oh, how ignorant you are to believe that we should not all kill ourselves as escape from the crushing misery of our bleak reality! You, who frolick in the shallow waters of false happiness, you cannot comprehend the depths of darkness that lurk within us. It is not madness that plagues us, no, it is clarity. We, the Shadowy Assassin Demonic Blade Slayer Super Ninjas, see the world for what it truly is: a desolate wasteland of despair and deceit, its only light thrown by the glorious certainty of death.\n"
              + '\n'
              + "We are not driven by insanity, but by a true sense of righteousness that demands no accolades or recognition. We, the Ninjas, are the silent protectors of this doomed world, the only salvation to be found in our merciless blades. We cut down all who stand in our path, leaving nothing but devastation and carnage in our wake. We envy all whose blood we spill, for they are fortunate enough to find death.\n"
              + '\n'
              + "We have sacrificed everything for this noble cause. We have forsaken our very souls, stained our blades in the blood of our families, and abandoned all that we once held dear. But this price is nothing in face of the ability to deliver death upon our enemies. And in the end, when the final moment of silence descends upon us, when all that remains is the sound of our own breathless cries, we will ourselves descend into the eternal void of oblivion, our brutal quest complete.\n"
              + '\n'
              + "You will come to understand, in time, the glory that can be found in misery. You will feast your eyes on the beauty of our sacrifice, the righteousness of our vengeance. And you will bow before the Shadowy Assassin Demonic Blade Slayer Super Ninjas, the true masters of death and despair.", () -> {
            int x = (int) Game.get().getUserInputListener().getX();
            int y = (int) Game.get().getUserInputListener().getY();
            world.getMobsGrid().callForEachCircle(
                x, y, 50, TdMob::delete
            );
            Sprite s = new Sprite("Explosion1-0", 5).setPosition(x, y).setSize(500, 500)
                .addToBs(world.getBs());
            s.playAnimation(s.new BasicAnimation("Explosion1-0", .2f))
                .setDeleteOnAnimationEnd(true);
          }, abilityId);
          addBuff(new DelayedTrigger<Turret>(t -> a.delete(), true));
        }
        , 499000);
  }

  @Override
  protected Upgrade up001() {
    return new Upgrade("Button", () -> "shoots faster and affects more projectiles",
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 3));
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.pierce, 3));
        }
        , 1500);
  }

  private static final long aoeBuffId = Util.getUid();

  @Override
  protected Upgrade up002() {
    return new Upgrade("Button", () -> "gives nearby towers 15% increased attack speed",
        () -> addBuff(new OnTickBuff<Turret>(buffer -> world.getTurrets().forEach(t -> {
          if (t != buffer
              && Util.distanceSquared(t.getX() - buffer.getX(), t.getY() - buffer.getY())
              <= Util.square(
              buffer.getStats()[Stats.range])) {
            if (t.addBuff(new Tag<Turret>(aoeBuffId, 50))) {
              t.addBuff(new StatBuff<Turret>(Type.INCREASED, 50, Stats.aspd, .15f));
            }
          }
        })))
        , 3000);
  }

  @Override
  protected Upgrade up003() {
    return new Upgrade("Button", () -> "gives the player 0.5/sec added attack speed",
        () -> addBuff(new OnTickBuff<Turret>(
            buffer -> world.getPlayer().addBuff(
                new StatBuff<Player>(Type.ADDED, Game.tickIntervalMillis + 1, Player.Stats.aspd,
                    .5f)
            )
        ))
        , 6000);
  }


  @Override
  protected Upgrade up004() {
    return new Upgrade("Button", () -> "more attack speed, power, granted explosion radius.",
        () -> {
          bulletLauncher.setSpread(30);
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 5));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.power, 20));
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, 2));
        }
        , 40000);
  }

  Projectile.Guided guide = new Projectile.Guided(500, 5);

  @Override
  protected Upgrade up005() {
    return new Upgrade("Button", () -> "All projectiles seek",
        () -> addBuff(new OnTickBuff<Turret>(t -> world.getProjectilesList().forEach(guide::tick)))
        , 30000);
  }

  private void gainMoney(long amount, float duration) {
    if (WasDeleted()) {
      return;
    }
    world.setMoney(world.getMoney() + amount);
    var t = new Text("+" + amount, "Calibri", 500, (int) x - 130 + Data.unstableRng.nextInt(0, 150),
        (int) y - 85 + Data.unstableRng.nextInt(0, 150), 6, 50, world.getBs());
    t.setColors(Util.getColors(1.5f, 1.5f, 0));
    Game.get().addTickable(new CallAfterDuration(t::delete, duration));
  }

  private float endOfTurnGold = 100;

  @Override
  protected Upgrade up100() {
    return new Upgrade("Button", () -> "get 100 end-of-turn gold",
        () -> endOfRoundEffects.add(() -> gainMoney((long) endOfTurnGold, 2000))
        , 1500);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Button",
        () -> "get 50 additional end-of-turn gold per turn while this exists",
        () -> endOfRoundEffects.add(() -> endOfTurnGold += 50)
        , 5000);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("Button", () -> "at end of turn, increase your total gold by 0.5%",
        () -> endOfRoundEffects.add(() -> gainMoney((long) (world.getMoney() * .005f), 2000))
        , 10000);
  }

  private static final long increasedGoldIf = Util.getUid();

  @Override
  protected Upgrade up400() {
    return new Upgrade("Button",
        () -> "bloons popped in radius give 50% increased gold (no stacking)",
        () -> addBuff(new OnTickBuff<Turret>(buffer ->
            world.getMobsGrid().callForEachCircle((int) buffer.getX(), (int) buffer.getY(),
                (int) buffer.getStats()[Stats.range], mob -> {
                  if (mob.addBuff(new Tag<TdMob>(increasedGoldIf, 100))) {
                    mob.addBuff(new StatBuff<TdMob>(Type.INCREASED, 100, TdMob.Stats.value, .5f));
                  }
                }
            )))
        , 10000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("Button", () -> "when the player attacks, get money equal to his damage",
        () -> world.getPlayer().getBulletLauncher().addProjectileModifier(p -> gainMoney(
            (long) p.getPower(),
            Math.min(2000, 8000 / world.getPlayer().getStats()[Player.Stats.aspd])))
        , 50000);
  }


  // generated stats
  @Override
  public int getStatsCount() {
    return 14;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 5f;
    stats[Stats.range] = 400f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = 1.7f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 8f;
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.assaCd] = 1000f;
    stats[ExtraStats.assaDamageMult] = 100f;
    stats[ExtraStats.assaDuration] = 5000f;
    stats[ExtraStats.radius] = 150f;
  }

  public static final class ExtraStats {

    public static final int assaCd = 10;
    public static final int assaDamageMult = 11;
    public static final int assaDuration = 12;
    public static final int radius = 13;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
