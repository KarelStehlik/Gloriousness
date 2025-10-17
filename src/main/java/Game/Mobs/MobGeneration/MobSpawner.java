package Game.Mobs.MobGeneration;

import Game.Misc.TdWorld;
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

    public void run() {
        for (Iterator<Wave> iterator = waves.iterator(); iterator.hasNext(); ) {
            Wave x = iterator.next();
            x.onGameTick(world.getTick());
            if (x.WasDeleted()) {
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
        if((waveNum+1)%5==0){
            Collections.shuffle(goldGenerators);
            for (WaveGenerator gen : goldGenerators) {
                if (gen.validFromWave() <= waveNum && gen.validToWave() >= waveNum) {
                    waves.add(new Wave(world, waveNum, gen.generate(waveNum)));
                    break;
                }
            }
        }
        Collections.shuffle(generators);
        for (WaveGenerator gen : generators) {
            if (gen.validFromWave() <= waveNum && gen.validToWave() >= waveNum) {
                waves.add(new Wave(world, waveNum, gen.generate(waveNum)));
                break;
            }
        }
        if(waves.isEmpty()){
            waves.add(Wave.get(world, waveNum));
            Log.write("generating wave using old system waves");
        }
        waveNum++;
    }
}