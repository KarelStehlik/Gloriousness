package Game.Buffs;

import Game.DamageType;
import Game.Game;
import Game.Mobs.TdMob;
import Game.Mobs.TdMob.Stats;
import general.Util;
import java.util.Iterator;
import java.util.TreeSet;
import windowStuff.Sprite;

public class Ignite<T extends TdMob> implements Buff<T>, Comparable<Ignite<T>> {

  protected final float damagePerTick, expiryTime;
  private final long id;


  public Ignite(float dmg, float dur) {
    damagePerTick = dmg;
    expiryTime = Game.get().getTicks() + dur / Game.tickIntervalMillis;
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

  private class Aggregator implements BuffAggregator<T> {

    private final TreeSet<Ignite<T>> ignites = new TreeSet<>();
    private final Sprite fireSprite;
    private float dpTick = 0;

    protected Aggregator() {
      var bs = Game.get().getSpriteBatching("main");
      fireSprite = new Sprite("Fireball-0", 1).addToBs(bs).setSize(50, 50);
      fireSprite.setRotation(180);
      fireSprite.playAnimation(fireSprite.new BasicAnimation("Fireball-0", 0.3f).loop());
      fireSprite.setHidden(true);
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

    @Override
    public void tick(T target) {
      float time = Game.get().getTicks();

      for (Iterator<Ignite<T>> iterator = ignites.iterator(); iterator.hasNext(); ) {
        Ignite<T> ig = iterator.next();
        if (ig.expiryTime > time) {
          break;
        }
        iterator.remove();
        dpTick -= ig.damagePerTick;
      }

      float power = Math.min(5,dpTick / target.getStats()[Stats.health] * 1000);
      fireSprite.setSize(power * target.getStats()[Stats.size],
          power * target.getStats()[Stats.size]);
      fireSprite.setPosition(target.getX(),
          target.getY() + power * target.getStats()[Stats.size] * .4f);
      target.takeDamage(dpTick, DamageType.TRUE);
      if (ignites.isEmpty()) {
        fireSprite.setHidden(true);
      }
    }

    @Override
    public void delete(T target) {
      ignites.clear();
      fireSprite.delete();
    }

    @Override
    public BuffAggregator<T> copyForChild(T newTarget) {
      Aggregator copy = new Aggregator();
      for (Ignite<T> ig : ignites) {
        copy.add(ig.copy(), newTarget);
      }
      return copy;
    }
  }
}
