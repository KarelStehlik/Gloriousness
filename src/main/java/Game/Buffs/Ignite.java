package Game.Buffs;

import Game.Enums.DamageType;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Mobs.TdMob.Stats;
import general.Util;
import java.util.Iterator;
import java.util.TreeSet;
import windowStuff.AbstractSprite;
import windowStuff.Sprite;

public class Ignite<T extends TdMob> implements Buff<T>, Comparable<Ignite<T>> {

  protected final float damagePerTick, expiryTime;
  private final long id;


  public Ignite(float dps, float durMillis) {
    damagePerTick = dps * Game.tickIntervalMillis / 1000;
    expiryTime = Game.get().getTicks() + durMillis / Game.tickIntervalMillis;
    id = Util.getUid();
  }

  private Ignite(float damagePerTick, float expiryTime, long id) {
    this.damagePerTick = damagePerTick;
    this.expiryTime = expiryTime;
    this.id = id;
  }

  public Ignite<T> copy() {
    return new Ignite<T>(damagePerTick, expiryTime, Util.getUid());
  }

  @Override
  public int compareTo(Ignite o) {
    int floatComp = Float.compare(expiryTime, o.expiryTime);
    if (floatComp != 0) {
      return floatComp;
    }
    return Long.compare(id, o.id);
  }

  @Override
  public BuffAggregator<T> makeAggregator() {
    return new Aggregator();
  }

  public class Aggregator implements BuffAggregator<T> {

    private final TreeSet<Ignite<T>> ignites = new TreeSet<>();
    private final AbstractSprite fireSprite;

    public float getDpTick() {
      return dpTick;
    }

    private float dpTick = 0;
    private Aggregator parentIgnites = null;

    protected Aggregator() {
      var bs = Game.get().getSpriteBatching("main");
      Sprite fs = new Sprite("Fireball-0", 3).setPosition(-1000, -1000).addToBs(bs).setSize(50, 50);
      fs.setRotation(180);
      fs.playAnimation(fs.new BasicAnimation("Fireball-0", 1.1f).loop());
      fs.setHidden(true);
      fireSprite = fs;
    }

    @Override
    public boolean add(Buff<T> b, T target) {
      assert b instanceof Ignite<T>;
      Ignite<T> buff = (Ignite<T>) b;
      ignites.add(buff);
      dpTick += buff.damagePerTick;
      //fireSprite.setPosition(target.getX(), target.getY());
      fireSprite.setHidden(false);
      return true;
    }

    private void update() {
      if (parentIgnites != null) {
        parentIgnites.update();
      }
      float time = Game.get().getTicks();

      for (Iterator<Ignite<T>> iterator = ignites.iterator(); iterator.hasNext(); ) {
        Ignite<T> ig = iterator.next();
        if (ig.expiryTime > time) {
          break;
        }
        iterator.remove();
        dpTick -= ig.damagePerTick;
      }
    }

    @Override
    public void tick(T target) {
      if (parentIgnites != null) {
        dpTick -= parentIgnites.dpTick;
        update();
        dpTick += parentIgnites.dpTick;
      } else {
        update();
      }

      float power = Math.min(3, dpTick / target.getStats()[Stats.health] * 60);
      fireSprite.setSize(power * target.getStats()[Stats.size],
          power * target.getStats()[Stats.size]);
      fireSprite.setPosition(target.getX(),
          target.getY() + power * target.getStats()[Stats.size] * .4f);
      target.takeDamage(dpTick, DamageType.TRUE);
      fireSprite.setHidden(power <= 0.001);
    }

    @Override
    public void delete(T target) {
      fireSprite.delete();
    }

    @Override
    public BuffAggregator<T> copyForChild(T newTarget) {
      Aggregator copy = new Aggregator();
      copy.parentIgnites = this;
      copy.dpTick += dpTick;
      return copy;
    }
  }
}
