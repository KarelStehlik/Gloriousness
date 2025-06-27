package Game.Buffs;

import Game.BulletLauncher;
import Game.Projectile;
import general.Log;
import org.joml.Vector2f;

public class SideWeapon implements AttackEffect {
    private Vector2f displacement;
    private boolean relative;// positive X,Y goes to the left upper corner if facing up
    public SideWeapon(Vector2f displacement){
        this(displacement,true);
    }
    public SideWeapon(Vector2f displacement,boolean displaceRelative){
        this.displacement=displacement;
        this.relative=displaceRelative;
    }
    @Override
    public void mod(BulletLauncher target,boolean cooldown,float angle) {
        if(cooldown){
            Projectile p= target.attack(angle,false);
            if(!relative){
                p.moveRelative(displacement.x,displacement.y);
            }else{
                float angl1=(float)(Math.sin(2*Math.PI*(angle)/360));
                float angl2=(float)(Math.cos(2*Math.PI*(angle)/360));
                float displaceX= displacement.x*angl1+
                        displacement.y*angl2;
                float displaceY=displacement.y*angl1-
                        displacement.x*angl2;

                p.moveRelative(displaceX
                        ,displaceY);
            }
        }
    }
}
