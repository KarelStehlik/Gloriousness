package Game.Common.Buffs.Buff;

import Game.Common.Buffs.Modifier.Modifier;
import Game.Common.Projectile;
import Game.Common.Turrets.Turret;
import Game.Misc.BasicCollides;
import Game.Misc.Game;
import GlobalUse.Log;
import GlobalUse.Util;
import com.sun.source.tree.ArrayAccessTree;

import java.util.ArrayList;

/*thing that makes mortar shots go up
 */
public class SkyShot extends DefaultBuff<Projectile> {
    public float strength,duration,physicalLength,physicalAtSpeed, currentSpeed;
    /*
        strength= how strong it gets pulled upwards
        dsuration= in seconds how long it gets pulled for before descending
        physicalLength= distance for which it can physically collide with enemies for
     */
    private ArrayList<Modifier<Projectile>> mods=new ArrayList<>();
    public SkyShot(float strength, float duration, float physicalLength){

        this.strength=strength;
        this.duration=duration;
        this.physicalLength=physicalLength;
        this.physicalAtSpeed=(float) -(Math.sqrt(strength* Math.pow(duration,2) -2*physicalLength)*Math.sqrt(strength));
        this.currentSpeed =strength*duration;

    }
    public SkyShot(float strength, float duration, float physicalLength,ArrayList<Modifier<Projectile>> mods){

        this.strength=strength;
        this.duration=duration;
        this.physicalLength=physicalLength;
        this.physicalAtSpeed=(float) -(Math.sqrt(strength* Math.pow(duration,2) -2*physicalLength)*Math.sqrt(strength));
        this.currentSpeed =strength*duration;
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
            target.moveRelative(0, currentSpeed *Game.secondsPerFrame);
            currentSpeed -=strength* Game.secondsPerFrame;
            target.getSprite().setRotation(Util.get_rotation(target.vx,target.vy+currentSpeed*Game.secondsPerFrame)-90);
            if(currentSpeed <physicalAtSpeed){
                physicalAtSpeed=-Float.MAX_VALUE;
                for(Modifier<Projectile> mod:mods){
                    mod.mod(target);
                }

            }
        }


    }
}
