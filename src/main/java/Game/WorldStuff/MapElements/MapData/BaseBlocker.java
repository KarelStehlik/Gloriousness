package Game.WorldStuff.MapElements.MapData;

import Game.Misc.GameObject;
import Game.WorldStuff.TdWorld;

public abstract class BaseBlocker extends GameObject implements Blocker {
    public BaseBlocker(float X, float Y, int W, int H, TdWorld w){
        super(X,Y,W,H,w);
        world.blockers.add(this);
    }



}
