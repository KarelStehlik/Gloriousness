package Game.Common.Turrets;

import Game.Common.Buffs.Buff.*;
import Game.Misc.BasicCollides;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Common.BulletLauncher;
import Game.Enums.DamageType;
import Game.Mobs.MobClasses.TdMob;
import Game.Common.Projectile;
import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.Misc.TurretGenerator;
import GlobalUse.*;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;

import java.util.Random;

public class Druid8 extends Turret {

    @Override
    protected ImageData getImageUpdate() {
        return Graphics.getImage("Druid");
    }

    public Druid8(TdWorld world, int X, int Y) {
        super(world, X, Y, new BulletLauncher(world, "DruidBall"));
        bulletLauncher.setLauncher(
                (world1, image1, x1, y1, speed, rotation1, w, ar, pierce, size, duration, power) -> new DruidBall(
                        world1, image1, x1, y1, speed, rotation1, w, ar, pierce, size, duration, power,
                        getStats()[ExtraStats.regrowTime]));
        originalStats[ExtraStats.regrowTime] = originalStats[ExtraStats.regrowTime] * originalStats[Stats.speed];
        stats[ExtraStats.regrowTime] = originalStats[ExtraStats.regrowTime];
        originalStats[Stats.range] += 75 * originalStats[Stats.speed];
        stats[Stats.range] = originalStats[Stats.range];

        bulletLauncher.setImage("DruidBall");
        onStatsUpdate();
        bulletLauncher.addMobCollide(BasicCollides.damage);
        bulletLauncher.addProjectileModifier(this::modProjectile);
    }

    private void modProjectile(Projectile p) {
        var respawnsLeft = new RefFloat(stats[Druid8.ExtraStats.respawns]);
        p.addBeforeDeath(proj -> regrow(respawnsLeft, proj));
    }


    public static TurretGenerator generator(TdWorld world) {
        return new TurretGenerator(world, "Druid", "Druid", () -> new Druid8(world, -1000, -1000));
    }
    private float rootEffectIveAt=10;
    private boolean root(TdMob mob, float strength) {
        float durationMs = 3000;
        if (mob.getStats()[TdMob.Stats.maxHealth] > rootEffectIveAt) {
            strength /= mob.getStats()[TdMob.Stats.health] / (rootEffectIveAt/10);
            durationMs/=15;
        }else if (mob.getStats()[TdMob.Stats.spawns] > 1) {
            mob.addBuff(new StatBuff<TdMob>(Type.ADDED, durationMs, TdMob.Stats.spawns, 1 - mob.getStats()[TdMob.Stats.spawns]));
        }

        float slow = 1 + 50 * strength / mob.getStats()[TdMob.Stats.health];
        mob.addBuff(new StatBuff<TdMob>(Type.MORE, durationMs, TdMob.Stats.speed, 1 / slow));

        VisualEffect.Aggregator aggr = (VisualEffect.Aggregator) mob.getBuffHandler().find(VisualEffect.class);
        if (aggr != null && aggr.hasExtendEffect("root", durationMs)) {
            return true;
        }
        mob.addBuff(new VisualEffect<>("root", durationMs, this::getRootImg, true));
        return true;
    }

    private Sprite getRootImg() {
        return new Sprite("thorns", 3).setSize(100, 100).setPosition(0, -500).addToBs(world.getBs());
    }

    @Override
    protected Upgrade up100() {
        return new Upgrade("brambles", new Description("Brambles", "roots bloons. +3 damage and +1 pierce",
                "rooted bloons are slowed and spawn a maximum of 1 children"),
                () -> {
                    sprite.setImage("Druid1");
                    bulletLauncher.addMobCollide((p, m) -> root(m, 1), 0);
                    addBuff(new StatBuff<Turret>(Type.ADDED, Stats.power, 3));
                    addBuff(new StatBuff<Turret>(Type.ADDED, Stats.pierce, 1));
                }, 50);
    }

    private boolean doTree = false;

    private void summonTree(Projectile proj) {
        treeCannon.setPower(proj.getPower() / 5);
        treeCannon.setSize(proj.getSize());
        treeCannon.move(proj.x, proj.y);
        treeCannon.attack(90);
    }

    BulletLauncher treeCannon;

    @Override
    protected Upgrade up200() {
        return new Upgrade("Tree", new Description("Grovekeeper", "summons long lasting trees, trees deal damage to nearby bloons and expire quickly when doing so",
                ""),
                () -> {
                    treeCannon = new BulletLauncher(this.world, "TreeSummon");
                    treeCannon.setPierce(Integer.MAX_VALUE);
                    treeCannon.addMobCollide(BasicCollides.damage);
                    treeCannon.addMobCollide((p, m) -> root(m, 0.01f), 0);
                    treeCannon.setSpeed(0);
                    treeCannon.setDuration(45);
                    treeCannon.addProjectileModifier((p) -> {
                        p.addBuff(new Intangiable(2, 0.25f, 9));

                    });
                    doTree = true;

                }, 200);
    }

