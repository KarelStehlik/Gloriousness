package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.StatBuff;
import Game.BulletLauncher;
import Game.TurretGenerator;
import Game.World;
import general.Data;
import general.Description;

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
    protected Upgrade up010() {
        return new Upgrade("beefmen",
                new Description("Beefy darts"
                        ,"reduce attack speed with low dart speed, but increase pierce by two. Warning: If dartspeed is too low may not shoot at all",
                        "1-19 drtspeed, atcspd*=sqrt(drtspeed/19), if atcpd were halved or more it is set to 0"),
                () -> {
                    sprite.setImage("beefdrtmonk");
                    bulletLauncher.addProjectileModifier(p -> {
                        p.getSprite().setImage("beefdrt");
                    });
                    float aspdDebuf= (float) Math.sqrt( stats[Stats.speed]/19);
                    if(aspdDebuf<0.5){ //happens at 4 or less dartspeed
                        aspdDebuf=0;
                    }
                    addBuff(new StatBuff<Turret>(StatBuff.Type.INCREASED, Stats.bulletSize, 1.25f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.pierce, 2));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, aspdDebuf));
                }, 40);
    }
    @Override
    protected Upgrade up100() {
        return new Upgrade("spddart", new Description( "Quicker darts","Quicker darts, quicker monkey, increases dart speed and doubles attack speed",
                "increases dartspeed times 1.5"),
                () -> {
                    if(path2Tier<1){
                        sprite.setImage("drtmonkS");
                        bulletLauncher.addProjectileModifier(p -> {
                            p.getSprite().setImage("drtS");
                        });
                    }
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, 1.5f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 2));
                }, 40);
    }

    // generated stats
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
  }
  // end of generated stats

}
