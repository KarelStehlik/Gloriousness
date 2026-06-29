package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;

import java.util.ArrayList;

public interface WaveGenerator {

    int validFromWave();
    int validToWave();


    //float for potential difficulty setting
    public ArrayList<SpawnSequence> generate(float wave);

}