    @Override
    protected Upgrade up300() {
        return new Upgrade("forestcore",
                new Description("Heart of the forest", "summons 5 trees; trees deal more damage",
                        "increases effectivity of root against larger bloons"),
                () -> {
                    rootEffectIveAt=800;
                    treeCannon.addProjectileModifier(
                            (p) -> {
                                p.addBuff(new StatBuff<>(Type.MORE, Projectile.Stats.power, 3));
                                p.move(p.x + (float) (Math.random() - 0.5) * 350, p.y + (float) (Math.random() - 0.5) * 350);
                                p.setRotation((float) (90 * (1 + (Math.random() - 0.5) / 3)));
                            }
                    );
                    treeCannon.cannons.addAll(BulletLauncher.radial(4));
                }, 750);
    }

    @Override
    protected Upgrade up010() {
        return new Upgrade("regrowth", new Description("regrows 3 additional times"),
                () -> addBuff(
                        new StatBuff<Turret>(Type.ADDED, ExtraStats.respawns, 3)),
                40);
    }

    @Override
    protected Upgrade up020() {
        return new Upgrade("bouncy", new Description("bounces from walls up to 2 times, adds 7 regrows"),
                () -> {
                    new StatBuff<Turret>(Type.ADDED, ExtraStats.respawns, 7);
                    sprite.setImage("Druid2");
                    bulletLauncher.addProjectileModifier(p -> p.addBuff(
                            new OnTickBuff<Projectile>(new Projectile.LimitedBounce(2))));
                }, 100);
    }

    @Override
    protected Upgrade up030() {
        return new Upgrade("druidbook",
                new Description("Tome", "Increases attack speed, balls get bigger when they regrow", "atcspd*2 and balls get +1 pierce per regrow"),
                () -> {
                    addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 2));
                    addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.pierceScaling, 1f));
                    addBuff(new StatBuff<Turret>(Type.ADDED, ExtraStats.sizeScaling, 0.11f));
                }, 550);
    }


    private int distPerDamage = 100;

    private void fly(Projectile projectile, RefFloat accumulator) {
        if (!projectile.isActive())
            return;
        projectile.addBuff(new StatBuff<>(Type.INCREASED, Projectile.Stats.power, projectile.getSpeed() / distPerDamage));
        accumulator.add(projectile.getSpeed());
        if (accumulator.get()+projectile.getSpeed() > 300&&accumulator.get()<300) {
            projectile.getSprite().setColors(Util.getColors(250, 0, 0));
            projectile.addBuff(new StatBuff<>(Type.MORE, Projectile.Stats.power, 2));
            projectile.addBuff(new StatBuff<>(Type.MORE, Projectile.Stats.pierce, 2));
        }

    }

    @Override
    protected Upgrade up001() {
        return new Upgrade("birdsong", new Description("attacks faster with higher range, adds range"),
                () -> {
                    // rtange goes up with projectile speed
                    float buffAmount = (originalStats[Stats.range]) / 1000 + 0.1f;
                    addBuff(new StatBuff<>(Type.ADDED, Stats.aspd, buffAmount));
                    addBuff(new StatBuff<>(Type.ADDED, Stats.range, 100));
                },
                40);
    }

    @Override
    protected Upgrade up002() {
        return new Upgrade("flight",
                new Description("Forest winds", "Projectiles that travel far deal more damage", "atcspd*2 and balls get +1 pierce per regrow"),
                () -> {
                    bulletLauncher.addProjectileModifier((proj) -> {
                        RefFloat accumulator = new RefFloat(0);
                        proj.addBuff(new OnTickBuff<>(
                                (p) -> {
                                    fly(p, accumulator);

                                }));
                    });
                }, 150);
    }

    @Override
    protected Upgrade up003() {
        return new Upgrade("shadowcore",
                new Description("The Shadow Core", "Deals more damage to MOABs and less to regular bloons; Ignores armor.",
                        "5 times damage to moabs and 5 times less to bloons. Also multiplies damage by pierce and sets it to one."),
                () -> {
                    bulletLauncher.addProjectileModifier((Projectile p) -> {
                        float pierceChange = p.getStats()[Projectile.Stats.pierce];
                        p.addBuff(new StatBuff<>(Type.ADDED, Projectile.Stats.pierce, 1 - pierceChange));
                        p.addBuff(new StatBuff<>(Type.MORE, Projectile.Stats.pierce, pierceChange));
                    });

                    sprite.setImage("Druid2");
                    bulletLauncher.setImage("voidball");
                    bulletLauncher.removeMobCollide(BasicCollides.damage);
                    bulletLauncher.addMobCollide((p, m) -> {
                        m.takeDamage(p.getPower() * (m.isMoab() ? 5f : 0.5f), DamageType.MAGIC);
                        return true;
                    });
                }, 750);
    }

    private void regrow(RefFloat respawnsLeft, Projectile proj) {
        if (respawnsLeft.get() < 1) {
            if (doTree) {
                summonTree(proj);
            }
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
    stats[Stats.power] = 2f;
    stats[Stats.range] = 100f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(.2f, .4f);
    stats[Stats.projectileDuration] = 999f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(160f, 260f);
    stats[Stats.speed] = Data.gameMechanicsRng.nextFloat(1f, 5f);
    stats[Stats.cost] = 50f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
    stats[ExtraStats.respawns] = 2f;
    stats[ExtraStats.sizeScaling] = 0f;
    stats[ExtraStats.powScaling] = 0f;
    stats[ExtraStats.pierceScaling] = 0f;
    stats[ExtraStats.regrowTime] = 1.25f;
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
