package Game.Mobs.MobClasses;

import Game.Common.Buffs.Buff.RegrowBuff;
import GlobalUse.RefFloat;

public class RegrowParams {
    public RefFloat interval,amount;
    public RegrowParams(RefFloat interval,RefFloat amount){
        this.interval=interval;
        this.amount=amount;
    }
}
