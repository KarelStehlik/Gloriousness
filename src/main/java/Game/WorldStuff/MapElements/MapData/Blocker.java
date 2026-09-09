package Game.WorldStuff.MapElements.MapData;

import Game.Common.Turrets.Turret;

public interface Blocker {
    public boolean allowPlacement(Class monkeytype);
    public boolean intersects(int x, int y, float size);
    public void place(Turret turret);
}
