package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Common.Buffs.Buff.StatBuff;
import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.GoldenBloon;
import GlobalUse.Data;

import java.util.ArrayList;

public class BasicGoldBloonGenerator implements WaveGenerator {


    @Override
    public int validFromWave() {
        return 0;
    }

    @Override
    public int validToWave() {
        return 9999;
    }
    private float incomePerDamage =1;

    private void calcValue(float wave){
        incomePerDamage=(float) (1/(Math.pow(wave,wave/30)));
//        System.out.println(incomePerDamage+"   "+wave);
    }
    @Override
    public ArrayList<SpawnSequence> generate(float wave) {
        ArrayList<SpawnSequence> sequence=new ArrayList<SpawnSequence>(1);
        calcValue(wave);
        sequence.add(new SpawnSequence((TdWorld w, int round)->{
                    GoldenBloon gold=new GoldenBloon(w,round, incomePerDamage, (float) (Math.sqrt(wave)*2));
                    return gold;
        }, 1, Data.gameMechanicsRng.nextInt(1,250), 50));
        return sequence;
    }
}
