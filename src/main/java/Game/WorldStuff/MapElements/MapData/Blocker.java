package Game.WorldStuff.MapElements.MapData;

public interface Blocker {
    public boolean allowPlacement();
    public boolean intersects(int x, int y, float size);
}
