package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.Blue;
import Game.Mobs.SpecificMobs.Green;
import Game.Mobs.SpecificMobs.Pink;
import Game.Mobs.SpecificMobs.Red;
import Game.Mobs.SpecificMobs.Yellow;
import GlobalUse.Data;

//sends purely basic bloons - red to pink
public class BasicMobGenerator implements WaveGenerator {
    private static int validFromWave = 0;
    private static int validToWave = 16;

    public BasicMobGenerator() {

    }

    @Override
    public int validFromWave() {
        return validFromWave;
    }

    @Override
    public int validToWave() {
        return validToWave;
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
        int blooncount=(int)Math.round((wave*wave+25)/ Math.pow(strength,1.75f));
        switch(strength){
            case 1 -> {
                return new SpawnSequence(Red::new, blooncount, beginTime, interval);
            }
            case 2 ->{
                return new SpawnSequence(Blue::new, blooncount, beginTime, interval);
            }
            case 3 ->{
                return new SpawnSequence(Green::new, blooncount, beginTime, interval);
            }
            case 4 ->{
                return new SpawnSequence(Yellow::new, blooncount, beginTime, interval);
            }
            case 5 ->{
                return new SpawnSequence(Pink::new, blooncount, beginTime, interval);
            }
            default -> {
                return null;
            }
        }
    }

    //float for potential difficulty setting
    public SpawnSequence[] generate(float wave) {
        int bloonkindcount = Math.min((int) wave / 5 + 1, 3);
        int strongest;
        int temp=(int)Math.ceil( (wave+1)/3);
        if (temp >= 5) {
            strongest = 5;
        } else {
            strongest = Data.gameMechanicsRng.nextInt(temp, 6);
        }
        SpawnSequence[] sequence = new SpawnSequence[bloonkindcount];
        for (int i = bloonkindcount; i > 0; i--) {
            sequence[i-1]=genPart(strongest-i+1,wave,(bloonkindcount-i)*150);
        }

        return sequence;
    }
}
