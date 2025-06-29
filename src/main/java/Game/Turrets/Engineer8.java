package Game.Turrets;

import Game.*;
import Game.Buffs.*;
import Game.Enums.DamageType;
import Game.Mobs.TdMob;
import general.Data;
import general.Description;
import general.Util;

import java.util.ArrayList;
import java.util.List;

public class Engineer8 extends Turret {

    public static final String image = "Engineer";

    private final BulletLauncher turretLauncher;

    private final List<Modifier<EngiTurret8>> turretMods = new ArrayList<>(1);

    public Engineer8(World world, int X, int Y) {
        super(world, X, Y, image,
                new BulletLauncher(world, "Spanner"));
        onStatsUpdate();
        turretLauncher = new BulletLauncher(world, "Laser");
        bulletLauncher.addMobCollide(BasicCollides.damage);
    }

    public static TurretGenerator generator(World world) {
        return new TurretGenerator(world, image, "Basic", () -> new Engineer(world, -1000, -1000));
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
                while (bulletLauncher.canAttack()) {
                    bulletLauncher.attack(rotation);
                }
            }
        }
        turretPlaceTimer += Game.tickIntervalMillis * stats[Stats.aspd] * stats[Engineer.ExtraStats.spawnSpd];
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
                        t.bulletLauncher.addAttackEffect(new SideWeapon(5,5));
                    });
                }, 50);
    }

    @Override
    protected Upgrade up030() {
        return new Upgrade("Dart",  new Description( "turrets shoot 10 bullets radially."),
                () -> {
                    turretMods.add(t -> {
                        t.bulletLauncher.radial = 10;
                    });
                }, 1500);
    }

    @Override
    protected Upgrade up040() {
        return new Upgrade("BeefyDart",  new Description( "turrets have beefier lasers."),
                () -> {
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 0.1f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.INCREASED, Stats.bulletSize, 5f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.pierce, 10f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.power, 36f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, 0.3f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.projectileDuration, 3.2f));
                    });
                }, 64000);
    }

    private static final long bigDart = Util.getUid();

    @Override
    protected Upgrade up050() {
        return new Upgrade("Dart",  new Description( "turrets shoot a ring of death, but there's fewer turrets."),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Engineer.ExtraStats.spawnSpd, 0.3f));
                    turretMods.add(t -> {
                        t.bulletLauncher.radial = 1000;
                    });
                }, 1500);
    }

    private static final long dartEatId = Util.getUid();

    @Override
    protected Upgrade up300() {
        return new Upgrade("MagnetDart",  new Description( "turrets explode on death"),
                () -> {
                    turretMods.add(t -> t.addBuff(new DelayedTrigger<Turret>(turr -> {
                        world.aoeDamage((int) turr.getX(), (int) turr.getY(), 400, 200, DamageType.TRUE);
                        world.explosionVisual((int) turr.getX(), (int) turr.getY(), 200, false, "Explosion1-0");
                    }, true)));
                }, 4000);
    }

    private final Projectile.Guided g = new Projectile.Guided(1000, 3);

    @Override
    protected Upgrade up100() {
        return new Upgrade("Radar",
                new Description( "turret projectiles last longer and seek"),
                () -> {
                    turretLauncher.addProjectileModifier(p -> p.addBuff(new OnTickBuff<Projectile>(g::tick)));
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.projectileDuration, 2));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, 2));
                    });
                }, 350);
    }

    @Override
    protected Upgrade up200() {
        return new Upgrade("MoreRadar",  new Description( "turret projectiles explode on depletion"),
                () -> {
                    turretLauncher.addProjectileModifier(p -> p.addBeforeDeath(proj -> {
                        world.aoeDamage((int) proj.getX(), (int) proj.getY(), 200, proj.getPower(),
                                DamageType.TRUE);
                        world.explosionVisual((int) proj.getX(), (int) proj.getY(), 200, false, "Explosion2-0");
                    }));
                }, 800);
    }

    @Override
    protected Upgrade up020() {
        return new Upgrade("Dart",  new Description( "turrets have 20% more attack speed, range, damage, duration."),
                () -> {
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 1.2f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.range, 1.2f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.power, 1.2f));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, EngiTurret.ExtraStats.duration, 1.2f));
                    });
                }, 900);
    }

    @Override
    protected Upgrade up500() {
        return new Upgrade("InfiniDart",
                new Description( "projectiles last literally forever (i'm sure this isn't game breaking)"),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.projectileDuration,
                            Float.POSITIVE_INFINITY));
                }, 1000000);
    }

    @Override
    protected Upgrade up400() {
        return new Upgrade("Goldfish",  new Description( "produces 5x more turrets, with less duration"),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Engineer.ExtraStats.spawnSpd, 5));
                    turretMods.add(t -> t.addBuff(
                            new StatBuff<Turret>(StatBuff.Type.MORE, EngiTurret.ExtraStats.duration, 0.2f)));
                }, 10000);
    }

    @Override
    protected Upgrade up001() {
        return new Upgrade("DoubleDart",  new Description( "shoots and makes turrets 2x faster."),
                () -> addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 2f)), 100);
    }

    @Override
    protected Upgrade up002() {
        return new Upgrade("TripleDart",  new Description( "shoots and makes turrets 3x faster."),
                () -> addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 3f)), 1000);
    }


    @Override
    protected Upgrade up003() {
        return new Upgrade("QuadDart", new Description( "???"),
                () ->{
                }, 8000);
    }

    @Override
    protected Upgrade up004() {
        return new Upgrade("QuinDart",  new Description( "shoots 5x faster."),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 5f));
                    bulletLauncher.setSpread(10);
                }, 40000);
    }

    @Override
    protected Upgrade up005() {
        return new Upgrade("TenDart",  new Description( "shoots 5x faster with double damage."),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 5f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.power, 2f));
                    bulletLauncher.setSpread(30);
                }, 150000);
    }

    // generated stats
    @Override
    public int getStatsCount() {
        return 11;
    }

    @Override
    public void clearStats() {
        stats[Stats.power] = 1f;
        stats[Stats.range] = 350f;
        stats[Stats.pierce] = 2f;
        stats[Stats.aspd] = 0.7f;
        stats[Stats.projectileDuration] = 2f;
        stats[Stats.bulletSize] = 30f;
        stats[Stats.speed] = 15f;
        stats[Stats.cost] = 100f;
        stats[Stats.size] = 50f;
        stats[Stats.spritesize] = 150f;
        stats[Engineer.ExtraStats.spawnSpd] = 0.6f;
    }

    public static final class ExtraStats {

        public static final int spawnSpd = 10;

        private ExtraStats() {
        }
    }
    // end of generated stats
}
