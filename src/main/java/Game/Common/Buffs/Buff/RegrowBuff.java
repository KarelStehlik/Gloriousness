package Game.Common.Buffs.Buff;

import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.Game;
import GlobalUse.RefFloat;

public class RegrowBuff {
    public RefFloat interval,healamount;
    float cooldown;
    //interval in milliseconds
    public RegrowBuff(RefFloat interval,RefFloat healamount){
        this.healamount=healamount;
        this.interval=interval;
        this.cooldown=interval.get();
    }
    public void tick(TdMob mob){
        if(mob.wasDeleted()||mob.isUndamaged()){
            return;
        }
        cooldown-= Game.tickIntervalMillis;
        if(cooldown<=0){
            mob.heal(healamount.get());
        }
    }
    public static OnTickBuff<TdMob> getBuff(RefFloat interval, RefFloat healamount){
        RegrowBuff buff=new RegrowBuff(interval,healamount);
        return new OnTickBuff<>(Float.POSITIVE_INFINITY, buff::tick,false);
    }
}
