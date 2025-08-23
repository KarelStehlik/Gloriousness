package Game.Buffs;

import Game.Enums.DamageType;
import Game.GameObject;
import Game.Mobs.TdMob;
import java.util.ArrayList;
import java.util.List;

public class Explosive implements Modifier<GameObject> {

  public float damage;
  private float radius;
  private float visualRadius;
  private final List<Modifier<TdMob>> preEffects = new ArrayList<>();
  private final List<Modifier<TdMob>> postEffects = new ArrayList<>();
  DamageType damageType = DamageType.PHYSICAL;

  public Explosive(float damage, float radius) {
    this(damage, radius, radius);
  }

  public Explosive(float damage, float radius, float visualRadius) {
    this.damage = damage;
    this.radius = radius;
    this.visualRadius = visualRadius;

  }

  @Override
  public void mod(GameObject target) {
    target.world.getMobsGrid().callForEachCircle((int) target.getX(),
        (int) target.getY(),
        radius, m -> {
          for (Modifier<TdMob> effect : preEffects) {
            effect.mod(m);
          }
          m.takeDamage(damage, damageType);
          for (Modifier<TdMob> effect : postEffects) {
            effect.mod(m);
          }
        });
    target.world.lesserExplosionVisual((int) target.getX(),
        (int) target.getY(),
        visualRadius);
  }

  public void setRadius(int radius) {
    this.radius = radius;
    visualRadius = radius;
  }

  public void multRadius(float multiplier) {
    this.radius *= multiplier;
    visualRadius *= multiplier;
  }

  public void addPreEffect(Modifier<TdMob> effect) {
    preEffects.add(effect);
  }
  public void addPostEffect(Modifier<TdMob> effect) {
    postEffects.add(effect);
  }
}
