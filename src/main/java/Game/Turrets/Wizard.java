package Game.Turrets;

import static Game.Buffs.StatBuff.Type.INCREASED;
import static Game.Buffs.StatBuff.Type.MORE;

import Game.BasicCollides;
import Game.Buffs.Explosive;
import Game.Buffs.Modifier;
import Game.Buffs.OnTickBuff;
import Game.Buffs.StatBuff;
import Game.BulletLauncher;
import Game.BulletLauncher.Cannon;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Projectile;
import Game.TdWorld;
import Game.TransformAnimation;
import Game.TurretGenerator;
import general.Data;
import general.Description;
import general.Util;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Graphics;
import windowStuff.ImageData;
import windowStuff.Sprite;
import windowStuff.Sprite.FrameAnimation;

public class Wizard extends Turret {

  @Override
  protected ImageData getImageUpdate(){
    return Graphics.getImage("wizard");
  }

  private final List<BulletLauncher> spells = new ArrayList<>(1);

  public Wizard(TdWorld world, int X, int Y) {
    super(world, X, Y, new BulletLauncher(world, "skull"));
    onStatsUpdate();
    bulletLauncher.addMobCollide(BasicCollides.damage);
    spells.add(bulletLauncher);
    bulletLauncher.addAttackEffect(new CastAnimation("wizCast", 500, 2, 2));
    bulletLauncher.addAttackEffect(new CastAnimation("wizCast", 300, 4, 1.5f));
  }

  private static class CastAnimation implements Modifier<BulletLauncher>{
    private final ImageData img;
    protected String shader = "basic";
    protected final float size;
    protected float spin;
    protected float duration;
    protected CastAnimation(String img, float size, float spin, float duration){
      this.img = Graphics.getImage(img);
      this.size=size;
      this.spin=spin;
      this.duration = duration;
    }

    @Override
    public void mod(BulletLauncher target) {
      new Sprite(img, 3, shader).setPosition(target.getX(), target.getY()).
          setSize(size, size).addToBs(Game.get().getSpriteBatching("main"))
          .setRotation(Data.unstableRng.nextFloat(360))
          .playAnimation(new TransformAnimation(duration)
          .setOpacityScaling(-1/(1000*duration/Game.tickIntervalMillis)).setSpinning(spin));
    }
  }

  @Override
  public void setRotation(float f) {
    rotation = f % 360;
  }

  @Override
  public void onGameTick(int tick) {
    if (notYetPlaced) {
      return;
    }
    for (var spell : spells) {
      spell.tickCooldown();
      spell.move(x, y);
    }
    TdMob target = target();
    if (target != null) {
      setRotation(Util.get_rotation(target.getX() - x, target.getY() - y));
      for (var spell : spells) {
        while (spell.canAttack()) {
          spell.attack(rotation);
        }
      }
    }

    buffHandler.tick();
  }

  @Override
  public void onStatsUpdate() {
    if (spells == null) {
      super.onStatsUpdate();
      return;
    }
    for (var spell : spells) {
      spell.updateStats(stats);
    }
    rangeDisplay.setSize(stats[Stats.range] * 2, stats[Stats.range] * 2);
    extraStatsUpdate();
  }

  public static TurretGenerator generator(TdWorld world) {
    return new TurretGenerator(world, "wizard", "Wizard", () -> new Wizard(world, -1000, -1000));
  }

  private BulletLauncher fireballs;
  @Override
  protected Upgrade up010() {
    return new Upgrade("Fireball-0", new Description("Fireball",
        "shoots fireball",
        "Deals damage determined by pierce in an area determined by projectile size"),
        () -> {
          Explosive explosive = new Explosive(2, 100);
          fireballs = new BulletLauncher(world, "Fireball-0") {
            @Override
            public void updateStats(float[] stats) {
              setDuration(stats[Turret.Stats.projectileDuration] * 0.5f);
              setPierce(0);
              setPower(0);
              explosive.damage = stats[Stats.pierce];
              setSize(stats[Turret.Stats.bulletSize] * 1.8f);
              explosive.setRadius((int) (stats[Turret.Stats.bulletSize] * 5f));
              setSpeed(stats[Turret.Stats.speed] * 2);
              setCooldown(1000f / stats[Turret.Stats.aspd] * 3);
            }
          };
          fireballs.addProjectileModifier(
              p -> p.getSprite().playAnimation(
                  new FrameAnimation(
                      "Fireball", p.getStats()[Projectile.Stats.duration]
                  )
              )
          );
          fireballs.addAttackEffect(new CastAnimation("fireRune", 600, 1,3));
          fireballs.addMobCollide(BasicCollides.damage);
          fireballs.updateStats(stats);
          fireballs.addProjectileModifier(p -> p.addBeforeDeath(explosive));
          spells.add(fireballs);
        }, 250);
  }

