package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.Enums.DamageType;
import Game.Mobs.TdMob;
import Game.Projectile;
import Game.TdWorld;
import Game.TurretGenerator;
import general.Data;
import general.Description;
import general.RefFloat;
import windowStuff.Sprite;

public class Druid8 extends Turret {

  public static final String image = "Druid";

  public Druid8(TdWorld world, int X, int Y) {
    super(world, X, Y, image, new BulletLauncher(world, ""));
    bulletLauncher.setLauncher(
        (world1, image1, x1, y1, speed, rotation1, w, h, pierce, size, duration, power) -> new DruidBall(
            world1, image1, x1, y1, speed, rotation1, w, pierce, size, duration, power,
            getStats()[ExtraStats.regrowTime]));
    originalStats[ExtraStats.regrowTime] = 0.5f * originalStats[Stats.speed];
    stats[ExtraStats.regrowTime] = originalStats[ExtraStats.regrowTime];
    bulletLauncher.setImage("DruidBall");
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.addProjectileModifier(
        p -> p.addBeforeDeath(proj -> regrow(new RefFloat(this.stats[ExtraStats.respawns]), proj)));
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, image, "Druid", () -> new Druid8(world, -1000, -1000));
  }

  private boolean root(TdMob mob) {
    float slow = 1 + 100 / mob.getStats()[TdMob.Stats.health];
    float durationMs = 3000;
    mob.addBuff(new StatBuff<TdMob>(Type.MORE, durationMs, TdMob.Stats.speed, 1 / slow));
    mob.addBuff(new StatBuff<TdMob>(Type.ADDED, durationMs, TdMob.Stats.spawns, -1));
    Sprite roots = new Sprite("root", 3).setSize(100, 100).addToBs(world.getBs());
    mob.addBuff(new OnTickBuff<TdMob>(m -> roots.setPosition(mob.getX(), mob.getY())));
    mob.addBuff(new DelayedTrigger<TdMob>(durationMs, m -> roots.delete(), true));
    return true;
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("brambles", new Description("roots", "roots bloons. +3 damage and +1 pierce",
        "rooted bloons are slowed and don't spawn children"),
        () -> {
          sprite.setImage("Druid1");
          bulletLauncher.addMobCollide((p, m) -> root(m), 0);
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.power, 3));
          addBuff(new StatBuff<Turret>(Type.ADDED, Stats.pierce, 1));
        }, 50);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("bouncy", new Description("bounces from walls up to 4 times"),
        () -> {
          sprite.setImage("Druid2");
          bulletLauncher.addProjectileModifier(p -> p.addBuff(
              new OnTickBuff<Projectile>(new Projectile.LimitedBounce(4))));
        }, 90);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("demoncore",
        new Description("The Shadow Core", "does more damage to MOABs and less to regular bloons",
            ""),
        () -> {
          sprite.setImage("Druid2");
          bulletLauncher.setImage("voidball");
          bulletLauncher.removeMobCollide(BasicCollides.damage);
          bulletLauncher.addMobCollide((p, m) -> {
            m.takeDamage(p.getPower() * (m.isMoab() ? 5f : 0.5f), DamageType.MAGIC);
            return true;
          });
        }, 750);
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("regrowth", new Description("regrows up to 10 additional times"),
        () -> addBuff(
            new StatBuff<Turret>(Type.ADDED, ExtraStats.respawns, 10)),
        40);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("druidbook",
        new Description("Tome", "Increases attack speed, balls get bigger when they regrow"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 2));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.pierceScaling, 0.5f));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.powScaling, 0.5f));
          addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.sizeScaling, 0.3f));
        }, 380);
  }

  private void regrow(RefFloat respawnsLeft, Projectile proj) {
    if (respawnsLeft.get() < 1) {
      return;
    }
    respawnsLeft.add(-1);
    proj.setActive(false);
    proj.addBuff(
        new StatBuff<Projectile>(Type.INCREASED, Projectile.Stats.power,
            stats[ExtraStats.powScaling]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.INCREASED, Projectile.Stats.size,
            stats[ExtraStats.sizeScaling]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.INCREASED, Projectile.Stats.pierce,
            stats[ExtraStats.pierceScaling]));
    proj.addBuff(
        new StatBuff<Projectile>(Type.FINALLY_ADDED, Projectile.Stats.pierce,
            stats[Stats.pierce]));
    ((DruidBall) proj).special(0);
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 15;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = Data.gameMechanicsRng.nextFloat(200f, 350f);
    stats[Stats.pierce] = 2f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(.2f, .4f);
    stats[Stats.projectileDuration] = 999f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(160f, 260f);
    stats[Stats.speed] = Data.gameMechanicsRng.nextFloat(1f, 5f);
    stats[Stats.cost] = 50f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.respawns] = 1f;
    stats[ExtraStats.sizeScaling] = 0f;
    stats[ExtraStats.powScaling] = 0f;
    stats[ExtraStats.pierceScaling] = 0f;
    stats[ExtraStats.regrowTime] = 1f;
  }

  public static final class ExtraStats {

    public static final int respawns = 10;
    public static final int sizeScaling = 11;
    public static final int powScaling = 12;
    public static final int pierceScaling = 13;
    public static final int regrowTime = 14;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
