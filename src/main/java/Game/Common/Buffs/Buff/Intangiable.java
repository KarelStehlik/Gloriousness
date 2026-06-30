package Game.Common.Buffs.Buff;

import Game.Common.Projectile;
import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.Game;
import GlobalUse.Util;

//while hitting stuff projectile will go active for upTime and then inactive for downTime, every time it does this duration is decrased by durationPenalty
    //everything is in seconds
public class Intangiable extends DefaultBuff<Projectile> implements Projectile.OnCollideComponent<TdMob> {
    private float timer;
    private final float upTime;
    private final float downTime;
    private boolean active;
    private boolean hasHit;
    private float durationPenalty=0;
    public Intangiable(float uptime,float downTime){
        super();
        this.upTime=uptime;
        this.downTime=downTime;
        this.timer=uptime;
        active =true;
        hasHit=false;
    }

    public Intangiable(float uptime,float downTime,float durationPenalty){
        super();
        this.upTime=uptime;
        this.downTime=downTime;
        this.timer=uptime;
        active =true;
        hasHit=false;
        this.durationPenalty=durationPenalty;
    }

    private void setTangiable(Projectile target,boolean tangiable){
        if(active ==tangiable)
            return;
        active =tangiable;
        target.setActive(tangiable);
        target.getSprite().setHidden(false);
        if(tangiable) {
            target.clearCollisions();
            timer = upTime;
            hasHit=false;
        }else{
            timer=downTime;
            if(durationPenalty!=0) {
                target.addBuff(new StatBuff<>(StatBuff.Type.FINALLY_ADDED, Projectile.Stats.duration, -durationPenalty * 1000));
            }
        }

    }
    @Override
    public Intangiable.Aggregator makeAggregator() {
        return new Intangiable.Aggregator();
    }

    @Override
    public boolean collide(Projectile proj, TdMob target) {
        if(active)
            hasHit=true;
        return false;
    }

    protected class Aggregator extends DefaultAggregator<Projectile> {
        @Override
        public boolean add(Buff<Projectile> b, Projectile target) {
            super.add(b,target);
            assert b instanceof Intangiable;
            target.addMobCollide((Projectile.OnCollideComponent<TdMob>) b);
            return true;
        }
        @Override
        public void tick(Projectile target) {
            super.tick(target);
            if(active&&!hasHit)
                return;
            timer-= Game.secondsPerFrame;
            if(timer<=0)
                setTangiable(target,!active);

        }
    }
}
