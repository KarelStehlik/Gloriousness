package Game.Turrets;

import Game.BasicCollides;
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
import general.Data;
import general.Description;
import general.Util;
import java.util.List;
import org.joml.Vector2d;
import windowStuff.Audio;
import windowStuff.Audio.SoundToPlay;
import windowStuff.Graphics;
import windowStuff.ImageData;
import windowStuff.SingleAnimationSprite;
import windowStuff.Sprite;
import windowStuff.TextModifiers;
import Game.Animation;

public class DartlingGunner extends Turret {

  @Override
  protected ImageData getImage(){
    String prefix = switch (path1Tier) {
      case 0 -> "Gunner";
      case 1 -> "doubleGunner";
      case 2 -> "tripleGunner";
      case 3 -> "tank";
      default -> "error";
    };
    String suffix = switch(path2Tier){
      case 0, 1 ->"";
      case 2->"Wide";
      case 3,4->"Jugg";
      default->"error";
    };
    if(suffix.equals("Jugg")){
      prefix = "";
    }
    return Graphics.getImage(prefix+suffix);
  }

  private float barrelLength = 82;
  private float barrelExplosionSize = 60;

  private SoundToPlay sound = new SoundToPlay("gunshot", 0.5f);

  public DartlingGunner(TdWorld world, int X, int Y) {
    super(world, X, Y, new BulletLauncher(world, "drt"));
    float aspdBuff = (float) Math.pow(Data.gameMechanicsRng.nextFloat(1, (float) Math.sqrt(2)), 2);
    originalStats[Stats.aspd] *= aspdBuff;
    getStats()[Stats.aspd] *= aspdBuff;
    onStatsUpdate();
    bulletLauncher.setAspectRatio(1.5f);
    bulletLauncher.addMobCollide(BasicCollides.damage);

    final List<ImageData> boom = Graphics.getAnimation("Explosion1");
    final float duration=0.2f;
    bulletLauncher.addProjectileModifier(p->{
      world.getBs().addSprite(new SingleAnimationSprite(boom,duration,21).
          setPosition(p.getX(),p.getY()).setSize(barrelExplosionSize,barrelExplosionSize).
          setRotation(p.getRotation()));
      Audio.play(sound);
    });

    bulletLauncher.cannons.clear();
    bulletLauncher.cannons.add(new Cannon(0, barrelLength));
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, "Gunner", "Dartling Gunner",
        () -> new DartlingGunner(world, -1000, -1000));
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("barrel",
        new Description("Double barrel",
            "Shoots darts from an additional barrel",
            "buffs work on the second barrel (apart from on attack effects that happen on no cooldown trigger I guess)"),
        () -> {
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(-30, barrelLength));
        }, 200);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("barells", new Description("Triple Barrel",
        "The greed for barrels is strong, two was never enough"),
        () -> {
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(30, barrelLength));
        }, 200);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("tankmen", new Description("Tank",
        "Increases all basic stats, adds three barrels and occasionally fires bombs, bomb frequency depends on attackspeed",
        "Base attack speed is 1.5 to 6 but is increased by random(1,sqrt(2))**2." +
            "Pierce and damage is doubled, attackspeed *1.5. bombs deal 10 AOE damage (each)," +
            " and the chance of an attack being bombs is 0.05+originalStats[Stats.aspd]/72d where 1 is 100%"),
        () -> {
          barrelLength = 140;
          bulletLauncher.cannons.clear();

          bulletLauncher.cannons.add(new BulletLauncher.Cannon(-40, barrelLength+50));
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(40, barrelLength+50));
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(0, barrelLength+50));
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(-20, barrelLength+25));
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(20, barrelLength+25));
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(0, barrelLength));

          bulletLauncher.setImage("blcdrt");
          float bombchance = 0.05f + originalStats[Stats.aspd] / 72f;
          sprite.scale(2.5f);

          bulletLauncher.addProjectileModifier(new TankRockets(bombchance));

          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 1.5f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, 1.5f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.pierce, 2f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.power, 2f));
        }, 4500);
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("larger",
        new Description("Larger darts"
            ,
            "Shoots extra large extra powerful darts that deal extra damage, but shoots slightly slower (and slower projectiles)."
                +
                "Better if dartspeed is at least mediocre.",
            "dartspeed is 2-25. pierce is +1 or +2 (if dartspeed is above 9), damage is +1. " +
                "Attacks and dartspeeds 20% slower or 10% if dartspeed is decent."),
        () -> {
          sound = new SoundToPlay("gunshot", 0.6f);
          int extraPierce = originalStats[Stats.aspd] > 9 ? 2 : 1;
          float atcSpeedDebuff = originalStats[Stats.aspd] > 9 ? 0.9f : 0.8f;
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, atcSpeedDebuff));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, atcSpeedDebuff));
          addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.pierce, extraPierce));
          addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
          barrelExplosionSize = 90;
        }, 300);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("greatbarrel",
        new Description("Superior Barrels"
            ,
            "Superior barrels accomodate the greater darts, increasing all basic stats, especially attackspeed",
            "50% attackspeed 40% dartspeed, +1 pierce and damage"),
        () -> {
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, 1.5f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.speed, 1.4f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.pierce, 1f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1f));
        }, 400);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("juggermen",
        new Description("Juggernaut darts"
            ,
            "Juggernaut darts are extremely powerful, but it comes at a cost of attackspeed that depends on dartspeed."
                +
                "Removes additional barrels."
                + TextModifiers.red + "Warning:" + TextModifiers.white
                + " If dartspeed is too low may not shoot at all",
            "five times the pierce and six times the damage. Attack speed is multiplied by " +
                "(original speed)/25, if this is lower or equal to a fourth of the original it does not shoot,"
                +
                "and then reduced to third."),
        () -> {
          sound = new SoundToPlay("explosionSmall", 0.9f);
          barrelLength = 190;
          bulletLauncher.cannons.clear();
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(0, barrelLength));
          barrelExplosionSize = 220;

          path1Tier = 2;
          sprite.setImage("gunnerjugger");
          sprite.scale(1.5f);
          bulletLauncher.setImage("juggerdrt");
          int pierceBuff = 5;
          int dmgBuff = 24;
          float atcSpeedDebuff = originalStats[Stats.speed] / 25f;
          if (atcSpeedDebuff <= 0.25f) {
            atcSpeedDebuff = 0;
          }
          atcSpeedDebuff /= 3f;
          addBuff(new StatBuff<Turret>(StatBuff.Type.INCREASED, Stats.bulletSize, 3.8f));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.aspd, atcSpeedDebuff));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.pierce, pierceBuff));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.power, dmgBuff));
        }, 2225);
  }

  @Override
  protected void extraStatsUpdate() {
    if (clusterLauncher == null) {
      return;
    }
    clusterLauncher.setPower(stats[Stats.power] * 0.5f);
    clusterLauncher.setSize(stats[Stats.bulletSize] * 0.7f);
    clusterLauncher.setPierce((int) (stats[Stats.pierce] * 1.4f));
    clusterLauncher.setSpeed(stats[Stats.speed] * 1f);
  }

  BulletLauncher clusterLauncher;

  private void ClusterAttack(float x, float y) {
    clusterLauncher.move(x, y);
    clusterLauncher.attack(0, false);
  }

  @Override
  protected Upgrade up040() {
    return new Upgrade("jugjugjug",
        new Description("Cluster Juggernauts"
            ,
            "Balls last less long and split when depleted",

            "splits into 5-20 new projectiles depending on the size of your balls"),
        () -> {
          sound = new SoundToPlay("explosionBig", 1f);
          barrelLength = 250;
          bulletLauncher.cannons.clear();
          bulletLauncher.cannons.add(new BulletLauncher.Cannon(0, barrelLength));
          barrelExplosionSize = 350;

          clusterLauncher = new BulletLauncher(world, "juggerdrt");
          clusterLauncher.setDuration(0.95f);
          clusterLauncher.cannons = BulletLauncher.radial(
              (int) (5 + (originalStats[Stats.bulletSize] - 15) / 2));
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.projectileDuration, 0.3f));
          extraStatsUpdate();
          clusterLauncher.addMobCollide(BasicCollides.damage);
          clusterLauncher.addProjectileModifier(
              p -> p.addBuff(new OnTickBuff<Projectile>(proj -> proj.setRotation(
                  proj.getRotation() + 6f))));
          sprite.setImage("gunnerjugger");
          bulletLauncher.addProjectileModifier(
              p -> p.addBuff(new OnTickBuff<Projectile>(Projectile::bounce)));
          sprite.scale(1.5f);
          bulletLauncher.addProjectileModifier(
              p -> p.addBeforeDeath(proj -> this.ClusterAttack(proj.getX(), proj.getY())));
        }, 2225);
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
      bulletLauncher.attack(rotation, true);
    }
    setRotation(rotation);

    buffHandler.tick();
  }

  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 2f;
    stats[Stats.range] = 15f;
    stats[Stats.pierce] = 2f;
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(1.5f, 6f);
    stats[Stats.projectileDuration] = 4f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(15f, 45f);
    stats[Stats.speed] = Data.gameMechanicsRng.nextFloat(2f, 25f);
    stats[Stats.cost] = 250f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
  }
  // end of generated stats

}
