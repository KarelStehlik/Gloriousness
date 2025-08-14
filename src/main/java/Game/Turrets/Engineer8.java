package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.*;
import Game.BulletLauncher;
import Game.Game;
import Game.Mobs.TdMob;
import Game.TdWorld;
import Game.TurretGenerator;
import general.*;
import windowStuff.Sprite;
import windowStuff.TextModifiers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Graphics;
import windowStuff.ImageData;

import static Game.Buffs.StatBuff.Type.ADDED;
import static Game.Buffs.StatBuff.Type.MORE;
import static Game.Turrets.Engineer8.ExtraStats.originalTurretAspd;
import static Game.Turrets.Engineer8.ExtraStats.originalTurretSpeed;

public class Engineer8 extends Turret {


    @Override
    public void delete() {
        sprite.delete();
        buffHandler.delete();
        rangeDisplay.delete();
        for (Sprite sprit:alignmentAnimation){
            sprit.delete();
        }
    }
  @Override
  protected ImageData getImageUpdate(){
      String img;
      switch (path3Tier){
          case 0 ->
              img = "engineer";
          case 1->
              img = "engineer2";
          case 2,3,4->
              img="grmn";
          default->
              img="error, No Engi image";
      }
    return Graphics.getImage(img);
  }

  private final BulletLauncher turretLauncher;
  private int alignmentCd=Integer.MAX_VALUE;
  private List<Sprite> alignmentAnimation=new ArrayList<Sprite>(3);
  private float alignmentTimer=Integer.MAX_VALUE;
  private float alignmentDmg;
  private boolean alignment=false;

  private final List<Modifier<EngiTurret8>> turretMods = new ArrayList<>(1);

