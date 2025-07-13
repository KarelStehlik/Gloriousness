package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.Explosive;
import Game.Buffs.Modifier;
import Game.Buffs.ProcTrigger;
import Game.Buffs.StatBuff;
import Game.BulletLauncher;
import Game.Game;
import Game.Mobs.TdMob;
import Game.TdWorld;
import Game.TurretGenerator;
import general.Data;
import general.Description;
import general.RefFloat;
import general.Util;
import windowStuff.TextModifiers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Graphics;
import windowStuff.ImageData;

import static Game.Buffs.StatBuff.Type.MORE;
import static Game.Turrets.Engineer8.ExtraStats.originalTurretAspd;
import static Game.Turrets.Engineer8.ExtraStats.originalTurretSpeed;

public class Engineer8 extends Turret {



  @Override
  protected ImageData getImage(){
    return Graphics.getImage("engineer");
  }

  public static final String image = "engineer";
  private String turretImage="turret";

  private final BulletLauncher turretLauncher;

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
      turretPlaceTimer += Game.tickIntervalMillis * stats[Stats.aspd] * stats[Engineer8.ExtraStats.spawnSpd];
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
        return new Upgrade("turretmenu",  new Description("Double slits", "turrets have an additional firing slit to shoot two projectiles at once"),
                () -> {
                    if(turretImage.equals("turret")) {
                        turretImage = "turret2";
                    }
                    turretMods.add(t -> {
                        t.bulletLauncher.cannons.add(new BulletLauncher.Cannon(5,5));
                    });
                }, 50);
    }
    @Override
    protected Upgrade up001() {
        return new Upgrade("spannermen",  new Description("Spanner", "turrets are built faster and occasionally throws le spanner, " +
                "better spanner attack speed with turret projectile speed",
                "increases turret building speed by 80%,"+ TextModifiers.blue +"Spanner: "+TextModifiers.white+"Base attack speed of spanner is "+(int)(10f*getStats()[Stats.aspd])/10f+" every sec," +
                        "with "+getStats()[Stats.pierce]+" pierce, "+getStats()[Stats.power]+" damage and 69% chance for 6.9 times the cd." +
                        "The random penalty is then lowered by 1/19 for every turret dartspeed where 19 (max dartspeed) cancels it completely"),
                () -> {
                    sprite.setImage("engineer2");
                    bulletLauncher.setRemainingCooldown(turretLauncher.cooldown);
                    bulletLauncher.addAttackEffect(new ProcTrigger<BulletLauncher>(
                            launcher->{
                                launcher.setRemainingCooldown(
                                        launcher.cooldown
                                                *6.9f*(1-1/19f*getStats()[originalTurretSpeed])
                                );//is 6.9 because the cooldown is also added normally

                            }
                                                            ,0.69f));
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(MORE, ExtraStats.spawnSpd, 1.8f));
                    });

                }, 50);
    }
    @Override
    protected Upgrade up002() {
        return new Upgrade("gears",  new Description("Gears", "turrets shoot faster and their projectiles explude (in a smol area), " +
                "spanner damage is tripled",
                "increases turret attack speed by 25%; Explosion radius is smol. increases dartspeed by 80%"),
                () -> {
                    turretImage="tureet";
                    sprite.setImage("grmn");
                    addBuff(new StatBuff<Turret>(MORE, Stats.power, 3f));
                    turretMods.add(t -> {
                        t.addBuff(new StatBuff<Turret>(MORE,Stats.aspd,1.8f));
                        t.addBuff(new StatBuff<Turret>(MORE,Stats.speed,1.8f));
                        t.bulletLauncher.addProjectileModifier( p->p.addBeforeDeath( new Explosive(1f,35)));
                    });
                }, 175);
    }
    protected static float demonDamage =2;
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
    @Override
    protected Upgrade up200() {
        return new Upgrade("demoncore",  new Description("Demoncore",
                "",
                "Affects turrets and spanner."),
                () -> {
                    Explosive<Engineer8> explod=new Explosive<>(demonDamage,275);
                    RefFloat atcSpeedBuff=  new RefFloat(1);
                    explod.addPostEffect(mob->{
                        if(mob.WasDeleted()) {
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
    stats[ExtraStats.spawnSpd] = 0.6f;
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
