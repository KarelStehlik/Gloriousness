package Game.Turrets;

import Game.*;
import Game.Buffs.Explosive;
import Game.Buffs.Ignite;
import Game.Buffs.Modifier;
import Game.Buffs.StatBuff;
import Game.Mobs.TdMob;
import general.Data;
import general.Description;
import general.Log;
import general.Util;
import windowStuff.TextModifiers;

import java.util.ArrayList;
import java.util.List;

public class DartMonkey extends Turret{
    public static final String image = "DartMonkey";

    public DartMonkey(World world, int X, int Y){
        super(world, X, Y, image, new BulletLauncher(world, "drt"));
        onStatsUpdate();
        bulletLauncher.setAspectRatio(1.5f);
        bulletLauncher.addMobCollide(BasicCollides.damage);
    }
    public static TurretGenerator generator(World world) {
        return new TurretGenerator(world, image, "DartMonkey", () -> new DartMonkey(world, -1000, -1000));
    }
    @Override
    public void onGameTick(int tick) {
        if (notYetPlaced) {
            return;
        }
        bulletLauncher.tickCooldown();
        if(bulletLauncher.canAttack()){
            ArrayList<TdMob> targets = target((int)getStats()[ExtraStats.maxTargets]);
            if(!targets.isEmpty()){
                List<Float> rotations=new ArrayList<>(targets.size());
                for(int i=0;i<targets.size();i++){
                    TdMob target=targets.get(i);
                    rotations.add(Util.get_rotation(target.getX() - x, target.getY() - y));
                }
                while (bulletLauncher.canAttack()) {
                    for (int i=0;i<rotations.size();i++) {
                        bulletLauncher.attack(rotations.get(i),i==0);
                    }
                }
                setRotation(rotations.get(0));
            }
        }else{
            TdMob target = target();
            if (target != null) {
                setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
            }
        }

        buffHandler.tick();
    }
    @Override
    protected Upgrade up010() {
        return new Upgrade("sharper",
                new Description("Sharper darts"
                        ,"Throws razor sharp darts that can pop 2 layers at once"),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
                }, 40);
    }
    @Override
    protected Upgrade up020() {
        return new Upgrade("beefmen",
                new Description("Beefy darts"
                        ,"reduce attack speed with low dart speed, but increase pierce by two. "+ TextModifiers.red +"Warning:"+ TextModifiers.white +" If dartspeed is too low may not shoot at all",
                        "1-19 drtspeed, atcspd*=sqrt(drtspeed/19), if atcpd were halved or more it is set to 0"),
                () -> {
                    if(getHighestTier()<2) {
                        sprite.setImage("beefdrtmonk");
                        bulletLauncher.addProjectileModifier(p -> {
                            p.getSprite().setImage("beefdrt");
                        });
                    }
                    float aspdDebuf= (float) Math.sqrt( originalStats[Stats.speed]/19);
                    if(aspdDebuf<0.5){ //happens at 4 or less dartspeed
                        aspdDebuf=0;
                    }
                    addBuff(new StatBuff<Turret>(StatBuff.Type.INCREASED, Stats.bulletSize, 0.5f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.pierce, 2));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, aspdDebuf));
                }, 40);
    }
    @Override
    protected Upgrade up030() {
        return new Upgrade("beefyer",
                new Description("Beefyer darts"
                        ,"increases pierce by up to 13 depending on dartspeed",
                        "1-19 drtspeed, pierce+=drtspeed/19*10+3"),
                () -> {
                    sprite.setImage("gorrila");
                    sprite.scale(1.4f,1.2f);
                    bulletLauncher.addProjectileModifier(p -> {
                        p.getSprite().setImage("beefyerdrt");
                    });
                    int pierceBuff=(int)(originalStats[Stats.speed]/19*10)+3;
                    addBuff(new StatBuff<Turret>(StatBuff.Type.INCREASED, Stats.bulletSize, 0.5f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.pierce, pierceBuff));
                }, 100);
    }
    @Override
    protected Upgrade up100() {
        return new Upgrade("spddart", new Description( "Quicker darts","Quicker darts, quicker monkey, increases dart speed and doubles attack speed",
                "increases dartspeed times 1.5"),
                () -> {
                    if(getHighestTier()<2){
                        sprite.setImage("drtmonkS");
                        bulletLauncher.addProjectileModifier(p -> {
                            p.getSprite().setImage("drtS");
                        });
                    }
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, 1.5f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 2));
                }, 40);
    }
    @Override
    protected Upgrade up200() {
        return new Upgrade("doubleshot", new Description( "Doubleshot",
                "We've had one dart yes, but what about second dart?",
                "I don't think he knows about second dart"),
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, ExtraStats.maxTargets, 1));
                }, 100);
    }
    private boolean upgraded=false;
    private void explodfunc(Projectile proj){
        if (upgraded){
            explodfuncUpgraded(proj);
            return;
        }
        proj.addBeforeDeath(
                new Explosive<Projectile>(1,120)
        );
    }
    @Override
    protected Upgrade up300() {
        return new Upgrade("rocketdart", new Description( "Rocket darts",
                "Darts explode, have increased projectile speed and deal more damage.",
                "they deal 1 more damage, 1 dmg explosions, 75 percent more speedy"),
                () -> {
                    sprite.setImage("cyborg");
                    bulletLauncher.addProjectileModifier(p -> {
                        p.getSprite().setImage("drtex");
                    });
                    bulletLauncher.addProjectileModifier(this::explodfunc);
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, 1.75f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
                }, 175);
    }
    private void explodfuncUpgraded(Projectile proj){
        Explosive<Projectile> explode=new Explosive<Projectile>(getStats()[Stats.pierce],180);
        explode.addEffect((TdMob mob)->mob.addBuff( new Ignite<>(originalStats[Stats.aspd],4*1000)));
        proj.addBeforeDeath(
                explode
        );
    }
    @Override
    protected Upgrade up400() {
        return new Upgrade("incendiary", new Description( "Incendiary darts",
                "Roasts bloons alive with extra large and powerful explosions, burn damage is better with attackspeed. " +
                        "Additionally sets base explosion damage to pierce.",
                "duration of 4s, burn damage is equal to base attackspeed, AOE is increased by 50%"),
                () -> {
                    sprite.setImage("bombsuit");
                    bulletLauncher.addProjectileModifier(p -> {
                        p.getSprite().setImage("drtbomb");
                    });
                    upgraded=true;

                }, 650);
    }

    // generated stats
  @Override
  public int getStatsCount() {
    return 11;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 250f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.45f,1.2f);
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = Data.gameMechanicsRng.nextFloat(1f,19f);
    stats[Stats.cost] = 25f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.maxTargets] = 1f;
  }

  public static final class ExtraStats {

    public static final int maxTargets = 10;

    private ExtraStats() {
    }
  }
  // end of generated stats

}
