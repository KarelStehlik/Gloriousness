package Game.Turrets;

import Game.BasicCollides;
import Game.Buffs.DelayedTrigger;
import Game.Buffs.Explosive;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.TankRockets;
import Game.BulletLauncher;
import Game.BulletLauncher.Cannon;
import Game.Game;
import Game.Projectile;
import Game.TdWorld;
import Game.TurretGenerator;
import Game.Turrets.EmpoweringTurret.ExtraStats;
import general.Data;
import general.Description;
import general.Util;
import org.joml.Vector2d;
import windowStuff.Sprite;
import windowStuff.Sprite.BasicAnimation;
import windowStuff.TextModifiers;

public class Mortar extends Turret {

  public static final String image = "mortar";
  private final Explosive<Projectile> explosive = new Explosive<>(0,0);

  @Override
  protected void extraStatsUpdate(){
    if(explosive!=null) {
      explosive.damage = stats[Stats.power];
      explosive.setRadius((int) stats[ExtraStats.radius]);
    }
  }

  public Mortar(TdWorld world, int X, int Y) {
    super(world, X, Y, image, new BulletLauncher(world, "drt"));
    bulletLauncher.addProjectileModifier(p->p.addBeforeDeath(this.explosive));
    bulletLauncher.addProjectileModifier(p->p.getSprite().playAnimation(p.getSprite().new BasicAnimation("Explosion1",this.getStats()[Stats.projectileDuration])));
    onStatsUpdate();
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, image, "Mortar",
        () -> new Mortar(world, -1000, -1000));
  }

  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    bulletLauncher.tickCooldown();

    Vector2d mousePos = Game.get().getUserInputListener().getPos();

    float rotation = Util.get_rotation((float) mousePos.x - x, (float) mousePos.y - y);

    while (bulletLauncher.canAttack()) {
      bulletLauncher.move(
          (float)mousePos.x + (Data.gameMechanicsRng.nextFloat()-0.5f)*stats[ExtraStats.spread],
          (float)mousePos.y + (Data.gameMechanicsRng.nextFloat()-0.5f)*stats[ExtraStats.spread]
      );
      bulletLauncher.attack(rotation, true);
    }
    setRotation(rotation);

    buffHandler.tick();
  }

  // generated stats
  @Override
  public int getStatsCount() {
    return 12;
  }

  @Override
  public void clearStats() {
    stats[Stats.power] = 2f;
    stats[Stats.range] = 0f;
    stats[Stats.pierce] = 0f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.4f, 0.9f);
    stats[Stats.projectileDuration] = 0.8f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(100f, 150f);
    stats[Stats.speed] = 0f;
    stats[Stats.cost] = 250f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.spread] = Data.gameMechanicsRng.nextFloat(10f, 150f);
    stats[ExtraStats.radius] = Data.gameMechanicsRng.nextFloat(100f, 150f);
  }

  public static final class ExtraStats {

    public static final int spread = 10;
    public static final int radius = 11;

    private ExtraStats() {
    }
  }
  // end of generated stats

}
