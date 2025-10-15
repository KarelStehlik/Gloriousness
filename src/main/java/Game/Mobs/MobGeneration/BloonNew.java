package Game.Mobs.MobGeneration;

import Game.Misc.TdWorld;
import Game.Mobs.MobClasses.TdMob;

@FunctionalInterface
public interface BloonNew {

    TdMob create(TdWorld w, int wave);
}