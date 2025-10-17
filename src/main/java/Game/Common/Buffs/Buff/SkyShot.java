package Game.Common.Buffs.Buff;

import Game.Common.Buffs.Modifier.Modifier;
import Game.Common.Projectile;
import Game.Common.Projectile.Stats;
import Game.Common.Turrets.Turret;
import Game.Misc.BasicCollides;
import Game.Misc.Game;
import GlobalUse.Constants;
import GlobalUse.Log;
import GlobalUse.RefFloat;
import GlobalUse.Util;
import com.sun.source.tree.ArrayAccessTree;

import java.util.ArrayList;

/*thing that makes mortar shots go up
 */
public class SkyShot extends DefaultBuff<Projectile> {
    public float strength, duration, physicalLength, physicalAtSpeed = Float.NEGATIVE_INFINITY, currentSpeed;
    private float traveled = 0;
    /*
        dsuration= in seconds how long it gets pulled for before descending
        physicalLength= distance for which it can physically collide with enemies for
     */
    private ArrayList<? extends Modifier<Projectile>> mods=new ArrayList<>(1);
    public SkyShot(float physicalLength){

        this.physicalLength=physicalLength;
    }
    public SkyShot(float physicalLength, ArrayList<? extends Modifier<Projectile>> mods){

        this.physicalLength=physicalLength;
        this.mods=mods;
    }
    @Override
    public SkyBuffAggregator makeAggregator() {
        return new SkyBuffAggregator();
    }
    protected class SkyBuffAggregator extends DefaultAggregator<Projectile> {
        @Override
        public void tick(Projectile target) {
            super.tick(target);

            currentSpeed -= strength;

            traveled += currentSpeed;
            if(traveled>physicalLength){
              physicalAtSpeed = -currentSpeed-0.0001f;
              traveled = Float.NEGATIVE_INFINITY;
            }

            if(currentSpeed>=0){
                target.addBuff(new StatBuff<>(StatBuff.Type.INCREASED, Projectile.Stats.size,
                    0.7f / (duration / Game.tickIntervalMillis)));
//                target.getSprite().setOpacity(0.75f+currentSpeed/(strength*duration)*0.25f);
            }else{
                target.addBuff(new StatBuff<>(StatBuff.Type.INCREASED, Projectile.Stats.size,
                    -0.7f / (duration / Game.tickIntervalMillis)));
//                target.getSprite().setOpacity(0.75f-currentSpeed/(strength*duration)*0.25f);
            }

            target.accelerate(0, -strength);

            if(currentSpeed < physicalAtSpeed){
                physicalAtSpeed=Float.NEGATIVE_INFINITY;
                for(Modifier<Projectile> mod:mods){
                    mod.mod(target);
                }

            }
        }

        @Override
        public boolean add(Buff<Projectile> e, Projectile target) {
            assert e instanceof SkyShot;
            SkyShot buff = (SkyShot)e;
            target.setRotation(90);
            buff.duration = target.getStats()[Stats.duration];
            buff.strength = target.getStats()[Stats.speed]/duration*Game.tickIntervalMillis*2;
            buff.currentSpeed = target.getSpeed();
            return super.add(e, target);
        }
    }
}
