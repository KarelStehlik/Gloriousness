package Game.Common.Turrets;

import Game.Common.Buffs.Buff.SkyShot;
import Game.Common.Buffs.Modifier.Explosive;
import Game.Common.Buffs.Buff.Ignite;
import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Common.Buffs.Modifier.Modifier;
import Game.Common.BulletLauncher;
import Game.Common.BulletLauncher.Cannon;
import Game.Common.Projectile;
import Game.Misc.Game;
import Game.Misc.TdWorld;
import Game.Misc.TurretGenerator;
import GlobalUse.Data;
import GlobalUse.Description;
import GlobalUse.Util;
import org.joml.Random;
import org.joml.Vector2d;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.Sprite.Sprite.FrameAnimation;

import static Game.Common.Turrets.Turret.Stats.projectileDuration;
import static Game.Common.Turrets.Turret.Stats.speed;

public class Mortar extends Turret {


    @Override
    protected ImageData getImageUpdate() {
        String monkimg = "mortarMonkey";
        String badgeimg = "mortarBadge";
        String mortarimg = "mortar";


        if (badgeSprite != null) {
            badgeSprite.setImage(badgeimg);
        }
        if (monkeySprite != null) {
            monkeySprite.setImage(monkimg);
        }
        return Graphics.getImage(mortarimg);
    }

    private Explosive explosive = new Explosive(0, 0);
    private Modifier<Projectile> explosion=p-> {
        explosive.mod(target());
    };

    @Override
    protected void extraStatsUpdate() {
        if (explosive != null) {
            explosive.damage = stats[ExtraStats.explodPower];
            explosive.setRadius((int) stats[ExtraStats.radius]);
        }
    }

    public Sprite badgeSprite;
    public Sprite monkeySprite;

    public Mortar(TdWorld world, int X, int Y) {
        super(world, X, Y, new BulletLauncher(world, "coconut"));

        badgeSprite = new Sprite("turretBase", 22).setSize(sprite.getWidth() * 2 * 0.75f,
                sprite.getWidth() * 2 * 0.75f);
        world.getBs().addSprite(badgeSprite);
        badgeSprite.setShader("basic");

        monkeySprite = new Sprite("turretBase", 22).setSize(sprite.getWidth() * 1.75f,
                0);
        monkeySprite.setNaturalHeight();

        bulletLauncher.addProjectileModifier(p -> p.addBeforeDeath(this.explosive));
        bulletLauncher.addProjectileModifier(p -> {
            p.addBuff(new SkyShot(2000f, getStats()[projectileDuration]/2f, 100));
        });
        world.getBs().addSprite(monkeySprite);
        move(X, Y);
        getImageUpdate();
        onStatsUpdate();
    }

    public static TurretGenerator generator(TdWorld world) {
        return new TurretGenerator(world, "mortar", "Mortar",
                () -> new Mortar(world, -1000, -1000));
    }


    @Override
    public void move(float _x, float _y) {
        super.move(_x, _y);
        sprite.setPosition(_x, _y);
        badgeSprite.setPosition(sprite.getX(), sprite.getY() - sprite.getHeight() * 0.2f);
        monkeySprite.setPosition(sprite.getX() + sprite.getWidth() + monkeySprite.getWidth(), sprite.getY() - sprite.getHeight() + monkeySprite.getHeight());
        rangeDisplay.setPosition(_x, _y);
        if (world.canFitTurret((int) x, (int) y, stats[Stats.size])) {
            rangeDisplay.setColors(Util.getColors(0, 0, 0));
        } else {
            rangeDisplay.setColors(Util.getColors(9, 0, 0));
        }
        bulletLauncher.move(_x, _y);
    }

    @Override
    protected Upgrade up100() {
        return new Upgrade("Bomb-0",
                new Description("Double Bombs"
                        ,
                        "Doubles... the bombs.",
                        "Genius."),
                () -> {
                    bulletLauncher.cannons.add(new Cannon(0, 0));
                }, 300);
    }

