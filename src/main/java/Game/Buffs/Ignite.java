package Game.Buffs;

import Game.DamageType;
import Game.Game;
import Game.TdMob;
import java.util.Iterator;
import java.util.TreeSet;
import windowStuff.Sprite;

public class Ignite<T extends TdMob> implements Buff<T>, Comparable<Ignite<T>> {

  private static long staticId = 0;
  protected final float damagePerTick, expiryTime;
  private final long id;


  public Ignite(float dmg, float dur) {
    damagePerTick = dmg;
    expiryTime = Game.get().getTicks() + dur / Game.tickIntervalMillis;
    id = staticId;
    staticId++;
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
    private final Sprite fireSprite;
    private float dpTick = 0;

    protected Aggregator() {
      var bs = Game.get().getSpriteBatching("main");
      fireSprite = new Sprite("Fireball-0", 1).addToBs(bs).setSize(50, 50);
      fireSprite.setRotation(180);
      fireSprite.playAnimation(fireSprite.new BasicAnimation("Fireball-0", 1).loop());
      fireSprite.setHidden(true);
    }

    private void delete() {
      fireSprite.delete();
      ignites.clear();
    }

    @Override
    public void add(Buff<T> b, T target) {
      assert b instanceof Ignite<T>;
      ignites.add((Ignite<T>) b);
      dpTick += ((Ignite<TdMob>) b).damagePerTick;
      //fireSprite.setPosition(target.getX(), target.getY());
      fireSprite.setHidden(false);
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

      float power = dpTick / target.baseStats.health * 1000;
      fireSprite.setSize(power * target.baseStats.size, power * target.baseStats.size);
      fireSprite.setPosition(target.getX(), target.getY() + power * target.baseStats.size * .4f);
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
  }
}
