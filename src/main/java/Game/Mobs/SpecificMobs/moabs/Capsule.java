package Game.Mobs.SpecificMobs.moabs;

import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.Trail;
import Game.Mobs.MobClasses.TdMob;
import Game.Mobs.SpecificMobs.TigerG;
import Game.Mobs.SpecificMobs.TigerP;
import Game.WorldStuff.TdWorld;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.TransformAnimation;

import java.util.ArrayList;
import java.util.List;

public class Capsule extends TdMob {
    private static final int spawnCount=5;
    private static final List<ChildSpawner> spawns = getSpawns();
    private final ImageData trailIm = Graphics.getImage("fire");
    private Trail trail;

    public Capsule(TdWorld world, int wave) {
        super(world, wave);
    }

    public Capsule(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        createImage( "Capsule");
        Trail parentTrail=new Trail(world.getBs(), r ->new Sprite(trailIm,sprite.getLayer()-1).setSize(30,30).setRotation(r+90).
                playAnimation(new TransformAnimation(1f).setOpacityScaling(-0.01f)).setDeleteOnAnimationEnd(true),30,55);
        trail=new Trail(parentTrail,getX(),getY());
    }

    public void onGameTick(int tick){
        super.onGameTick(tick);
        trail.tick(this);
    }

    private static List<ChildSpawner> getSpawns() {
        List<ChildSpawner> spawn=new ArrayList<>(spawnCount);
        for (int i = 0; i < spawnCount; i++) {
            if (i<4) {
                spawn.add(TigerG::new);
            } else {
                spawn.add(TigerP::new);
            }
        }
        return spawn;
    }

    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 160.0f;
    stats[Stats.speed] = 3.75f;
    stats[Stats.health] = 20f;
    stats[Stats.value] = 100f;
    stats[Stats.damageTaken] = 1f;
    stats[Stats.spawns] = 1f;
    stats[Stats.maxHealth] = 1f;
  }
  // end of generated stats

    @Override
    public boolean isMoab() {
        return true;
    }


    @Override
    protected List<TdMob.ChildSpawner> children() {
        return spawns;
    }

    @Override
    public int getChildrenSpread() {
        return 150;
    }
}
