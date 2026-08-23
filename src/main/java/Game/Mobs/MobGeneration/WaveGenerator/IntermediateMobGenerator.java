package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.*;
import Game.Mobs.SpecificMobs.basicaf.Blue;
import Game.Mobs.SpecificMobs.basicaf.Green;
import Game.Mobs.SpecificMobs.basicaf.Pink;
import Game.Mobs.SpecificMobs.basicaf.Red;
import Game.Mobs.SpecificMobs.basicaf.Yellow;
import GlobalUse.Data;
import GlobalUse.Log;
import windowStuff.Controls.MouseDetect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static Game.Mobs.MobGeneration.BloonNew.BloonNewRegrow;

//sends purely basic bloons - red to pink
public class IntermediateMobGenerator implements WaveGenerator {
    private static int validFromWave = 9;
    private static int validToWave = 40;
    public float regrowChance=1/4f;

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
        public static final int Purple=6,Black=7,TigerG=9,TigerP=10,Lead=15,Ceramic=19;
    }
    private SpawnSequence genPart(int strength, float wave, int beginTime){
        int interval;
        boolean regrow=Data.gameMechanicsRng.nextFloat()<=regrowChance;
        float bloonCountMod=1;
        //technically this shouldn't really happen because it's not valid at that wave but validity is more of a suggestion than a hard rule
        if (wave<40){
            interval = (int) Data.gameMechanicsRng.nextFloat(1, 15 * strength / (wave));
            bloonCountMod*=1+interval/(15 * strength / (wave)+wave-9);
        }else{
            interval=1;
        }
        if(regrow){
            bloonCountMod*=0.8f;
        }
        //I somehow fully bolieve this will yield best results
        int blooncount=(int)Math.round((wave*wave*3.25+90)/ Math.pow(strength,2.5)*bloonCountMod);
        if(interval>1500/blooncount){
            interval=Math.max(1,1500/blooncount);
        }
        blooncount*=1+interval/(3*strength);
        switch(strength){
            case bloonStrength.Purple -> {
                return new SpawnSequence(BloonNewRegrow(Purple::new,regrow), blooncount, beginTime, interval);
            }
            case bloonStrength.Black ->{
                return new SpawnSequence(BloonNewRegrow(Black::new,regrow), blooncount, beginTime, interval);
            }
            case bloonStrength.TigerG ->{
                return new SpawnSequence(BloonNewRegrow(TigerG::new,regrow), blooncount, beginTime, interval);
            }
            case bloonStrength.TigerP ->{
                return new SpawnSequence(BloonNewRegrow(TigerP::new,regrow), blooncount, beginTime, interval);
            }
            case bloonStrength.Lead ->{
                return new SpawnSequence(BloonNewRegrow(Lead::new,regrow), blooncount, beginTime, interval);
            }
            case bloonStrength.Ceramic ->{
                return new SpawnSequence(BloonNewRegrow(Ceramic::new,regrow), blooncount, beginTime, interval);
            }
            default -> {
                System.exit(556);
                return null;
            }
        }
    }

    //float for potential difficulty setting
    public ArrayList<SpawnSequence> generate(float wave) {
        int [] bloonStrengthList={bloonStrength.Purple,bloonStrength.Black,bloonStrength.TigerG,bloonStrength.TigerP,bloonStrength.Lead,bloonStrength.Ceramic};
        bloonStrengthList= Arrays.stream(bloonStrengthList).sorted().toArray();

        int bloonkindcount = Math.min((int)Math.ceil((wave-7) / 7f) , 3);
        int strongest;
        int temp=(int)Math.max( -9+wave,bloonStrengthList[bloonkindcount-1]);
        if (temp >= bloonStrengthList[bloonStrengthList.length-1]) {
            strongest = bloonStrengthList[bloonStrengthList.length-1];
        } else{
            //strongest at least the strength of the strongest bloon
            strongest=Data.gameMechanicsRng.nextInt(temp, Math.min(bloonStrengthList[bloonStrengthList.length-1],(int)(temp*1.5)));
        }
        ArrayList<SpawnSequence> sequence=new ArrayList<SpawnSequence>(bloonkindcount);
        for (int i = bloonStrengthList.length-1; bloonkindcount>0; i--) {
            if(i==0||bloonStrengthList[i]<=strongest) {
                sequence.add( genPart(bloonStrengthList[i], wave, (bloonkindcount-1) * 350));
                bloonkindcount--;
            }
        }

        return sequence;
    }
}
