package Game.Buffs;

import Game.Enums.DamageType;
import Game.Game;
import Game.GameObject;
import Game.Mobs.TdMob;
import general.Log;

import java.util.ArrayList;
import java.util.List;

public class Explosive<T extends GameObject> implements Modifier<T>{
        public float damage;
        private int radius;
        private int visualRadius;
        private List<Modifier<TdMob>> effects=new ArrayList<>();
        DamageType damageType=DamageType.PHYSICAL;
        public Explosive(float damage,int radius){
            this(damage,radius,radius);
        }
        public Explosive(float damage,int radius,int visualRadius){
            this.damage=damage;
            this.radius=radius;
            this.visualRadius=visualRadius;

        }
        @Override
        public void mod(GameObject target) {
            target.world.getMobsGrid().callForEachCircle((int) target.getX(),
                    (int) target.getY(),
                radius, m -> {
                        for (Modifier<TdMob> effect : effects) {
                            effect.mod(m);
                        }
                    });
            if(damage!=0) {
                target.world.aoeDamage((int) target.getX(),
                        (int) target.getY(),
                        radius,
                        damage,
                        damageType
                );
            }
            target.world.lesserExplosionVisual((int) target.getX(),
                    (int) target.getY(),
                    visualRadius);
        }
        public void setRadius(int radius){
            this.radius=radius;
            visualRadius=radius;
        }
        public void multRadius(float multiplier){
            this.radius*=multiplier;
            visualRadius*=multiplier;
        }
        public void addEffect(Modifier<TdMob> effect){
            effects.add(effect);
        }
}
