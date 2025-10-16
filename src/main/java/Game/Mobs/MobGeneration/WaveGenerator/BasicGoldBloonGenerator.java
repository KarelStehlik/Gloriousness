package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Misc.TdWorld;
import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.GoldenBloon;
import GlobalUse.Data;

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
        incomePerDamage=(float) (1.5/(Math.pow(wave,wave/50)));
//        System.out.println(incomePerDamage+"   "+wave);
    }
    @Override
    public SpawnSequence[] generate(float wave) {
        SpawnSequence[] sequence=new SpawnSequence[1];
        calcValue(wave);
        sequence[0]=new SpawnSequence((TdWorld w, int round)->{
            return new GoldenBloon(w,round, incomePerDamage);
        }, 1, Data.gameMechanicsRng.nextInt(1,250), 50);
        return sequence;
    }
}
