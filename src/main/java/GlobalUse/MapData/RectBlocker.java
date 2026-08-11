package GlobalUse.MapData;

import Game.WorldStuff.TdWorld;

public class RectBlocker extends BaseBlocker {
    private final boolean allowsPlacement;

    public RectBlocker(float X, float Y, int W, int H,boolean allowsPlacement, TdWorld w) {
        super(X, Y, W, H, w);
        this.allowsPlacement=allowsPlacement;
    }

    @Override
    public boolean allowPlacement() {
        return allowsPlacement;
    }

    @Override
    public boolean intersects(int x, int y, float size) {
        if(Math.abs(this.x-x)<width&&Math.abs(this.y-y)<height){
            return true;
        }return false;
    }


}
