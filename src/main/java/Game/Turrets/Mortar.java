package Game.Turrets;

import Game.Buffs.Explosive;
import Game.Buffs.Ignite;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.BulletLauncher;
import Game.BulletLauncher.Cannon;
import Game.Game;
import Game.Projectile;
import Game.TdWorld;
import Game.TurretGenerator;
import general.Data;
import general.Description;
import general.Util;
import org.joml.Vector2d;
import windowStuff.Graphics;
import windowStuff.ImageData;
import windowStuff.Sprite;

public class Mortar extends Turret {


    @Override
    protected ImageData getImageUpdate(){
        String monkimg="mortarMonkey";
        String badgeimg="mortarBadge";
        String mortarimg="mortar";


        if(badgeSprite!=null){
            badgeSprite.setImage(badgeimg);
        }
        if(monkeySprite!=null){
            monkeySprite.setImage(monkimg);
        }
        return Graphics.getImage(mortarimg);
    }

  private final Explosive<Projectile> explosive = new Explosive<>(0, 0);


  @Override
  protected void extraStatsUpdate() {
    if (explosive != null) {
      explosive.damage = stats[Stats.power];
      explosive.setRadius((int) stats[ExtraStats.radius]);
    }
  }

  public Sprite badgeSprite;
  public Sprite monkeySprite;

  public Mortar(TdWorld world, int X, int Y) {
    super(world, X, Y, new BulletLauncher(world, "drt"));
    bulletLauncher.addProjectileModifier(p -> p.addBeforeDeath(this.explosive));
    bulletLauncher.addProjectileModifier(
        p -> p.moveRelative((Data.gameMechanicsRng.nextFloat() - 0.5f) * stats[ExtraStats.spread],
            (Data.gameMechanicsRng.nextFloat() - 0.5f) * stats[ExtraStats.spread]));
    bulletLauncher.addProjectileModifier(p -> p.getSprite().playAnimation(
        p.getSprite().new BasicAnimation("Explosion1", this.getStats()[Stats.projectileDuration])));

    badgeSprite = new Sprite("turretBase", 22).setSize(sprite.getWidth()*2*0.75f,
            sprite.getWidth()*2*0.75f);
    world.getBs().addSprite(badgeSprite);
    badgeSprite.setShader("basic");

    monkeySprite = new Sprite("turretBase", 22).setSize(sprite.getWidth()*1.75f,
          0);
    monkeySprite.setNaturalHeight();
    world.getBs().addSprite(monkeySprite);
    monkeySprite.setShader("rotator");
    move(X,Y);
    getImageUpdate();
    onStatsUpdate();
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, "mortar", "Mortar",
        () -> new Mortar(world, -1000, -1000));
  }


    @Override
    public void move(float _x, float _y) {
        super.move(_x, _y);
        sprite.setPosition(_x, _y);
        badgeSprite.setPosition(sprite.getX(),sprite.getY()-sprite.getHeight()*0.2f);
        monkeySprite.setPosition(sprite.getX()+sprite.getWidth()+monkeySprite.getWidth(),sprite.getY()-sprite.getHeight()+monkeySprite.getHeight());
        rangeDisplay.setPosition(_x, _y);
        if (world.canFitTurret((int) x, (int) y, stats[Stats.size])) {
            rangeDisplay.setColors(Util.getColors(0, 0, 0));
        } else {
            rangeDisplay.setColors(Util.getColors(9, 0, 0));
        }
        bulletLauncher.move(_x, _y);
    }

  @Override
  protected Upgrade up100() {
    return new Upgrade("Bomb-0",
        new Description("Double Bombs"
            ,
            "Doubles... the bombs.",
            "Genius."),
        () -> {
          bulletLauncher.cannons.add(new Cannon(0, 0));
        }, 300);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("Bomb-0",
        new Description("Focused Bombardment"
            ,
            "Adds 3 more bombs, but bombs are smaller.",
            "Doesn't actually focus shit"),
        () -> {
          for (int i = 0; i < 3; i++) {
            bulletLauncher.cannons.add(new Cannon(0, 0));
          }
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.bulletSize, 0.6f));
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, 0.4f));
        }, 300);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("Bomb-0",
        new Description("Glorious Coverage"
            ,
            "Increases attack speed based on spread",
            ""),
        () -> {
          float attackArea = Util.square(originalStats[ExtraStats.spread] / 100);
          addBuff(new StatBuff<Turret>(Type.MORE, Stats.aspd, attackArea));
        }, 300);
  }

  @Override
  protected Upgrade up010() {
    return new Upgrade("Bomb-0",
        new Description("Larger Shells"
            ,
            "Extra AoE, +1 damage, increases spread",
            ""),
        () -> {
          addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, 1.5f));
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.spread, 2));
        }, 300);
  }

  @Override
  protected Upgrade up020() {
    return new Upgrade("Bomb-0",
        new Description("Chaotic Bombing"
            ,
            "No need to he accurate if a miss is lethal enough",
            "further increases explosion size based on spread"),
        () -> {
          addBuff(new StatBuff<Turret>(StatBuff.Type.ADDED, Stats.power, 1));
          addBuff(new StatBuff<Turret>(StatBuff.Type.MORE, Stats.bulletSize, 1.2f));
          float radiusBuff = 1 + originalStats[ExtraStats.spread] / 200f;
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.radius, radiusBuff));
          addBuff(new StatBuff<Turret>(Type.MORE, ExtraStats.spread, 1.5f));
        }, 300);
  }


  @Override
  protected Upgrade up001() {
    return new Upgrade("Fireball-0",
        new Description("Glorious Flames"
            ,
            "fire",
            ""),
        () -> {
          explosive.addPreEffect(mob -> mob.addBuff(new Ignite<>(this.stats[Stats.power], 2000)));
        }, 300);
  }


  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    bulletLauncher.tickCooldown();

    Vector2d mousePos = Game.get().getUserInputListener().getPos();

    while (bulletLauncher.canAttack()) {
      bulletLauncher.move((float) mousePos.x, (float) mousePos.y);
      bulletLauncher.attack(rotation, true);
    }

    buffHandler.tick();
  }
    @Override
    public void delete() {
        sprite.delete();
        badgeSprite.delete();
        monkeySprite.delete();
        buffHandler.delete();
        rangeDisplay.delete();
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
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.4f, 0.8f);
    stats[Stats.projectileDuration] = 0.9f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(120f, 180f);
    stats[Stats.speed] = 0f;
    stats[Stats.cost] = 250f;
    stats[Stats.size] = 25f;
    stats[Stats.spritesize] = 100f;
    stats[ExtraStats.spread] = Data.gameMechanicsRng.nextFloat(10f, 300f);
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
