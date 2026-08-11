package GlobalUse.MapData;

public interface Blocker {
    public boolean allowPlacement();
    public boolean intersects(int x, int y, float size);
}
