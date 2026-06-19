package Game.WorldStuff.MapElements;

import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.WorldStuff.World;
import Game.WorldStuff.WorldParameters;
import GlobalUse.Constants;
import GlobalUse.Data;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.Sprite.SpriteBatching;

public class MapSelect {
    private SpriteBatching bs;
    ButtonArray islands;


    public MapSelect(){
        int mapCount = Data.listMaps().length;
        Button[] buttons = new Button[mapCount];
        for (int i = 0; i < mapCount; i++) {
            buttons[i] = makeMapButton(i);
        }
        islands = new ButtonArray(2,
                buttons,
                new Sprite("Button", 4), 75, Constants.screenSize.x, Constants.screenSize.y, 10,
                1, 1);
    }
    private Button makeMapButton(int id) {
        String mapName = Data.listMaps()[id];
        Sprite sp = new Sprite(mapName, 6).setSize(10, 10);
        Button b = new Button(Game.get().getSpriteBatching("main"), sp, (x, y) -> {
            Game.get().getWorld().delete();
            TdWorld level=new TdWorld(generateWorldParams(id));
            Game.get().setWorld(level);
        });
        return b;
    }

    //the pre random default for the next map - updated every map.
    //map gets overwritten
    private WorldParameters defaultParams=new WorldParameters(0,3,0,1);

    private WorldParameters generateWorldParams(int id){
        WorldParameters worldParameters=new WorldParameters(id,defaultParams.maxRound,defaultParams.startDifficulty,defaultParams.roundScaling);
        //TODO apply random modifiers and stuff, add stuff to world parameters for stuff like cash starve, max monkeys, list of favored monkeys, list of bonuses that carry over from map to map etc
        if(defaultParams.maxRound<10)
            defaultParams.maxRound++;
        defaultParams.roundScaling+=0.1f;
        return worldParameters;
    }
    //deactivation is done through the world dying, activate once per world instance
    public void activate(){
        bs = Game.get().getSpriteBatching("main");
        islands.addAllToBs(bs);
        Game.get().addMouseDetect(islands);
        islands.show();
    }

    public void show(){
        islands.show();
    }
    //not the opposite of activate, I'm assuming deactivation is done through the world dying
    public void hide(){
        islands.hide();
    }
}