    @Override
    protected Upgrade up200() {
        return new Upgrade("Bomb-0",
                new Description("Focused Bombardment"
                        ,
                        "Adds 3 more bombs, but bombs are smaller.",
                        "Doesn't actually focus shit"),
                () -> {
                    for (int i = 0; i < 3; i++) {
                        bulletLauncher.cannons.add(new Cannon(0, 0));
                    }
                    addBuff(new StatBuff<Turret>(Type.MORE, Stats.bulletSize, 0.6f));
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, 0.4f));
                }, 300);
    }

    @Override
    protected Upgrade up300() {
        return new Upgrade("Bomb-0",
                new Description("Glorious Coverage"
                        ,
                        "Increases attack speed based on spread",
                        ""),
                () -> {
                    float attackArea = Util.square(originalStats[ExtraStats.spread] / 100);
                    addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, attackArea));
                }, 300);
    }

    @Override
    protected Upgrade up010() {
        return new Upgrade("Bomb-0",
                new Description("Larger Shells"
                        ,
                        "Extra AoE, +1 damage, increases spread",
                        ""),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, 1.5f));
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.spread, 2));
                }, 300);
    }

    @Override
    protected Upgrade up020() {
        return new Upgrade("Bomb-0",
                new Description("Chaotic Bombing"
                        ,
                        "No need to he accurate if a miss is lethal enough",
                        "further increases explosion size based on spread"),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
                    float radiusBuff = 1 + originalStats[ExtraStats.spread] / 200f;
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, radiusBuff));
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.spread, 1.5f));
                }, 300);
    }


    @Override
    protected Upgrade up001() {
        return new Upgrade("Fireball-0",
                new Description("Glorious Flames"
                        ,
                        "fire",
                        ""),
                () -> {
                    explosive.addPreEffect(mob -> mob.addBuff(new Ignite<>(this.stats[Stats.power], 2000)));
                }, 300);
    }


    @Override
    public void onGameTick(int tick) {
        if (notYetPlaced) {
            return;
        }
        bulletLauncher.tickCooldown();

        Vector2d mousePos =  Game.get().getUserInputListener().getPos();
        double spreadx= Math.sqrt(Data.gameMechanicsRng.nextFloat(0,(float)Math.pow(getStats()[ExtraStats.spread],2)));
        double spready= Math.sqrt(Data.gameMechanicsRng.nextFloat(0,(float)Math.pow(getStats()[ExtraStats.spread],2)));

        if(Data.gameMechanicsRng.nextBoolean()){
            spreadx*=-1;
        }
        if(Data.gameMechanicsRng.nextBoolean()){
            spready*=-1;
        }
        while (bulletLauncher.canAttack()) {
            addBuff(new StatBuff<Turret>(Type.FINALLY_ADDED, speed, -getStats()[speed]+ Util.distanceNotSquared((float)(getX()-mousePos.x+spreadx),
                    (float)(getY()-mousePos.y+spready))/getStats()[projectileDuration]*Game.secondsPerFrame));
            bulletLauncher.attack((float) mousePos.x, (float) mousePos.y, true);
        }

        buffHandler.tick();
    }

    @Override
    public void delete() {
        sprite.delete();
        badgeSprite.delete();
        monkeySprite.delete();
        buffHandler.delete();
        rangeDisplay.delete();
    }

    // generated stats
  @Override
  public int getStatsCount() {
    return 13;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 2f;
    stats[Stats.range] = 0f;
    stats[Stats.pierce] = 12f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.4f, 0.8f);
    stats[Stats.projectileDuration] = 1.5f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(50f, 80f);
    stats[Stats.speed] = 0f;
    stats[Stats.cost] = 250f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.spread] = Data.gameMechanicsRng.nextFloat(10f, 300f);
    stats[ExtraStats.radius] = Data.gameMechanicsRng.nextFloat(50f, 75f);
    stats[ExtraStats.explodPower] = 1f;
  }

  public static final class ExtraStats {

    public static final int spread = 10;
    public static final int radius = 11;
    public static final int explodPower = 12;

    private ExtraStats() {
    }
  }
  // end of generated stats

}
