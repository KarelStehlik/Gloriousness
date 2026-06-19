package Game.Mobs.MobGeneration;

import Game.WorldStuff.TdWorld;
import Game.Mobs.MobClasses.TdMob;

@FunctionalInterface
public interface BloonNew {

    TdMob create(TdWorld w, int wave);
}