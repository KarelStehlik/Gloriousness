package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.StatBuff;
import Game.BulletLauncher;
import Game.TurretGenerator;
import Game.World;
import general.Data;
import general.Description;
import windowStuff.TextModifiers;

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
                    float aspdDebuf= (float) Math.sqrt( stats[Stats.speed]/19);
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
                    int pierceBuff=(int)(stats[Stats.speed]/19*10)+3;
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
        return new Upgrade("doubleshot", new Description( "Doubleshot","We've had one dart yes, but what about second dart?",
                "I don't think he knows about second dart"),
                () -> {
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
