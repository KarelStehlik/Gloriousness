package Game.Mobs.MobGeneration;

import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

@FunctionalInterface
public interface BloonNew {

    @FunctionalInterface
    interface BloonNewRegrow{
        TdMob create(TdWorld w, int wave,boolean regrow);
    }
    static BloonNew BloonNewRegrow(BloonNewRegrow base, boolean regrow){
        return new BloonNew() {
            @Override
            public TdMob create(TdWorld w, int wave) {
                TdMob mob=base.create(w,wave,regrow);
                return mob;
            }
        };
    }

    TdMob create(TdWorld w, int wave);
}

