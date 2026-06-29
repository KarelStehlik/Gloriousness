package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;
import Game.Mobs.SpecificMobs.moabs.*;
import GlobalUse.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

//sends purely basic bloons - red to pink
public class MoabGenerator implements WaveGenerator {
    private static int validFromWave = 17;
    private static int validToWave = 999;

    public MoabGenerator() {

    }

    @Override
    public int validFromWave() {
        return validFromWave;
    }

    @Override
    public int validToWave() {
        return validToWave;
    }
    enum bloonStrength{
        Balloon(10),BigBalloon(15),BlueMoab(17),Bombarder(22),Capsule(8),Moab(20),OrkShip(10),Purpcart(9),SmallMoab(15),MultiMoabCore(25);
        public int str;
        bloonStrength(int str){
            this.str=str;
        }
    }
    private SpawnSequence genPart(bloonStrength moab, float wave, int beginTime){

        int interval;
        if ((350 * moab.str / (wave))>1){
            interval = (int)(350 * moab.str / (wave));
        }else{
            interval=1;
        }
        //I somehow fully bolieve this will yield best results
        int blooncount=(int)Math.ceil((wave*wave*0.2f)/ Math.pow(moab.str,2));
        if(blooncount*interval>2000){
            interval=2000/blooncount;
        }
        switch(moab){
            case Balloon -> {
                return new SpawnSequence(Balloon::new, blooncount, beginTime, interval);
            }
            case BigBalloon ->{
                return new SpawnSequence(BigBalloon::new, blooncount, beginTime, interval);
            }
            case BlueMoab ->{
                return new SpawnSequence(BlueMoab::new, blooncount, beginTime, interval);
            }
            case Bombarder ->{
                return new SpawnSequence(Bombarder::new, blooncount, beginTime, interval);
            }
            case Capsule ->{
                return new SpawnSequence(Capsule::new, blooncount, beginTime, interval);
            }
            case Moab ->{
                return new SpawnSequence(Moab::new, blooncount, beginTime, interval);
            }
            case OrkShip ->{
                return new SpawnSequence(OrkShip::new, blooncount, beginTime, interval);
            }
            case Purpcart ->{
                return new SpawnSequence(Purpcart::new, blooncount, beginTime, interval);
            }
            case SmallMoab ->{
                return new SpawnSequence(SmallMoab::new, blooncount, beginTime, interval);
            }
            case MultiMoabCore ->{
                return new SpawnSequence(MultiMoabCore::new, blooncount, beginTime, interval);
            }
            default -> {
                System.exit(556);
                return null;
            }
        }
    }

    //float for potential difficulty setting
    public ArrayList<SpawnSequence> generate(float wave) {
        bloonStrength[] bloonStrengthList=bloonStrength.values();
        Arrays.sort(bloonStrengthList, Comparator.comparingInt((bloonStrength a)->a.str));

        int bloonkindcount = Math.min((int)Math.ceil((wave-10) / 15f) , 3);
        int strongest;
        int temp=(int)Math.min( wave/3,bloonStrengthList[bloonkindcount-1].str);
        if (temp >= bloonStrengthList[bloonStrengthList.length-1].str) {
            strongest = bloonStrengthList[bloonStrengthList.length-1].str;
        } else{
            //strongest is sharply lower than the strength of the strongest bloon, so strongest 10 means at least tiger bloon
            strongest=Data.gameMechanicsRng.nextInt(temp, Math.min(bloonStrengthList[bloonStrengthList.length-1].str,temp+3));
        }
        ArrayList<SpawnSequence> sequence = new ArrayList<SpawnSequence>(bloonkindcount);
        for (int i = bloonStrengthList.length-1; bloonkindcount>0; i--) {
            if(i==0||bloonStrengthList[i-1].str<=strongest) {
                sequence.add( genPart(bloonStrengthList[i], wave, (bloonkindcount-1) * 150));
                bloonkindcount--;
            }
        }

        return sequence;
    }
}