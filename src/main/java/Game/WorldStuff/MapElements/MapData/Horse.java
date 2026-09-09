package Game.WorldStuff.MapElements.MapData;

import Game.Common.Projectile;
import Game.Common.Turrets.Necromancer;
import Game.Common.Turrets.Plane;
import Game.Common.Turrets.Turret;
import Game.Misc.GameObject;
import Game.Misc.TickDetect;
import Game.Mobs.MobClasses.TdMob;
import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import GlobalUse.Constants;
import GlobalUse.Util;
import org.joml.Vector2f;
import org.lwjgl.system.MathUtil;
import windowStuff.GraphicsOnly.Sprite.SingleAnimationSprite;
import windowStuff.GraphicsOnly.Sprite.Sprite;

import java.awt.*;
import java.util.ArrayList;

public class Horse extends GameObject implements TickDetect,Blocker {
    private TdMob.MoveAlongTrack<Horse> movement;
    private final Sprite sprite;
    private ArrayList<Passenger> passengers=new ArrayList<>();
    public Horse(float x,float y,int size,TdWorld world){
        super(x,y,0,0,world);
        var bs = Game.get().getSpriteBatching("main");
        sprite=new Sprite("Horse", Constants.layerInterval.monkey.min-1).addToBs(bs).setSize(size,size).setNaturalWidth();
        sprite.playAnimation(
                new Sprite.FrameAnimation(
                        "Gallop", 1
                ).loop());
        Game.get().addTickable(this);
        resetMovement();
        world.blockers.add(this);
    }
    @Override
    public void onGameTick(int tick) {
        float temp=this.x;
        movement.tick(this);
        sprite.setFlipped(this.x>temp);
        sprite.setPosition(x, y+sprite.getHeight()/2);
        for(Passenger passenger:passengers){
            passenger.move(x,y);
        }
    }

    @Override
    public void delete() {

    }

    @Override
    public boolean wasDeleted() {
        return false;
    }
    public void resetMovement() {
        this.movement=new TdMob.MoveAlongTrack<Horse>(false, world.getTrack().subList(1,world.getTrack().toArray().length-2),
                new Point(0, 0), new float[]{7}, 0,Horse::resetMovement);
    }

    @Override
    public void place(Turret turret) {
        passengers.add(new Passenger(turret));
    }

    @Override
    public boolean allowPlacement(Class monkeytype) {
        if(monkeytype==Plane.class || monkeytype==Necromancer.class){
            return false;
        }
        return true;
    }

    @Override
    public boolean intersects(int x, int y, float size) {
        if(Util.distanceNotSquared(x-this.x,y-this.y-sprite.getHeight()/2)<100){
            return true;
        }
        return false;
    }
    private class Passenger{
        private Turret turret;
        private Float[] offset;
        public Passenger(Turret turret){
            this.turret=turret;
            offset=new Float[2];
            offset[0]=turret.x-x;
            offset[1]=turret.y-y;
        }
        public void move(float newx,float newy){
            turret.move(newx+offset[0],newy+offset[1]);
            System.out.println(sprite.getLayer()+"  "+turret.getSprite().getLayer());
        }
    }
}
