package Game.Mobs.MobGeneration;

public interface WaveGenerator {

    int validFromWave();
    int validToWave();


    //float for potential difficulty setting
    public SpawnSequence[] generate(float wave);

}
