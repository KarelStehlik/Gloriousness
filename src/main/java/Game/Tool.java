package Game;

import windowStuff.UserInputListener;

public abstract class Tool implements MouseDetect, KeyboardDetect, TickDetect {

    protected final UserInputListener input;
    private final World world;
    private boolean deleted = false;

    public Tool(World world) {
        input = Game.get().getUserInputListener();
        this.world = world;
    }

    @Override
    public void delete() {
        deleted = true;
        world.currentTool = null;
    }

    @Override
    public boolean WasDeleted() {
        return deleted;
    }
}
