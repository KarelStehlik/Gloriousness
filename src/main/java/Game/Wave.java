package Game;

import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Mobs.Black;
import Game.Mobs.Blue;
import Game.Mobs.Ceramic;
import Game.Mobs.GoldenBloon;
import Game.Mobs.Green;
import Game.Mobs.Lead;
import Game.Mobs.Moab;
import Game.Mobs.MultiMoabCore;
import Game.Mobs.Pink;
import Game.Mobs.Red;
import Game.Mobs.ShieldBloon;
import Game.Mobs.SmallMoab;
import Game.Mobs.TdMob;
import Game.Mobs.TdMob.Stats;
import Game.Mobs.Yellow;
import general.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wave implements TickDetect {

  private static final Map<Integer, Integer> MobsAliveFromEachWave = new HashMap<>();

  public static void increaseMobsInWave(int waveNum) {
    MobsAliveFromEachWave.put(waveNum, MobsAliveFromEachWave.get(waveNum) + 1);
  }

  public static void decreaseMobsInWave(int waveNum) {
    int n = MobsAliveFromEachWave.get(waveNum) - 1;
    MobsAliveFromEachWave.put(waveNum, n);
  }

  public final int waveNum;
  private final World world;
  private final float scaling;
  private int elapsed = 0;
  private final SpawnSequence[] spawns;

  public float getElapsedMillis() {
    return elapsedMillis;
  }

  private final float elapsedMillis = 0;

  private Wave(World world, int n, SpawnSequence[] s) {
    MobsAliveFromEachWave.put(n, 0);
    this.world = world;
    this.waveNum = n;
    scaling = getScaling(n);
    spawns = s;
  }

  @Override
  public void onGameTick(int tick) {
    for (var sp : spawns) {
      sp.onTick(elapsed, this);
    }
    elapsed++;
  }

  @Override
  public void delete() {

  }

  @Override
  public boolean WasDeleted() {
    if(!MobsAliveFromEachWave.containsKey(waveNum)){
      return true;
    }
    if(MobsAliveFromEachWave.get(waveNum)==0 && Arrays.stream(spawns).allMatch(sp -> sp.done(elapsed))){
      MobsAliveFromEachWave.remove(waveNum);
      return true;
    }
    return false;
  }

  private void add(TdMob e) {
    final float hpScaling = scaling;
    final float spdScaling = (float) Math.pow(scaling, 0.1);
    e.addBuff(
        new StatBuff<TdMob>(Type.MORE, Stats.health,
            hpScaling));
    e.addBuff(
        new StatBuff<TdMob>(Type.MORE, Stats.speed,
            spdScaling));
    world.addEnemy(e);
  }

  public static Wave get(World w, int num) {
    if (num >= waves.length) {
      return new Wave(w, num, waves[Data.gameMechanicsRng.nextInt(waves.length - 5, waves.length)]);
    }
    return new Wave(w, num, waves[num]);
  }

  @FunctionalInterface
  private interface BloonNew {

    TdMob create(World w, int wave);
  }

  private static final class SpawnSequence {

    BloonNew bloonType;
    int count;
    int beginTick;
    int interval;

    private SpawnSequence(BloonNew bloonType, int count, int beginTick, int interval) {
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

  private static float getScaling(int wave) {
    return
        1 +
            Math.max(0, wave - 40) * .05f +
            Math.max(0, wave - 80) * .1f +
            Math.max(0, wave - 100) * .1f +
            Math.max(0, wave - 150) * .5f +
            Math.max(0, wave - 300) * 1f
        ;
  }

  private static final SpawnSequence[][] waves = new SpawnSequence[][]{
      // 1
      new SpawnSequence[]{
          new SpawnSequence(Red::new, 20, 0, 5),
      },
      // 2
      new SpawnSequence[]{
          new SpawnSequence(Blue::new, 20, 0, 5),
      },
      // 3
      new SpawnSequence[]{
          new SpawnSequence(Green::new, 20, 0, 7),
      },
      // 4
      new SpawnSequence[]{
          new SpawnSequence(Red::new, 50, 0, 2),
      },
      // 5
      new SpawnSequence[]{
          new SpawnSequence(Yellow::new, 20, 0, 14),
      },
      // 6
      new SpawnSequence[]{
          new SpawnSequence(Pink::new, 20, 0, 14),
      },
      // 7
      new SpawnSequence[]{
          new SpawnSequence(Yellow::new, 20, 0, 14),
          new SpawnSequence(Pink::new, 20, 7, 14),
      },
      // 8
      new SpawnSequence[]{
          new SpawnSequence(Pink::new, 10, 0, 1),
      },
      // 9
      new SpawnSequence[]{
          new SpawnSequence(Black::new, 10, 0, 5),
      },
      // 10
      new SpawnSequence[]{
          new SpawnSequence(Black::new, 25, 0, 2),
          new SpawnSequence(GoldenBloon::new, 1, 300, 1),
      },
      // 11
      new SpawnSequence[]{
          new SpawnSequence(Pink::new, 20, 0, 30),
          new SpawnSequence(Pink::new, 20, 302, 15),
          new SpawnSequence(Pink::new, 20, 454, 7),
          new SpawnSequence(Pink::new, 20, 580, 3),
      },
      // 12
      new SpawnSequence[]{
          new SpawnSequence(Red::new, 20, 0, 30),
          new SpawnSequence(Green::new, 20, 302, 15),
          new SpawnSequence(Black::new, 20, 454, 7),
          new SpawnSequence(Pink::new, 20, 580, 3),
      },
      // 13
      new SpawnSequence[]{
          new SpawnSequence(Lead::new, 50, 0, 20),
      },
      // 14
      new SpawnSequence[]{
          new SpawnSequence(Ceramic::new, 5, 0, 100),
      },
      // 15
      new SpawnSequence[]{
          new SpawnSequence(Ceramic::new, 10, 0, 50),
      },
      // 16
      new SpawnSequence[]{
          new SpawnSequence(Ceramic::new, 20, 0, 20),
      },
      // 17
      new SpawnSequence[]{
          new SpawnSequence(Ceramic::new, 50, 0, 4),
      },
      // 18
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 1, 0, 1),
      },
      // 19
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 1, 100, 1),
          new SpawnSequence(Ceramic::new, 10, 0, 50),
      },
      // 20
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 5, 0, 100),
          new SpawnSequence(GoldenBloon::new, 1, 250, 1),
      },
      // 21
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 10, 0, 20),
      },
      // 22
      new SpawnSequence[]{
          new SpawnSequence(Ceramic::new, 50, 0, 1),
          new SpawnSequence(Ceramic::new, 50, 0, 1),
          new SpawnSequence(Ceramic::new, 50, 0, 1),
          new SpawnSequence(Ceramic::new, 50, 0, 1),
      },
      // 23
      new SpawnSequence[]{
          new SpawnSequence(Lead::new, 50, 0, 1),
          new SpawnSequence(Red::new, 50, 40, 1),
          new SpawnSequence(Blue::new, 50, 80, 1),
          new SpawnSequence(Green::new, 50, 120, 1),
          new SpawnSequence(Black::new, 50, 160, 1),
          new SpawnSequence(Ceramic::new, 50, 200, 1),
          new SpawnSequence(Yellow::new, 50, 240, 1),
          new SpawnSequence(Pink::new, 50, 280, 1),
      },
      // 24
      new SpawnSequence[]{
          new SpawnSequence(ShieldBloon::new, 15, 0, 100),
          new SpawnSequence(Red::new, 1000, 0, 1),
      },
      // 25
      // 26
      // 27
      // 28
      // 29
      // 30
      // 31
      new SpawnSequence[]{
          new SpawnSequence(ShieldBloon::new, 10, 0, 35),
          new SpawnSequence(Lead::new, 50, 0, 1),
          new SpawnSequence(Red::new, 50, 40, 1),
          new SpawnSequence(Blue::new, 50, 80, 1),
          new SpawnSequence(Green::new, 50, 120, 1),
          new SpawnSequence(Black::new, 50, 160, 1),
          new SpawnSequence(Ceramic::new, 50, 200, 1),
          new SpawnSequence(Yellow::new, 50, 240, 1),
          new SpawnSequence(Pink::new, 50, 280, 1),
      },
      // 32
      // 33
      // 34
      // 35
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 20, 0, 60),
      },
      // 36
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 20, 0, 50),
          new SpawnSequence(ShieldBloon::new, 10, 0, 100),
          new SpawnSequence(Lead::new, 1000, 0, 1),
      },
      // 37
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 5, 0, 100),
          new SpawnSequence(SmallMoab::new, 5, 500, 50),
          new SpawnSequence(SmallMoab::new, 5, 750, 25),
          new SpawnSequence(SmallMoab::new, 10, 875, 13),
          new SpawnSequence(SmallMoab::new, 10, 950, 7),
      },
      // 38
      new SpawnSequence[]{
          new SpawnSequence(Ceramic::new, 400, 0, 1),
          new SpawnSequence(Ceramic::new, 400, 0, 1),
          new SpawnSequence(Ceramic::new, 400, 0, 1),
          new SpawnSequence(Ceramic::new, 400, 0, 1),
          new SpawnSequence(Ceramic::new, 400, 0, 1),
      },
      // 39
      new SpawnSequence[]{
          new SpawnSequence(SmallMoab::new, 40, 0, 10),
      },
      // 40
      new SpawnSequence[]{
          new SpawnSequence(Moab::new, 1, 0, 1),
      },
      // 41
      // 42
      // 43
      // 44
      // 45
      // 46
      // 47
      // 48
      // 49
      // 50
      // 51
      // 52
      // 53
      // 54
      // 55
      // 56
      // 57
      // 58
      // 59
      // 60
      new SpawnSequence[]{
          new SpawnSequence(MultiMoabCore::new, 1, 0, 1),
      },
      // 61
      // 62
      // 63
      // 64
      // 65
      // 66
      // 67
      // 68
      // 69
      // 70
      // 71
      // 72
      // 73
      // 74
      // 75
      // 76
      // 77
      // 78
      // 79
      // 80
      // 81
      // 82
      // 83
      // 84
      // 85
      // 86
      // 87
      // 88
      // 89
      // 90
      // 91
      // 92
      // 93
      // 94
      // 95
      // 96
      // 97
      // 98
      // 99
      // 100
  };
}
