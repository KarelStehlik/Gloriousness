package Game.Mobs.MobGeneration;

import Game.Misc.TdWorld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MobSpawner {

    public int waveNum = 0;
    public ArrayList<WaveGenerator> generators = new ArrayList();
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
        for (WaveGenerator gen : generators) {
            if (gen.validFromWave() <= waveNum && gen.validToWave() >= gen.validToWave()) {
                waves.add(new Wave(world, waveNum, gen.generate(waveNum)));
                waveNum++;
                return;
            }
        }
        waves.add(Wave.get(world, waveNum));
        waveNum++;
    }
}