  private BulletLauncher dbreath;
  @Override
  protected Upgrade up020() {
    return new Upgrade("fireball", new Description("Fire Breath",
        "shoots fire breath",
        "fires quickly with low damage"),
        () -> {
          dbreath = new BulletLauncher(world, "fireball") {
            @Override
            public void updateStats(float[] stats) {
              setDuration(stats[Turret.Stats.projectileDuration] * 0.25f);
              setPierce((int) stats[Stats.pierce]);
              setPower(stats[Stats.power]);
              setSize(stats[Turret.Stats.bulletSize] * 0.8f);
              setSpeed(stats[Turret.Stats.speed]);
              setCooldown(1000f / stats[Turret.Stats.aspd] * 0.05f);
            }
          };
          dbreath.setSpread(50);
          //dbreath.addAttackEffect(new CastAnimation("fireRune", 600, 1,3));
          dbreath.addMobCollide(BasicCollides.damage);
          dbreath.updateStats(stats);
          spells.add(dbreath);
        }, 1800);
  }

  @Override
  protected Upgrade up030() {
    return new Upgrade("firebolt", new Description("Firestorm",
        "shoots firestorms",
        "firestorms shoot fire breath projectiles at 30% cooldown"),
        () -> {
          BulletLauncher fstorm = new BulletLauncher(world, "firebolt") {
            @Override
            public void updateStats(float[] stats) {
              setDuration(stats[Turret.Stats.projectileDuration] * 4f);
              setPierce((int) stats[Stats.pierce]);
              setPower(stats[Stats.power]);
              setSize(stats[Turret.Stats.bulletSize] * 5f);
              setSpeed(stats[Turret.Stats.speed]*0.15f);
              setCooldown(1000f / stats[Turret.Stats.aspd] * 5f);
            }
          };
          fstorm.setSpread(360);
          //dbreath.addAttackEffect(new CastAnimation("fireRune", 600, 1,3));

          fstorm.addProjectileModifier(p->{
            BulletLauncher fireRain = new BulletLauncher(dbreath);
            fireRain.setCooldown(fireRain.getCooldown()*0.3f);
            p.getSprite().setLayer(2);
            p.getSprite().playAnimation(new TransformAnimation(999).setSpinning(1.8f));
            p.addBuff(new OnTickBuff<Projectile>(storm->{
            fireRain.move(storm.getX(),storm.getY());
            fireRain.tickCooldown();
            while(fireRain.canAttack()){
              fireRain.attack(Data.gameMechanicsRng.nextFloat(360), true);
            }
          }));});
          fstorm.updateStats(stats);
          spells.add(fstorm);
        }, 7000);
  }


  private boolean ShouldReplaceProj(){
    if(path2Tier==5){
      return true;
    }
    return Data.gameMechanicsRng.nextFloat()<0.1f;
  }
  @Override
  protected Upgrade up040() {
    return new Upgrade("firebolt2", new Description("Fire Enhancement",
        "small firebolts are sometimes replaced by big ones",
        "the chance is 10%"),
        () -> {
          dbreath.addProjectileModifier(p->{
            if(ShouldReplaceProj()){
              fireballs.attack(p.getRotation(),false).move(p.getX(),p.getY());
              p.delete();
            }
          });
        }, 5000);
  }

  @Override
  protected Upgrade up050() {
    return new Upgrade("firebolt2", new Description("Fire Mastery","small firebolts are ALWAYS replaced by big ones"),
        () -> {
        }, 50000);
  }


  @Override
  protected Upgrade up001() {
    return new Upgrade("DoubleDart", new Description("Faster Casting","shoots 1.5x faster."),
        () -> addBuff(new StatBuff<Turret>(MORE, Stats.aspd, 1.5f)), 100);
  }

  @Override
  protected Upgrade up100() {
    return new Upgrade("inheritor3", new Description("More Bolts","shoots 2 additional magic bolts"),
        () -> {
          bulletLauncher.cannons.add(new Cannon(0,0, 25));
          bulletLauncher.cannons.add(new Cannon(0,0, -25));
        }
        , 100);
  }

  @Override
  protected Upgrade up200() {
    return new Upgrade("boost", new Description("Overcharge","each magic bolt fired charges all other spells",
        "other spells are charged by 0.25 seconds per magic bolt"),
        () -> {
          bulletLauncher.addProjectileModifier(p->{
            for(BulletLauncher spell : spells){
              if(spell != bulletLauncher){
                spell.setRemainingCooldown(spell.getRemainingCooldown()-250);
              }
            }
          });
        }
        , 777);
  }

  @Override
  protected Upgrade up300() {
    return new Upgrade("blustop", new Description("Superior magicking",
        "significantly reduces magic bolt cooldown",
        "to 40%"),
        () -> {
          bulletLauncher.addAttackEffect(mBolt -> mBolt.setRemainingCooldown(mBolt.getRemainingCooldown()- mBolt.getCooldown()*0.6f));
        }
        , 2000);
  }

