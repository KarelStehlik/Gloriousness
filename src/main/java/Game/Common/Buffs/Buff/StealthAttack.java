package Game.Common.Buffs.Buff;

import Game.Common.Buffs.Modifier.Modifier;
import Game.Misc.Game;
import Game.Common.Turrets.Turret;

public class StealthAttack extends DefaultBuff<Turret> {
    private float stealthDelay;
    private float stealthTimer=0;
    private float stealthRange;
    private boolean ready=false;
    private boolean stealthing=false;
    private StatBuff<Turret> rangeRestrict;

    private final Modifier<? super Turret> mod; //you probably aren't using a Modifier<Object> but I guess you can
    public StealthAttack(Modifier<? super Turret> onStealthModifier,float stealthDelay) {
        mod =onStealthModifier;
        this.stealthDelay=stealthDelay;
    }
    public StealthAttack(Modifier<? super Turret> onStealthModifier,float stealthDelay,float stealthRange) {
        this.stealthRange=stealthRange;
        mod =onStealthModifier;
        this.stealthDelay=stealthDelay;
    }
    
    private void stealth(Turret turret){
        if(ready){
            return;
        }
        if(!stealthing){
            stealthing=true;
            rangeRestrict=new StatBuff<Turret>(StatBuff.Type.MORE, Turret.Stats.range, stealthRange);
            turret.addBuff(rangeRestrict);
        }
        stealthTimer+= Game.secondsPerFrame;
        if(stealthTimer!=0){
            float[] stelthcolor=turret.getColor4();
            stelthcolor[3]=Math.max(1-stealthTimer/stealthDelay,0.1f);
            turret.setColor4(stelthcolor);
        }
        if(stealthTimer>=stealthDelay){
            ready=true;
            turret.removeBuff(rangeRestrict);
            rangeRestrict=null;

        }

    }
    private void unStealth(Turret turret){
        if(ready){
            ready=false;
            mod.mod(turret);
        }else if(stealthing){
            turret.removeBuff(rangeRestrict);
            rangeRestrict=null;
        }
        stealthing=false;
        stealthTimer=0;
        float[] stelthcolor=turret.getColor4();
        stelthcolor[3]=1;
        turret.setColor4(stelthcolor);
    }
    @Override
    public Aggregator makeAggregator() {
        return new Aggregator();
    }
    protected class Aggregator extends DefaultAggregator<Turret> {
        @Override
        public void tick(Turret target) {
            super.tick(target);
            if (target.bulletLauncher.canAttack()) {
                stealth(target);
            }else{
                unStealth(target);
            }


        }


    }

}
