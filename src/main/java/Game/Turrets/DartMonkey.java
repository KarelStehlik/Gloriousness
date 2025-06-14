package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.StatBuff;
import Game.BulletLauncher;
import Game.TurretGenerator;
import Game.World;

public class DartMonkey extends Turret{
    public static final String image = "DartMonkey";

    public DartMonkey(World world, int X, int Y){
        super(world, X, Y, image, new BulletLauncher(world, "Dart"));
        onStatsUpdate();
        bulletLauncher.addMobCollide(BasicCollides.damage);
    }
    public static TurretGenerator generator(World world) {
        return new TurretGenerator(world, image, "DartMonkey", () -> new DartMonkey(world, -1000, -1000));
    }
    @Override
    protected Upgrade up010() {
        return new Upgrade("Dart", () -> "beefy darts",
                () -> {
                    addBuff(new StatBuff<Turret>(StatBuff.Type.INCREASED, Stats.bulletSize, 2f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.power, 3f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.pierce, 3f));
                    addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, .7f));
                }, 40);
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 1f;
    stats[Stats.range] = 250f;
    stats[Stats.pierce] = 1f;
    stats[Stats.aspd] = 2.1f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 30f;
    stats[Stats.speed] = 10f;
    stats[Stats.cost] = 25f;
    stats[Stats.size] = 45f;
    stats[Stats.spritesize] = 100f;
  }
  // end of generated stats

}
