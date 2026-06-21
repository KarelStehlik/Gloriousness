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
import Game.Common.Projectile;
import Game.Misc.BasicCollides;
import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.Misc.TurretGenerator;
import GlobalUse.Data;
import GlobalUse.Description;
import GlobalUse.Util;
import org.joml.Vector2d;
import windowStuff.Audio;
import windowStuff.Audio.SoundToPlay;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import java.util.ArrayList;
import windowStuff.GraphicsOnly.Sprite.Sprite.FrameAnimation;
import windowStuff.GraphicsOnly.Text.TextModifiers;
import windowStuff.GraphicsOnly.TransformAnimation;

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

    boolean improvedEcplosives=false;
    @Override
    protected void extraStatsUpdate() {
        if(convertFire){
            if(stats[Stats.pierce]>2){
                addBuff(new StatBuff<>(Type.FINALLY_ADDED, Stats.pierce, -(stats[Stats.pierce]-2)));
                addedFire+=(stats[Stats.pierce]-2)/2;
            }
        }
        if (explosive != null) {
            explosive.damage = stats[ExtraStats.explodPower];
            explosive.setRadius((int) stats[ExtraStats.radius]);
            if(improvedEcplosives){
                explosive.damage+=stats[Stats.power]-1;
            }
        }
        if (clusterLauncher != null) {
            updateCluster();
        }

    }

    private final Sprite badgeSprite;
    private final Sprite monkeySprite;
    private final ArrayList<Modifier<Projectile>> physicalEffects=new ArrayList<>(1);

    private SoundToPlay sound = new SoundToPlay("pop",0.7f);

    private ImageData trailIm = Graphics.getImage("fire");
    private Trail trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(30,30).setRotation(r).
        playAnimation(new TransformAnimation(1).setOpacityScaling(-0.03f)).setDeleteOnAnimationEnd(true),60f, 50);

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
        addBuff(new StatBuff<>(Type.MORE, ExtraStats.radius, getStats()[Stats.bulletSize]/40f));
        originalStats[ExtraStats.radius]=stats[ExtraStats.radius];

        addBuff(new StatBuff<>(Type.MORE, Stats.pierce, getStats()[Stats.speed]/15f));
        originalStats[Stats.pierce]=stats[Stats.pierce];

        addBuff(new StatBuff<>(Type.MORE, ExtraStats.spread, getStats()[ExtraStats.spread]));
        originalStats[ExtraStats.spread]=stats[ExtraStats.spread];

        bulletLauncher.addProjectileModifier(p -> p.addBuff(new SkyShot(100, physicalEffects)));
        physicalEffects.add((Projectile target)-> target.addMobCollide(BasicCollides.damage));

        bulletLauncher.addProjectileModifier(p->Accuracy.mod(p, getStats()[ExtraStats.spread], getStats()[ExtraStats.spread]));

        world.getBs().addSprite(monkeySprite);
        move(X, Y);
        getImageUpdate();
        extraStatsUpdate();
        onStatsUpdate();

        bulletLauncher.addProjectileModifier(p->{
          Trail t = new Trail(trail, p.getX(), p.getY());
          p.addBuff(new OnTickBuff<>(t::tick));
          p.addBuff(new DelayedTrigger<>(t::tick, true));
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
    private float volleyFireDuration=0.15f;
    @Override
    protected Upgrade up100() {
        return new Upgrade("Bomb-0",
                new Description("Double Bombs"
                        ,
                        "Cost dependent on projectile size, if the projectiles are really cheap monkey HQ buys 2 additional projectiles instead.",
                        "Cost directly proportional to projectile size. When buying two bombs the upgrade is twice as expensive."),
                () -> {
                    if(originalStats[Stats.bulletSize]<=45)
                        bombsCount=3;
                    else
                        bombsCount=2;
                    bulletLauncher.addAttackEffect(bl->{
                      firingCycle++;
                      if (firingCycle>=bombsCount){
                        firingCycle=0;
                          bl.setRemainingCooldown(bl.getRemainingCooldown()-bl.getCooldown()*(volleyFireDuration));
                      }else{
                        bl.setRemainingCooldown(bl.getRemainingCooldown()-bl.getCooldown()*(1-volleyFireDuration/bombsCount));
                      }
                    });
                },originalStats[Stats.bulletSize]<=45 ? ((int)(originalStats[Stats.bulletSize]*3f)):((int)(originalStats[Stats.bulletSize]*1.5f)));
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
                    addBuff(new StatBuff<>(Type.MORE, Stats.bulletSize, 0.6f));
                    addBuff(new StatBuff<>(Type.MORE, ExtraStats.radius, 0.65f));
                    sound=new SoundToPlay(sound.name, sound.volume-0.2f);
                }, 300);
    }

    @Override
    protected Upgrade up300() {
        return new Upgrade("Bomb-0",
                new Description("Glorious Coverage"
                        ,
                        "Increases attack speed based on spread, better with high spread."+ TextModifiers.red+" WARNING: if spread is low reduces attack speed",
                        ""),
                () -> {
                    float attackArea = Util.square(originalStats[ExtraStats.spread] / 700);
                    addBuff(new StatBuff<>(Type.MORE, Stats.aspd, attackArea));
                }, 300);
    }

    private final float sizeIncrease010=1.4f;
    @Override
    protected Upgrade up010() {
        return new Upgrade("Bomb-0",
                new Description("Larger Shells"
                        ,
                        "Increases projectile size and explosion radius; adds pierce based on projectile size and damage with high projectile speed.",
                        "size and radius increased by "+(int)((sizeIncrease010-1)*100)+" percent. pierce is up to 2.5x and damage up +2"),
                () -> {
                    addBuff(new StatBuff<>(StatBuff.Type.MORE, Stats.bulletSize, 1.4f));
                    addBuff(new StatBuff<>(Type.MORE, ExtraStats.radius, 1.4f));

                    addBuff(new StatBuff<>(StatBuff.Type.MORE, Stats.pierce, originalStats[Stats.bulletSize]/28));
                    if(originalStats[Stats.speed]>25){
                        int addedDamage=1;
                        if(originalStats[Stats.speed]>34){
                            addedDamage+=1;
                        }
                        addBuff(new StatBuff<>(Type.ADDED, Stats.power, addedDamage));
                    }

                    trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(50,50).setRotation(r).
                      playAnimation(new TransformAnimation(1).setOpacityScaling(-0.03f)).setDeleteOnAnimationEnd(true),1f, 50);
                    addBuff(new StatBuff<>(Type.MORE, Stats.speed, 1.3f));
                    sound=new SoundToPlay(sound.name, sound.volume+0.1f);
                }, 75);
    }

    @Override
    protected Upgrade up020() {
        return new Upgrade("Bomb-0",
                new Description("Chaotic Bombing"
                        ,
                        "No need to he accurate if a miss is lethal enough",
                        "further increases explosion size based on spread"),
                () -> {
                    addBuff(new StatBuff<>(StatBuff.Type.ADDED, ExtraStats.explodPower, 1));
                    addBuff(new StatBuff<>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
                    float radiusBuff = 1 + originalStats[ExtraStats.spread] / 1000f;
                    addBuff(new StatBuff<>(Type.MORE, ExtraStats.radius, radiusBuff));
                    addBuff(new StatBuff<>(Type.MORE, ExtraStats.spread, 1.5f));
                    trailIm=Graphics.getImage("bluRay");
                    trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(50,10).setRotation(r).
                      playAnimation(new TransformAnimation(1).setOpacityScaling(-0.02f)).setDeleteOnAnimationEnd(true),3f, 50);
                  addBuff(new StatBuff<>(Type.MORE, Stats.speed, 1.8f));
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
          addBuff(new StatBuff<>(Type.MORE, ExtraStats.explodPower, 10));
          addBuff(new StatBuff<>(StatBuff.Type.MORE, Stats.bulletSize, 1.4f));
          addBuff(new StatBuff<>(Type.MORE, Stats.aspd, 0.3f));
          addBuff(new StatBuff<>(Type.MORE, Stats.projectileDuration, 5f));

          trailIm=Graphics.getImage("Explosion1-0");
          trail=new Trail(world.getBs(), r ->new Sprite(trailIm,3).setSize(250,250).setRotation(r-90).
              playAnimation(new FrameAnimation("Explosion1",1).and(
                  new TransformAnimation(1).setOpacityScaling(-0.03f)))
              .setDeleteOnAnimationEnd(true),
              20f, 100);
          addBuff(new StatBuff<>(Type.MORE, Stats.speed, 3f));
          sound=new SoundToPlay(sound.name, sound.volume+0.2f);
        }, 300);
  }
    private float getFireDmg(){
        float fireDamage= this.stats[ExtraStats.explodPower];
        fireDamage+=addedFire;
        if(improvedFire){
            fireDamage*=2;
        }
        return fireDamage;
    }
    private boolean improvedFire=false;
    @Override
    protected Upgrade up001() {
        return new Upgrade("Fireball-0",
                new Description("Glorious Flames"
                        ,
                        "Increases damage of the explosion based on projectile damage and adds fire",
                        "increases explosion damage by (projectile damage -1)."),
                () -> {
                    improvedEcplosives=true;
                    extraStatsUpdate();
                    physicalEffects.add((Projectile target)-> target.addMobCollide((proj, mob)->
                        mob.addBuff(new Ignite<>(getFireDmg(), 2000)),0));
                    explosive.addPreEffect(mob -> mob.addBuff(new Ignite<>(getFireDmg(), 2000)));
                }, 125);
    }
    private BulletLauncher clusterLauncher;
    private Explosive fireballExplod;
    private void ClusterAttack(float x, float y) {
        clusterLauncher.move(x, y);
        clusterLauncher.attack(0, false);
    }

    private void updateCluster() {
        clusterLauncher.setPower(stats[Turret.Stats.power]*0.5f);
        clusterLauncher.setSize(stats[Turret.Stats.bulletSize] * 0.2f+20);
        fireballExplod.damage=stats[ExtraStats.explodPower]/2;
        fireballExplod.setRadius((int)stats[ExtraStats.radius]/4);
    }
    @Override
    protected Upgrade up002() {
        return new Upgrade("Fireball-0",
                new Description("Flame cascade"
                        ,
                        "Explosions spread tiny fireballs, creates more with high spread. doubles fire damage",
                        ""),
                () -> {
                    improvedFire=true;
                    clusterLauncher = new BulletLauncher(world, "fire");
                    clusterLauncher.setDuration(0.09f);
                    clusterLauncher.cannons = BulletLauncher.radial(
                            (int) (Math.ceil(originalStats[ExtraStats.spread]/180+3)));
                    clusterLauncher.addProjectileModifier((Projectile p)->p.setRotation((float)(Math.random()*360)));
                    clusterLauncher.addProjectileModifier((Projectile p)->p.setSpeed((float)(p.getSpeed()+Math.random()*p.getSpeed())));
                    clusterLauncher.addMobCollide(BasicCollides.damage);
                    clusterLauncher.setSpeed(25);
                    clusterLauncher.setPierce(1);
                    fireballExplod=new Explosive(1,1);
                    clusterLauncher.addProjectileModifier(p -> p.addBeforeDeath(this.fireballExplod));

                    clusterLauncher.addProjectileModifier((Projectile target)-> target.addMobCollide((proj, mob)->
                            mob.addBuff(new Ignite<>(getFireDmg()/5, 2000)),0));
                    fireballExplod.addPreEffect(mob -> mob.addBuff(new Ignite<>(getFireDmg()/5, 2000)));

                    bulletLauncher.addProjectileModifier(
                            p -> p.addBeforeDeath(proj -> this.ClusterAttack(proj.getX(), proj.getY())));
                    extraStatsUpdate();
                }, 350);
    }

    private boolean convertFire=false;
    private float addedFire=0;
  @Override
  protected Upgrade up003() {
    return new Upgrade("laser",
        new Description("Searing laser"
            ,
            "laser precision, more fire, more fire rate less pierce",
            "kind of complicated, quite possibly op, converts pierce to fire damage and increases fire damage"),
        () -> {
          sound = new SoundToPlay("laser",sound.volume);
          addBuff(new StatBuff<>(Type.MORE, ExtraStats.spread, 0));
          addBuff(new StatBuff<>(Type.MORE, Stats.aspd, 2f));
          bulletLauncher.setImage("transparent");
          trailIm=Graphics.getImage("laser");
          convertFire=true;
          addBuff(new StatBuff<>(Type.MORE, Stats.speed, 1.6f));
          addBuff(new StatBuff<>(Type.MORE, Stats.projectileDuration, 0.3f));
          trail=new Trail(world.getBs(), r -> new Sprite(trailIm,3).setSize(30,30).setRotation(r-90).
              playAnimation(new TransformAnimation(1).setOpacityScaling(-0.03f)).setDeleteOnAnimationEnd(true),30f, 0);
        }, 777);
  }


    @Override
    public void onGameTick(int tick) {
        if (notYetPlaced) {
            return;
        }
        bulletLauncher.tickCooldown();

        Vector2d mousePos =  Game.get().getUserInputListener().getPos();

        while (bulletLauncher.canAttack()) {
            Projectile proj = bulletLauncher.attack((float) mousePos.x, (float) mousePos.y, true);
            float dx = (float) mousePos.x-x;
            float dy = (float) mousePos.y-y;
            float duration = stats[Stats.projectileDuration]*60;
            proj.accelerate(dx/duration, dy/duration);
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
    stats[Stats.pierce] = 6f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.4f, 0.8f);
    stats[Stats.projectileDuration] = 1.25f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(35f, 70f);
    stats[Stats.speed] = Data.gameMechanicsRng.nextFloat(15f, 40f);
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.spread] = Data.gameMechanicsRng.nextFloat(0f, 50f);
    stats[ExtraStats.radius] = 40f;
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