  public Engineer8(TdWorld world, int X, int Y) {

    super(world, X, Y, new BulletLauncher(world, "spanner"));
    onStatsUpdate();
    turretLauncher = new BulletLauncher(world, "nail");
    bulletLauncher.addMobCollide(BasicCollides.damage);
    bulletLauncher.setRemainingCooldown(Float.MAX_VALUE);
      turretMods.add(t -> {
                  t.addBuff(new StatBuff<Turret>(MORE, Stats.aspd, getStats()[originalTurretAspd]));
          t.addBuff(new StatBuff<Turret>(MORE, Stats.speed,getStats()[originalTurretSpeed]));
              }
      );
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, "engineer", "engineer", () -> new Engineer8(world, -1000, -1000));
  }

  private float turretPlaceTimer = 0;

  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
      bulletLauncher.tickCooldown();
      if(bulletLauncher.canAttack()) {
          TdMob target = world.getMobsGrid().search(
                  new Point((int) x, (int) y), 3500, targeting);
          if (target != null) {
              float enemyRotat=Util.get_rotation(target.getX() - x, target.getY() - y);
              while (bulletLauncher.canAttack()) {
                  bulletLauncher.attack(enemyRotat);
              }
          }
      }
      turretPlaceTimer += Game.tickIntervalMillis * stats[Engineer8.ExtraStats.spawnSpd];
      while (turretPlaceTimer >= 1000) {
          turretPlaceTimer -= 1000;
          float dist = (float) Math.sqrt(
                  Data.gameMechanicsRng.nextDouble(Util.square(stats[Stats.range])));
          float angle = Data.gameMechanicsRng.nextFloat(360);
          EngiTurret8 t = new EngiTurret8(world,
                  (int) (x + dist * Util.cos(angle)),
                  (int) (y + dist * Util.sin(angle)),
                  turretLauncher);
          t.path1Tier=path1Tier;
          t.path2Tier=path2Tier;
          t.path3Tier=path3Tier;
          t.sprite.setImage(t.getImageUpdate());
          t.place();
          turretMods.forEach(m -> m.mod(t));
      }
      buffHandler.tick();
      if(alignment){
          alignmentTimer-=Game.tickIntervalMillis;
          for(int i=1;i<alignmentAnimation.size();i++){
              alignmentAnimation.get(i).setRotation(180*alignmentTimer/alignmentCd*i);
          }
          if(alignmentTimer<0){
              alignmentTimer=alignmentCd;
              bulletLauncher.setRemainingCooldown(0);
              addBuff(new StatBuff<Turret>(MORE,5_000, Stats.aspd, alignmentDmg));
              addBuff(new StatBuff<Turret>(MORE,2_000, ExtraStats.spawnSpd, alignmentDmg));
          }

      }

  }
    private RefInt turretCount = new RefInt(0);
    private RefInt minTurrets;
    @Override
    protected Upgrade up001() {
        return new Upgrade("maintenance",  new Description("Persistent maintenace", "turrets get stronger over time and expire slower when there are 3 or less",
                "turrets get double pierce after 80% of lifespan and double damage after 200% of lifespan with visual indicators. expire 95% slower"),
                () -> {
                    minTurrets=new RefInt(3);
                    turretMods.add(t -> {
                        t.addBuff(new DelayedTrigger<Turret>(Integer.MAX_VALUE, (Turret m) -> {
                            turretCount.add(-1);
                        }, true));
                        turretCount.add(1);
                        RefFloat currentIncrease = new RefFloat(0);
                        RefInt aliveturrets = new RefInt(0);
                        t.addBuff(new DelayedTrigger<Turret>(t.getStats()[EngiTurret8.ExtraStats.duration]*0.8f,
                                (Turret turret)->{
                            turret.addBuff(new StatBuff<Turret>(MORE, Stats.pierce, 2f));
                            turret.scale(1.25f,1.25f);
                            turret.addBuff(new StatBuff<Turret>(MORE, Stats.bulletSize, 1.25f));
                        },false));
                        t.addBuff(new DelayedTrigger<Turret>(t.getStats()[EngiTurret8.ExtraStats.duration]*2f,
                                (Turret turret)->{
                                    turret.addBuff(new StatBuff<Turret>(MORE, Stats.power, 2f));
                                    turret.scale(1.25f,1.25f);
                                    turret.addBuff(new StatBuff<Turret>(MORE, Stats.bulletSize, 1.25f));
                                },false));
                        t.addBuff(new OnTickBuff<Turret>(turret -> {
                            if(turretCount.get()<=minTurrets.get()){
                                t.addBuff(new StatBuff<Turret>(ADDED, EngiTurret8.ExtraStats.duration, Game.tickIntervalMillis*0.95f));
                            }
                        }));
                    });
                }, 40);
    }
    @Override
    protected Upgrade up002() {
        return new Upgrade("spannermen",  new Description("Spanner", "Can maintain up to 6 turrets, increases turret build speed by 100% and occasionally throws le spanner, " +
                "better spanner attack speed with turret projectile speed",
                "100% more I don't like increased but I like the word. "+TextModifiers.blue +"Spanner: "+TextModifiers.white+"Base attack speed of spanner is "+(int)(10f*getStats()[Stats.aspd])/10f+" every sec," +
                        "with "+getStats()[Stats.pierce]+" pierce, "+getStats()[Stats.power]+" damage and 69% chance for 6.9 times the cd." +
                        "The random penalty is then lowered by 1/19 for every turret dartspeed where 19 (max dartspeed) cancels it completely"),
                () -> {
                    addBuff(new StatBuff<Turret>(MORE, ExtraStats.spawnSpd, 2));
                    bulletLauncher.setRemainingCooldown(turretLauncher.cooldown);
                    bulletLauncher.addAttackEffect(new ProcTrigger<BulletLauncher>(
                            launcher->{
                                launcher.setRemainingCooldown(
                                        launcher.cooldown
                                                *6.9f*(1-1/19f*getStats()[originalTurretSpeed])
                                );//is 6.9 because the cooldown is also added normally

                            }
                            ,0.69f));
                    minTurrets.set(5);

                }, 120);
    }
    @Override
    protected Upgrade up010() {
        return new Upgrade("turretmenu",  new Description("Double slits", "turrets have an additional firing slit to shoot two projectiles at once"),
                () -> {
                    turretMods.add(t -> {
                        t.bulletLauncher.cannons.add(new BulletLauncher.Cannon(5,5));
                    });
                }, 60);
    }
    @Override
    protected Upgrade up020() {
        return new Upgrade("timemen",  new Description("Sands of time", "Bloons hit go backwards, doesn't affect moabs"),
                () -> {
                    bulletLauncher.addMobCollide((proj, mob) -> {
                        if(mob.isMoab()){
                            return true;
                        }
                          mob.addProgress(-1);
                          return true;
                        },0);
                    turretMods.add(t -> {
                        t.bulletLauncher.addMobCollide((proj, mob) -> {
                            if(mob.isMoab()){
                                return true;
                            }
                            mob.addProgress(-1);
                            return true;
                        },0);
                    });

                    }, 80);
    }
    @Override
    protected Upgrade up030() {
        return new Upgrade("gears",  new Description("Gears", "turrets shoot faster and their projectiles explude (in a smol area), " +
                "spanner damage is tripled",
                "increases turret attack speed by 25%; Explosion radius is smol. increases dartspeed by 80%"),
                () -> {
                    addBuff(new StatBuff<Turret>(MORE, Stats.power, 3f));
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(MORE,Stats.aspd,1.8f));
                        t.addBuff(new StatBuff<Turret>(MORE,Stats.speed,1.8f));
                        t.bulletLauncher.addProjectileModifier( p->p.addBeforeDeath( new Explosive(1f,35)));
                    });
                }, 175);
    }
    @Override
    protected Upgrade up040() {
        return new Upgrade("timermen",  new Description("Overtime",
                "Occasionally goes turbo. Increased power with additional turret dartspeed, has up to 1 big + 5 small arrows",
                "Affects turrets and spanner. " +
                        "multiply turret spawn speed for 2s  and spanner attackspeed for 5s every 32s by" +
                        " 4 times per arrow (additional arrow count is (turretdartspeed-2)/4 rounded up) "),
                () -> {
                    alignment=true;
                    alignmentCd=32*1000;
                    alignmentTimer=alignmentCd;
                    alignmentDmg=4;
                    int animationposY=100;
                    var bs = Game.get().getSpriteBatching("main");
                    Sprite base=new Sprite("timercentre", 15).setPosition(getX(), getY()+animationposY).addToBs(bs).setSize(100, 100);
                    alignmentAnimation.add(base);
                    Sprite corner = new Sprite("timercorner", 18).setPosition(getX(), getY()+animationposY).addToBs(bs).setSize(110, 110);
                    Sprite arrow = new Sprite("timerarrow", 19).setPosition(getX(), getY()+animationposY).addToBs(bs).setSize(140, 140);
                    alignmentAnimation.add(corner);
                    alignmentAnimation.add(arrow);
                    for(int i=2;i<getStats()[originalTurretSpeed];i+=4) {
                        alignmentDmg+=4;
                        corner = new Sprite("timercorner", 16).setPosition(getX(), getY()+animationposY).addToBs(bs).setSize(100, 100);
                        arrow = new Sprite("timerarrow", 17).setPosition(getX(), getY()+animationposY).addToBs(bs).setSize(110, 110);
                        alignmentAnimation.add(corner);
                        alignmentAnimation.add(arrow);
                    }

                }, 350);
    }
    protected static float demonDamage =2;
    private int bloonsAbsorbed =0;
    @Override
    protected Upgrade up100() {
        return new Upgrade("demonsprout",  new Description("Demonsprout",
                TextModifiers.red+"Warning: Triggers only once, immediately after the upgrade is bought.\n"+TextModifiers.resetColor+
                "Uses demonic energy to absorb life of nearby bloons, " +
                "deals "+ demonDamage +" damage to all nearby bloons and gains 5% attack speed per bloon killed (reduced to 1% after 20 bloons). " +
                        "Permanently increases the damage of this upgrade by 1. ",
                "Affects turrets and spanner."),
                () -> {
                    Explosive<Engineer8> explod=new Explosive<>(demonDamage,275);
                    RefFloat atcSpeedBuff=  new RefFloat(1);
                    explod.addPostEffect(mob->{
                        if(mob.WasDeleted()) {
                            bloonsAbsorbed++;
                            if (atcSpeedBuff.get() < 2) {
                                atcSpeedBuff.add(0.05f);
                            } else {
                                atcSpeedBuff.add(0.01f);
                            }
                        }
                    });
                    explod.mod(this);
                    demonDamage+=1;
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, atcSpeedBuff.get()));
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, atcSpeedBuff.get()));
                    });

                }, 75);
    }
    private boolean demonSpeed(TdMob mob) {
        float speed = 1f;
        if(mob.isMoab()){
            speed/=20;
        }
        mob.addBuff(new StatBuff<TdMob>(StatBuff.Type.INCREASED, TdMob.Stats.speed, speed));
        mob.addBuff(new StatBuff<TdMob>(StatBuff.Type.INCREASED, TdMob.Stats.damageTaken, speed));
        return true;
    }
    @Override
    protected Upgrade up200() {
        return new Upgrade("greed",  new Description("Power siphon",
                "pumps turrets full of demonic power, increasing their pierce by 1. They shoot on death proportional to the amount of bloons absorbed by demonsprout.",
                ""),
                () -> {
                    turretMods.add(turret -> {
                        turret.addBuff(new DelayedTrigger<Turret>(Integer.MAX_VALUE, (Turret m) -> {
                            for(int i = 0; i<= bloonsAbsorbed; i+=2*i+2){
                                m.bulletLauncher.attack(Data.gameMechanicsRng.nextFloat(0, 360));
                            }
                        }, true));
                        turret.addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.pierce, 1));
                    });
                    turretLauncher.addProjectileModifier(p -> {
                        p.setAspectRatio(2);
                            }

                    );
                }, 80);
    }
    @Override
    protected Upgrade up300() {
        return new Upgrade("demoncore",  new Description("Demoncore",
                "Bloons hit are unstable, becoming extra fast but taking additional damage for every hit",
                "Affects turrets and spanner. Adds 1*damage taken and speed, 20 times reduced for moabs"),
                () -> {
                    bulletLauncher.addMobCollide((p, m) -> demonSpeed(m));
                    turretLauncher.addMobCollide((p, m) -> demonSpeed(m));
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
                        t.addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.pierce, 3));
                    });
                }, 300);
    }
    @Override
    protected void sell() {
        super.sell();
        if(minTurrets!=null){
            minTurrets.set(0);
        }

    }

    // generated stats
  @Override
  public int getStatsCount() {
    return 13;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 2f;
    stats[Stats.range] = 250f;
    stats[Stats.pierce] = 7f;
    stats[Stats.aspd] = 0.5f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = 15f;
    stats[Stats.cost] = 75f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.spawnSpd] = Data.gameMechanicsRng.nextFloat(0.2f, 1f);
    stats[ExtraStats.originalTurretAspd] = Data.gameMechanicsRng.nextFloat(0.45f, 1.2f);
    stats[ExtraStats.originalTurretSpeed] = Data.gameMechanicsRng.nextFloat(1f, 19f);
  }

  public static final class ExtraStats {

    public static final int spawnSpd = 10;
    public static final int originalTurretAspd = 11;
    public static final int originalTurretSpeed = 12;

    private ExtraStats() {
    }
  }
  // end of generated stats
}
