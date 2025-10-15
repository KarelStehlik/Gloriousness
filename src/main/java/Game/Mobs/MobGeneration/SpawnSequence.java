package Game.Mobs.MobGeneration;

public final class SpawnSequence {

    BloonNew bloonType;
    int count;
    int beginTick;
    int interval;

    public SpawnSequence(BloonNew bloonType, int count, int beginTick, int interval) {
        this.bloonType = bloonType;
        this.count = count;
        this.beginTick = beginTick;
        this.interval = interval;
    }

    public void onTick(int tick, Wave w) {
        if (tick >= beginTick && tick < beginTick + count * interval
                && (tick - beginTick) % interval == 0) {
            w.add(bloonType.create(w.world, w.waveNum));
        }
    }

    public boolean done(int tick) {
        return tick >= beginTick + count * interval;
    }

}