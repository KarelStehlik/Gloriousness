package Game.Buffs;

import Game.BulletLauncher;
import general.Data;

public class ProcTrigger<T extends Proc> implements AttackEffect  {//happens on some attacks

        private T attackEffect;
        private boolean cooldownOnly;
        private boolean triggered;
        private double chance;//between 0 and 1
    public ProcTrigger(T effect, double chance){
        this(effect,chance,false);
    }
        public ProcTrigger(T effect, double chance,boolean cooldownOnly){
            attackEffect=effect;
            this.chance=chance;
            this.cooldownOnly=cooldownOnly;
        }
        @Override
        public void mod(BulletLauncher target, boolean cooldown, float angle) {
            if(cooldownOnly&&!cooldown){
                return;
            }
            if(Data.gameMechanicsRng.nextDouble(0,1f)<= chance){
                triggered=true;
                attackEffect.mod(target,cooldown,angle);
            }else if(triggered){
                triggered=false;
                attackEffect.endMod(target,cooldown,angle);
            }
        }
}
