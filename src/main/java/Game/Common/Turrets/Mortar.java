package Game.Common.Turrets;

import Game.Common.Buffs.Buff.DelayedTrigger;
import Game.Common.Buffs.Buff.OnTickBuff;
import Game.Common.Buffs.Buff.SkyShot;
import Game.Common.Buffs.Buff.Trail;
import Game.Common.Buffs.Modifier.Accuracy;
import Game.Common.Buffs.Modifier.Explosive;
import Game.Common.Buffs.Buff.Ignite;
import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.StatBuff.Type;
import Game.Common.Buffs.Modifier.Modifier;
import Game.Common.BulletLauncher;
import Game.Common.BulletLauncher.Cannon;
import Game.Common.Projectile;
import Game.Misc.BasicCollides;
import Game.Misc.Game;
import Game.Misc.TdWorld;
import Game.Misc.TurretGenerator;
import Game.Mobs.TdMob;
import GlobalUse.Data;
import GlobalUse.Description;
import GlobalUse.Log;
import GlobalUse.Util;
import org.joml.Vector2d;
import windowStuff.Audio;
import windowStuff.Audio.SoundToPlay;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import java.util.ArrayList;
import windowStuff.GraphicsOnly.Sprite.Sprite.FrameAnimation;
import windowStuff.GraphicsOnly.TransformAnimation;

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

    private final Explosive explosive = new Explosive(0, 0);

    @Override
    protected void extraStatsUpdate() {
        if (explosive != null) {
            explosive.damage = stats[ExtraStats.explodPower];
            explosive.setRadius((int) stats[ExtraStats.radius]);
        }
    }

    public Sprite badgeSprite;
    public Sprite monkeySprite;
    private final ArrayList<Modifier<Projectile>> physicalEffects=new ArrayList<>();
    private float skyShotStrength = 2000;
    private SoundToPlay sound = new SoundToPlay("pop",0.7f);

    private ImageData trailIm = Graphics.getImage("fire");
    private Trail trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(30,30).setRotation(r).
        playAnimation(new TransformAnimation(1).setOpacityScaling(-0.03f)).setDeleteOnAnimationEnd(true),2f, 50);

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
            p.addBuff(new SkyShot(skyShotStrength, getStats()[projectileDuration]/2f, 100,physicalEffects));
        });
        physicalEffects.add((Projectile target)-> {target.addMobCollide(BasicCollides.damage);});

        bulletLauncher.addProjectileModifier(p->Accuracy.mod(p, getStats()[ExtraStats.spread], getStats()[ExtraStats.spread]));

        world.getBs().addSprite(monkeySprite);
        move(X, Y);
        getImageUpdate();
        onStatsUpdate();

        bulletLauncher.addProjectileModifier(p->{
          Trail t = new Trail(trail, p.getX(), p.getY());
          p.addBuff(new OnTickBuff<>(t::tick));
          Audio.play(sound);
        });
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

    private int firingCycle = 0;
    private int bombsCount = 1;
    @Override
    protected Upgrade up100() {
        return new Upgrade("Bomb-0",
                new Description("Double Bombs"
                        ,
                        "Doubles... the bombs.",
                        "Genius."),
                () -> {
                    bombsCount=2;
                    bulletLauncher.addAttackEffect(bl->{
                      firingCycle++;
                      if (firingCycle>=bombsCount){
                        firingCycle=0;
                      }else{
                        bl.setRemainingCooldown(bl.getRemainingCooldown()-bl.getCooldown()*(1-0.15f/bombsCount));
                      }
                    });
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
                    bombsCount +=3;
                    addBuff(new StatBuff<Turret>(Type.MORE, Stats.bulletSize, 0.6f));
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, 0.65f));
                    sound=new SoundToPlay(sound.name, sound.volume-0.2f);
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
                    float attackArea = Util.square(originalStats[ExtraStats.spread] / 1000);
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
                    addBuff(new StatBuff<>(StatBuff.Type.ADDED, ExtraStats.explodPower, 1));
                    addBuff(new StatBuff<>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
                    addBuff(new StatBuff<>(Type.MORE, ExtraStats.radius, 1.5f));
                    addBuff(new StatBuff<>(Type.MORE, ExtraStats.spread, 2));
                    trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(50,50).setRotation(r).
                      playAnimation(new TransformAnimation(1).setOpacityScaling(-0.03f)).setDeleteOnAnimationEnd(true),1f, 50);
                    skyShotStrength = 3000;
                  sound=new SoundToPlay(sound.name, sound.volume+0.1f);
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
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, ExtraStats.explodPower, 1));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
                    float radiusBuff = 1 + originalStats[ExtraStats.spread] / 200f;
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, radiusBuff));
                    addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.spread, 1.5f));
                    trailIm=Graphics.getImage("bluRay");
                    trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(50,10).setRotation(r).
                      playAnimation(new TransformAnimation(1).setOpacityScaling(-0.02f)).setDeleteOnAnimationEnd(true),3f, 50);
                    skyShotStrength = 5500;
                  sound=new SoundToPlay(sound.name, sound.volume+0.1f);
                }, 300);
    }

  @Override
  protected Upgrade up030() {
    return new Upgrade("Bomb-0",
        new Description("Meteor"
            ,
            "Overkill",
            "Massively reduces fire rate and increases damage"),
        () -> {
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.explodPower, 10));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.bulletSize, 1.4f));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, 0.3f));
          addBuff(new StatBuff<Turret>(Type.MORE, projectileDuration, 5f));

          trailIm=Graphics.getImage("Explosion1-0");
          trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(250,250).setRotation(r-90).
              playAnimation(new FrameAnimation("Explosion1",1).and(new TransformAnimation(1).setOpacityScaling(-0.03f))).setDeleteOnAnimationEnd(true),
              20f, 100);
          skyShotStrength = 1000;
          sound=new SoundToPlay(sound.name, sound.volume+0.2f);
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
                    physicalEffects.add((Projectile target)-> {
                        target.addMobCollide((Projectile proj, TdMob mob)-> mob.addBuff(new Ignite<>(proj.getPower(), 2000)),0);
                    });
                    explosive.addPreEffect(mob -> mob.addBuff(new Ignite<>(this.stats[ExtraStats.explodPower], 2000)));
                }, 175);
    }

  @Override
  protected Upgrade up002() {
    return new Upgrade("laser",
        new Description("laser precision"
            ,
            "fire",
            ""),
        () -> {
          sound = new SoundToPlay("laser",sound.volume);
          addBuff(new StatBuff<>(Type.MORE, ExtraStats.spread, 0));
          addBuff(new StatBuff<>(Type.MORE, Stats.aspd, 1.8f));
          bulletLauncher.setImage("transparent");
          trailIm=Graphics.getImage("laser");
          skyShotStrength*=1.6f;
          trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(30,150).setRotation(r-90).
              playAnimation(new TransformAnimation(1).setOpacityScaling(-0.03f)).setDeleteOnAnimationEnd(true),25f, 0);
        }, 175);
  }


    @Override
    public void onGameTick(int tick) {
        if (notYetPlaced) {
            return;
        }
        bulletLauncher.tickCooldown();

        Vector2d mousePos =  Game.get().getUserInputListener().getPos();
        if(bulletLauncher.canAttack()) {
            float distance = (float) Util.distanceNotSquared(mousePos.x - getX(), mousePos.y - getY());
            addBuff(new StatBuff<>(StatBuff.Type.FINALLY_ADDED, speed,
                    -getStats()[speed] + distance / getStats()[projectileDuration]*Game.secondsPerFrame));
        }
        while (bulletLauncher.canAttack()) {
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
    stats[projectileDuration] = 1.5f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(50f, 80f);
    stats[speed] = 0;
    stats[Stats.cost] = 150f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.spread] = Data.gameMechanicsRng.nextFloat(0f, 2500f);
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
