package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.*;
import Game.Mobs.SpecificMobs.basicaf.Blue;
import Game.Mobs.SpecificMobs.basicaf.Green;
import Game.Mobs.SpecificMobs.basicaf.Pink;
import Game.Mobs.SpecificMobs.basicaf.Red;
import Game.Mobs.SpecificMobs.basicaf.Yellow;
import GlobalUse.Data;

//sends purely basic bloons - red to pink
public class IntermediateMobGenerator implements WaveGenerator {
    private static int validFromWave = 9;
    private static int validToWave = 40;

    public IntermediateMobGenerator() {

    }

    @Override
    public int validFromWave() {
        return validFromWave;
    }

    @Override
    public int validToWave() {
        return validToWave;
    }
    enum bloonStrength{;
        public static final int Purple=6,Black=8,TigerG=16,TigerP=19,Lead=24,Ceramic=28;
    }
    private SpawnSequence genPart(int strength, float wave, int beginTime){
        int interval;
        //technically this shouldn't really happen because it's not valid at that wave but validity is more of a suggestion than a hard rule
        if(wave==0){
            interval=15;
        }else if (wave<20){
            interval = (int) Data.gameMechanicsRng.nextFloat(1, 15 * strength / (wave / 3.0f));
        }else{
            interval=1;
        }
        //I somehow fully bolieve this will yield best results
        int blooncount=(int)Math.round((wave*wave*1.25f+30)/ Math.pow(strength,2));
        switch(strength){
            case bloonStrength.Purple -> {
                return new SpawnSequence(Purple::new, blooncount, beginTime, interval);
            }
            case bloonStrength.Black ->{
                return new SpawnSequence(Black::new, blooncount, beginTime, interval);
            }
            case bloonStrength.TigerG ->{
                return new SpawnSequence(TigerG::new, blooncount, beginTime, interval);
            }
            case bloonStrength.TigerP ->{
                return new SpawnSequence(TigerP::new, blooncount, beginTime, interval);
            }
            case bloonStrength.Lead ->{
                return new SpawnSequence(Lead::new, blooncount, beginTime, interval);
            }
            case bloonStrength.Ceramic ->{
                return new SpawnSequence(Ceramic::new, blooncount, beginTime, interval);
            }
            default -> {
                return null;
            }
        }
    }

    //float for potential difficulty setting
    public SpawnSequence[] generate(float wave) {
        int bloonkindcount = Math.min((int) (wave-7) / 5 , 3);
        int strongest;
        int temp=(int)Math.ceil( -9+wave);
        if (temp >= 20) {
            strongest = 20;
        } else{
            //strongest is minimum strength of the bloon, so strongest 11 means at least tiger bloon
            strongest=Data.gameMechanicsRng.nextInt(temp, (int)Math.min(30,wave));
        }
        SpawnSequence[] sequence = new SpawnSequence[bloonkindcount];
        final int [] bloonStrengthList={bloonStrength.Purple,bloonStrength.Black,bloonStrength.TigerG,bloonStrength.TigerP,bloonStrength.Lead,bloonStrength.Ceramic};
        for (int i = bloonStrengthList.length-1; bloonkindcount>0; i--) {
            if(bloonStrengthList[i-1]<strongest) {
                sequence[bloonkindcount-1] = genPart(bloonStrengthList[i], wave, (bloonkindcount-1) * 150);
                bloonkindcount--;
            }
        }

        return sequence;
    }
}
