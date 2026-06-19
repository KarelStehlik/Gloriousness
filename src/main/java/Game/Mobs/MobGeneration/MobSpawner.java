package Game.Mobs.MobGeneration;

import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.Mobs.MobGeneration.WaveGenerator.WaveGenerator;
import GlobalUse.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MobSpawner {

    public int waveNum = 0;
    public ArrayList<WaveGenerator> generators = new ArrayList();
    public ArrayList<WaveGenerator> goldGenerators = new ArrayList();
    List<Wave> waves = new ArrayList<>(1);
    private TdWorld world;

    public MobSpawner(TdWorld world) {
        this.world = world;
    }

    public float difficultyMult=1;
    public float difficultyAdd=0;
    public int maxWave = Integer.MAX_VALUE;
    public void run() {
        for (Iterator<Wave> iterator = waves.iterator(); iterator.hasNext(); ) {
            Wave x = iterator.next();
            x.onGameTick(world.getTick());
            if (x.wasDeleted()) {
                world.endWave(x.waveNum);
                iterator.remove();
            }
        }

        if (waves.isEmpty()) {
            world.beginWave();
            beginWave();
        }
    }

    public void beginWave() {
        if(waveNum>= maxWave){
            Game.get().startIntroScreen();
            return;
        }
        float waveDiff=waveNum*difficultyMult+difficultyAdd;
        if((waveNum+1)%5==0){
            Collections.shuffle(goldGenerators);
            for (WaveGenerator gen : goldGenerators) {
                if (gen.validFromWave() <= waveDiff && gen.validToWave() >= waveDiff) {
                    waves.add(new Wave(world, waveNum, gen.generate(waveDiff)));
                    break;
                }
            }
        }
        Collections.shuffle(generators);
        for (WaveGenerator gen : generators) {
            if (gen.validFromWave() <= waveDiff && gen.validToWave() >= waveDiff) {
                waves.add(new Wave(world, waveNum, gen.generate(waveDiff)));
                waveNum++;
                return;
            }
        }
        waves.add(Wave.get(world, (int) waveDiff));
        Log.write("generating wave using old system waves");
        waveNum++;
    }
}