  private final Projectile.Guided guided = new Projectile.Guided(1000, 3);
  @Override
  protected Upgrade up400() {
    return new Upgrade("zaprot", new Description("Archmage",
        "Magic bolts now seek and reduce all other spell cooldowns when they hit a target.",
        "other spells are charged by 0.2 seconds per magic bolt hit"),
        () -> {
          bulletLauncher.addProjectileModifier(p->p.addBuff(new OnTickBuff<Projectile>(guided::tick)));
          bulletLauncher.addMobCollide((mob,proj) -> {
            for(BulletLauncher spell : spells){
              if(spell != bulletLauncher){
                spell.setRemainingCooldown(spell.getRemainingCooldown()-200);
              }
            }
            return true;
          });
        }
        , 5000);
  }

  @Override
  protected Upgrade up500() {
    return new Upgrade("zaprot", new Description("Grand Sorcery",
        "reduce cooldowns.",
        "by 50%"),
        () -> {
          addBuff(new StatBuff<Turret>(MORE, Stats.aspd, 2));
        }
        , 25000);
  }

  private BulletLauncher lightning;
  @Override
  protected Upgrade up002() {
    return new Upgrade("bluray", new Description("Lightning","lightning"),
        () -> {
            lightning = new BulletLauncher(world, "bluray") {
            @Override
            public void updateStats(float[] stats) {
              setPierce((int) (stats[Stats.range] / 20));
              setPower(stats[Stats.power] * stats[Stats.bulletSize]);
              setSize(8);
              setSpeed(stats[Stats.range]);
              setCooldown(1000f / stats[Stats.speed] * 100);
              setDuration(0.1f);
            }
          };
          lightning.setLauncher(Lightning::new);
          lightning.addMobCollide(BasicCollides.damage);
          lightning.updateStats(stats);
          lightning.setAspectRatio(5);
          lightning.addAttackEffect(new CastAnimation("zaprot", 450, 0,0.3f));
          spells.add(lightning);
        }, 1000);
  }

  private float lightningCritChance=0f;
  private boolean lightningCanMulticrit = false;
  private boolean rollLightningCrit(){
    return Data.gameMechanicsRng.nextFloat()<lightningCritChance;
  }
  private void modLightningForCrit(Projectile p){
    if(!rollLightningCrit()){
      return;
    }

    Explosive ex = new Explosive(p.getPower(), 50);
    p.getSprite().setColors(Util.getColors(3,0,0));
    p.addBuff(new StatBuff<Projectile>(INCREASED, Projectile.Stats.pierce, 2));
    p.addBuff(new StatBuff<Projectile>(INCREASED, Projectile.Stats.power, 3));
    p.addMobCollide((proj, target) -> {ex.mod(proj);return true;});

    if(!lightningCanMulticrit){
      return;
    }

    while(rollLightningCrit()){
      p.addBuff(new StatBuff<Projectile>(INCREASED, Projectile.Stats.pierce, 2));
      p.addBuff(new StatBuff<Projectile>(INCREASED, Projectile.Stats.power, 3));
    }
  }
  @Override
  protected Upgrade up003() {
    return new Upgrade("bluray", new Description("Critical Voltage",
        "lightning sometimes crits for more zaps and more damage",
        "crit chance: 15%"),
        () -> {
          lightningCritChance=0.15f;
          lightning.addProjectileModifier(this::modLightningForCrit);
        }, 1500);
  }

  @Override
  protected Upgrade up004() {
    return new Upgrade("bluray", new Description("Criticaler Voltage",
        "lightning crits can crit multiple times. increased lightning crit chance.",
        "crit chance: 25%. Can crit recursively. Lightning damage and chain count scales linearly with number of crits"),
        () -> {
          lightningCanMulticrit = true;
          lightningCritChance = 0.25f;
        }, 3000);
  }

  @Override
  protected Upgrade up005() {
    return new Upgrade("bluray", new Description("Criticalest Voltage",
        "more lightning crit chance",
        "crit chance: 70%"),
        () -> {
          lightningCritChance=0.7f;
        }, 15000);
  }




  // generated stats
  @Override
  public void clearStats() {
    stats[Stats.power] = 2f;
    stats[Stats.range] = Data.gameMechanicsRng.nextFloat(250f, 500f);
    stats[Stats.pierce] = Data.gameMechanicsRng.nextFloat(2f, 4f);
    stats[Stats.aspd] = Data.gameMechanicsRng.nextFloat(0.8f, 1.5f);
    stats[Stats.projectileDuration] = 2f;
    stats[Stats.bulletSize] = Data.gameMechanicsRng.nextFloat(20f, 100f);
    stats[Stats.speed] = 15f;
    stats[Stats.cost] = 100f;
    stats[Stats.size] = 50f;
    stats[Stats.spritesize] = 150f;
  }
  // end of generated stats
}
