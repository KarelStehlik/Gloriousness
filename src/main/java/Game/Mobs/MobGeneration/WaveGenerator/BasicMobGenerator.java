package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.Purple;
import Game.Mobs.SpecificMobs.basicaf.Blue;
import Game.Mobs.SpecificMobs.basicaf.Green;
import Game.Mobs.SpecificMobs.basicaf.Pink;
import Game.Mobs.SpecificMobs.basicaf.Red;
import Game.Mobs.SpecificMobs.basicaf.Yellow;
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
        int blooncount=(int)Math.round((wave*wave*1.25f+30)/ Math.pow(strength,2));
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
            case 7 ->{
                return new SpawnSequence(Purple::new, blooncount, beginTime, interval);
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
        if (temp >= 7) {
            strongest = 7;
        } else {
            strongest = Data.gameMechanicsRng.nextInt(temp, 7);
            if(strongest==6){
                if(wave<3){
                    //purple bloon is strongest, but that is too much for round 1-3
                    strongest = 5;
                }else{
                    strongest=7;
                }
            }
        }
        SpawnSequence[] sequence = new SpawnSequence[bloonkindcount];
        for (int i = 0; i < bloonkindcount; i++) {
            sequence[i]=genPart(strongest-i,wave,(bloonkindcount-1-i)*150);
            if(strongest==7)
                strongest--;
        }

        return sequence;
    }
}
