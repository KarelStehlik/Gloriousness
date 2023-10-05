package Game;

import windowStuff.Button;

import java.util.ArrayList;
import java.util.List;

public class UpgradeGiver {
    private List<Button> buttons = new ArrayList<>(2);
    private World world;
    public UpgradeGiver(World w){
        world=w;
    }
    private void clearOptions(){
        for(Button b : buttons){
            b.delete();
        }
    }
    private void optionPicked(int id){
        world.getPlayer().
    }
    public void gib(int gloriousness){

    }
}
