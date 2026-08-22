package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.Purple;
import Game.Mobs.SpecificMobs.basicaf.Blue;
import Game.Mobs.SpecificMobs.basicaf.Green;
import Game.Mobs.SpecificMobs.basicaf.Pink;
import Game.Mobs.SpecificMobs.basicaf.Red;
import Game.Mobs.SpecificMobs.basicaf.Yellow;
import GlobalUse.Data;

import java.util.ArrayList;

import static Game.Mobs.MobGeneration.BloonNew.BloonNewRegrow;

//sends purely basic bloons - red to pink
public class BasicMobGenerator implements WaveGenerator {
    private static int validFromWave = 0;
    private static int validToWave = 16;
    public float regrowChance=1/4f;

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
        boolean regrow=Data.gameMechanicsRng.nextFloat()<=regrowChance;
        float bloonCountMod=1;
        if(regrow){
            if(strength>3&&wave<4){
                strength--;
                if(strength==6){
                    strength--;
                }
            }
            if(strength!=1) bloonCountMod=0.8f;
        }
        //technically this shouldn't really happen because it's not valid at that wave but validity is more of a suggestion than a hard rule
        if(wave==0){
            interval=15;
        }else if (wave<20){
            interval = (int) Data.gameMechanicsRng.nextFloat(1, 15 * strength / (wave / 3.0f));
        }else{
            interval=1;
        }
        //I somehow fully bolieve this will yield best results
        int blooncount=(int)Math.round((wave*wave*3.25f+90)/ Math.pow(strength,2.5)*bloonCountMod);
        if(interval>1200/blooncount){
            interval=Math.max(1,1200/blooncount);
        }
        switch(strength){
            case 1 -> {
                return new SpawnSequence(BloonNewRegrow(Red::new,regrow), blooncount, beginTime, interval);
            }
            case 2 ->{
                return new SpawnSequence(BloonNewRegrow(Blue::new,regrow), blooncount, beginTime, interval);
            }
            case 3 ->{
                return new SpawnSequence(BloonNewRegrow(Green::new,regrow), blooncount, beginTime, interval);
            }
            case 4 ->{
                return new SpawnSequence(BloonNewRegrow(Yellow::new,regrow), blooncount, beginTime, interval);
            }
            case 5 ->{
                return new SpawnSequence(BloonNewRegrow(Pink::new,regrow), blooncount, beginTime, interval);
            }
            case 7 ->{
                return new SpawnSequence(BloonNewRegrow(Purple::new,regrow), blooncount, beginTime, interval);
            }
            default -> {
                return null;
            }
        }
    }

    //float for potential difficulty setting
    public ArrayList<SpawnSequence> generate(float wave) {
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
        ArrayList<SpawnSequence> sequence=new ArrayList<SpawnSequence>(bloonkindcount);
        for (int i = 0; i < bloonkindcount; i++) {
            sequence.add(genPart(strongest-i,wave,(bloonkindcount-1-i)*150));
            if(strongest==7)
                strongest--;
        }

        return sequence;
    }
}
