package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.UniqueBuff;
import Game.BulletLauncher;
import Game.DamageType;
import Game.Mobs.TdMob;
import Game.Mobs.TdMob.Stats;
import Game.Projectile;
import Game.TurretGenerator;
import Game.World;
import general.Data;
import general.RefFloat;
import general.Util;
import java.awt.Point;
import java.util.List;
import windowStuff.Sprite;

public class EmpoweringTurret extends Turret {

  public static final String image = "EmpoweringTower";

  public EmpoweringTurret(World world, int X, int Y) {
    super(world, X, Y, image,
        new BulletLauncher(world, "Buff"));
    onStatsUpdate();
    bulletLauncher.addProjectileCollide(this::collide);
    bulletLauncher.addProjectileModifier(p -> p.addBuff(new UniqueBuff<>(id, p1 -> {
    })));
  }

  public static TurretGenerator generator(World world) {
    return new TurretGenerator(world, image, "Empowering",
        () -> new EmpoweringTurret(world, -1000, -1000));
  }

  private static void addBuff(Projectile p2, float pow) {
    p2.addMobCollide(
        (proj2, mob) -> BasicCollides.explodeFunc((int) proj2.getX(), (int) proj2.getY(), pow,
            pow));
  }


  private boolean collide(Projectile p1, Projectile p2) {
    final float pow = p1.getPower();
    p2.addBuff(new UniqueBuff<>(id, proj2 -> addBuff(proj2, pow)));
    return true;
  }


  RefFloat assaCooldown=new RefFloat(0);
  @Override
  protected Upgrade up010() {
    return new Upgrade("Button", () -> "Hires edgy assassins to destroy nearby MOABs",
        () -> addBuff(new OnTickBuff<Turret>(turr->{
          assaCooldown.add(-1);
          while(assaCooldown.get()<0){
            assaCooldown.add(stats[Stats.cd]*.7f);
            var mob = world.getMobsGrid().getStrong(new Point((int) x, (int) y), (int)stats[Stats.range]);
            if(mob!=null){
              float angle = Data.unstableRng.nextFloat()*360;
              Sprite assa = new Sprite("Assassin",1).setPosition(-1000,-1000).addToBs(world.getBs()).setSize(200,200).setRotation(angle);
              mob.addBuff(new DelayedTrigger<TdMob>(1000, m -> {
                m.takeDamage(stats[Stats.power]*5000, DamageType.TRUE);
                assa.delete();
                world.explosionVisual(m.getX(),m.getY(),70,true,"Explosion1-0");
                },true));
              mob.addBuff(new OnTickBuff<TdMob>(1000,
                  m->assa.setPosition(
                      m.getX()+m.getStats()[TdMob.Stats.size]*.4f* Util.sin(-angle)
                      , m.getY()+m.getStats()[TdMob.Stats.size]*.4f*Util.cos(angle)
                  ),
                  false));
            }
          }
        })), 50000);
  }


  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 100f;
    stats[Stats.range] = 500f;
    stats[Stats.pierce] = 1f;
    stats[Stats.cd] = 1000f;
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = 50f;
    stats[Stats.speed] = 8f;
    stats[Stats.cost] = 1000f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
