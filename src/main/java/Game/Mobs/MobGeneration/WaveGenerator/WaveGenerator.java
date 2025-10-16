package Game.Mobs.MobGeneration.WaveGenerator;

import Game.Mobs.MobGeneration.SpawnSequence;

public interface WaveGenerator {

    int validFromWave();
    int validToWave();


    //float for potential difficulty setting
    public SpawnSequence[] generate(float wave);

}
