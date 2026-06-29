package Game.Mobs.MobGeneration;

import Game.Common.Buffs.Modifier.Modifier;
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
    private ArrayList<Modifier<Wave>> waveEffects=new ArrayList<>();


    public MobSpawner(TdWorld world) {
        this.world = world;
    }

    public float difficultyMult = 1;
    public float difficultyAdd = 0;
    public int maxWave = Integer.MAX_VALUE;

    public void addWaveEffect(Modifier<Wave> effect){
        waveEffects.add(effect);
    }

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
            beginWave();
        }
    }

    public void beginWave() {
        if (waveNum >= maxWave) {
            Game.get().startIntroScreen();
            return;
        }
        Wave wave=CreateWave();
        for(Modifier<Wave> mod:waveEffects){
            mod.mod(wave);
        }
        waves.add(wave);
        waveNum++;
    }

    public Wave CreateWave() {
        float waveDiff = waveNum * difficultyMult + difficultyAdd;
        if ((waveNum + 1) % 5 == 0) {
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
                return new Wave(world, waveNum, gen.generate(waveDiff));
            }
        }
        Log.write("generating wave using old system waves");
        return Wave.get(world, (int) waveDiff);
    }
}