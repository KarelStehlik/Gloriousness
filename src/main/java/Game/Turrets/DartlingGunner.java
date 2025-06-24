package Game.Turrets;

import Game.*;
import Game.Buffs.Explosive;
import Game.Buffs.Ignite;
import Game.Buffs.SideWeapon;
import Game.Buffs.StatBuff;
import Game.Mobs.TdMob;
import general.Data;
import general.Description;
import general.Util;
import org.joml.Vector2d;
import org.joml.Vector2f;
import windowStuff.TextModifiers;

import java.util.ArrayList;
import java.util.List;

public class DartlingGunner  extends Turret{
    public static final String image = "gunner";

    public DartlingGunner(World world, int X, int Y){
        super(world, X, Y, image, new BulletLauncher(world, "drt"));
        onStatsUpdate();
        bulletLauncher.setAspectRatio(1.5f);
        bulletLauncher.addMobCollide(BasicCollides.damage);
    }
    public static TurretGenerator generator(World world) {
        return new TurretGenerator(world, image, "Dartling Gunner", () -> new DartlingGunner(world, -1000, -1000));
    }
    @Override
    public void onGameTick(int tick) {
        if (notYetPlaced) {
            return;
        }
        bulletLauncher.tickCooldown();

        Vector2d mousePos=Game.get().getUserInputListener().getPos();
        float rotation=Util.get_rotation((float)mousePos.x - x, (float)mousePos.y - y);
        while (bulletLauncher.canAttack()) {
            bulletLauncher.attack(rotation,true);
        }
        setRotation(rotation);

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
        return new Upgrade("barrel",
                new Description( "Double barrel",
                        "Shoots darts from an additional barrel",
                "buffs work on the second barrel (apart from on attack effects that happen on no cooldown trigger I guess)"),
                () -> {
                    bulletLauncher.addAttackEffect(new SideWeapon(new Vector2f(0,100)));
                }, 40);
    }
    @Override
    protected Upgrade up200() {
        return new Upgrade("barrels", new Description( "Triple Barrel",
                "The greed for barrels is strong, two was never enough"),
                () -> {
            bulletLauncher.addAttackEffect(new SideWeapon(new Vector2f(-100,0)));
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
                }, 125);
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
                "duration of 4s, burn damage is equal to attackspeed, AOE is increased by 50%"),
                () -> {
                    sprite.setImage("bombsuit");
                    bulletLauncher.addProjectileModifier(p -> {
                        p.getSprite().setImage("drtbomb");
                    });
                    upgraded=true;

                }, 400);
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 2f;
    stats[Stats.range] = 15f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.9f,5f);
    stats[Stats.projectileDuration] = 4f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = Data.gameMechanicsRng.nextFloat(2f,25f);
    stats[Stats.cost] = 250f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
  }
  // end of generated stats

}
