package Game.Mobs.SpecificMobs.moabs;

import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Buffs.Buff.Trail;
import Game.Misc.Player;
import Game.Mobs.MobClasses.TdMob;
import Game.Mobs.SpecificMobs.Ceramic;
import Game.Mobs.SpecificMobs.Purple;
import Game.Mobs.SpecificMobs.TigerG;
import Game.Mobs.SpecificMobs.TigerP;
import Game.WorldStuff.TdWorld;
import windowStuff.GraphicsOnly.Graphics;
import windowStuff.GraphicsOnly.ImageData;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.TransformAnimation;

import java.util.ArrayList;
import java.util.List;

public class Purpcart extends TdMob {
    private static final int spawnCount=5;
    private static final List<ChildSpawner> spawns = getSpawns();
    private final ImageData trailIm = Graphics.getImage("laserpurp");
    private Trail trail;

    public Purpcart(TdWorld world, int wave) {
        super(world, wave);
    }

    public Purpcart(TdMob parent) {
        super(parent);
    }

    @Override
    protected void init() {
        //the base speed of this bloon has to be bigger so that the childbloons don't yeet themselves as hard when they get this thing's buff
        this.addBuff(new StatBuff<TdMob>(StatBuff.Type.MORE,
                Stats.speed, 0.3f));
        createImage("purpcart");
        //default moab is at 25
        sprite.setLayer(22);
        Trail parentTrail=new Trail(world.getBs(),r ->new Sprite(trailIm,sprite.getLayer()-1).setSize(30,30).setRotation(r+90).
                playAnimation(new TransformAnimation(1f).setOpacityScaling(-0.02f)).setDeleteOnAnimationEnd(true),30,40);
        trail=new Trail(parentTrail,getX(),getY());
    }

    @Override
    public void onGameTick(int tick){
        super.onGameTick(tick);
        trail.tick(this);
        this.addBuff(new StatBuff<TdMob>(StatBuff.Type.INCREASED,
                Stats.speed, 0.007f));
    }

    private static List<ChildSpawner> getSpawns() {
        List<ChildSpawner> spawn=new ArrayList<>(spawnCount);
        for (int i = 0; i < spawnCount; i++) {
            spawn.add(Purple::new);
        }
        return spawn;
    }


    // generated stats
  @Override
  public void clearStats() {
    stats[Stats.size] = 200.0f;
    stats[Stats.speed] = 1f;
    stats[Stats.health] = 14f;
    stats[Stats.damageTaken] = 0.7f;
    stats[Stats.value] = 100f;
    stats[Stats.spawns] = 1f;
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
