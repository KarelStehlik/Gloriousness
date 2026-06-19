package Game.WorldStuff.MapElements;

import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.WorldStuff.World;
import Game.WorldStuff.WorldParameters;
import GlobalUse.Constants;
import GlobalUse.Data;
import org.joml.Vector2f;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.Sprite.SpriteBatching;

import java.util.ArrayList;
import java.util.Vector;

public class MapSelect {
    ArrayList<Island> islands=new ArrayList<>();

    public MapSelect(){
        addIsland();
        addIsland();
        addIsland();
        addIsland();
        unlockIsland(0);
    }

    private void addIsland(){
        islands.add(makeIsland(islands.size()));
    }

    private int[] lastIslandPos={(int)(Constants.screenSize.x*0.1),(int)(Constants.screenSize.y*0.65)};
    private Island makeIsland(int index){
        Island isl= new Island(lastIslandPos,index,this);
        lastIslandPos[0]+=(int)(Constants.screenSize.x*0.2);
        return isl;
    }

    public void islandDone(int index){
        unlockIsland(index+1);
        //todo branching paths?
    }

    public void unlockIsland(int index){
        islands.get(index).unlock();
    }

    public void triggerLevel(WorldParameters baseLevelParams){
        //TODO apply random modifiers and stuff, add stuff to world parameters for stuff like max monkeys, list of bonuses that carry over from map to map etc
        TdWorld baseLevel=new TdWorld(baseLevelParams);
        Game.get().getWorld().delete();
        Game.get().setWorld(baseLevel);

    }

    //deactivation is done through the world dying, activate once per world instance
    public void activate(){
        SpriteBatching bs = Game.get().getSpriteBatching("main");
        for(Island isl:islands) {
            isl.activate(bs);
        }
    }

    public void show(){
        for(Island isl:islands) {
            isl.show();
        }
    }
    //not the opposite of activate, I'm assuming deactivation is done through the world dying
    public void hide(){
        for(Island isl:islands) {
            isl.hide();
        }
    }
}
