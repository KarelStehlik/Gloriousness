package Game.WorldStuff.MapElements;

import Game.WorldStuff.Game;
import Game.WorldStuff.TdWorld;
import Game.WorldStuff.WorldParameters;
import GlobalUse.Data;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.Sprite.SpriteBatching;

import java.util.logging.Level;

public class Island {
    private ButtonArray levels;
    private int[] position;

    public Island(int[] pos, int count) {
        this.position= new int[]{pos[0], pos[1]};
        levels=makeLevels();
    }

    public void activate(SpriteBatching bs) {
        levels.addAllToBs(bs);
        Game.get().addMouseDetect(levels);
        levels.show();
    }

    private ButtonArray makeLevels() {
        int mapCount = Data.listMaps().length;
        int levelcount=(int)(Math.random()*3d)+1;
        Button[] buttons = new Button[levelcount];
        int[] maps=new int[levelcount];
        for(int i=0;i<levelcount;i++){
            maps[i]=(int)(Math.random()*(mapCount-1));
        }
        for (int i = 0; i < levelcount; i++) {
            buttons[i] = makeMapButton(maps[i],i);
        }
        return new ButtonArray(2,
                buttons,
                new Sprite("Button", 4), 75, position[0], position[1], 10,
                1, 1);
    }

    private Button makeMapButton(int id,int index) {

        String mapName = Data.listMaps()[id];
        Sprite sp = new Sprite(mapName, 6).setSize(10, 10);
        Button b = new Button(Game.get().getSpriteBatching("main"), sp, (x, y) -> {
            Game.get().getWorld().delete();
            TdWorld level = new TdWorld(generateWorldParams(id));
            Game.get().setWorld(level);
            finishLevel(index);
        });
        return b;
    }

    private void finishLevel(int index){
        levels.deleteNth(index);
    }

    //the pre random default for the next map - updated every map.
    //map gets overwritten
    private WorldParameters defaultParams = new WorldParameters(0, 3, 0, 1);

    private WorldParameters generateWorldParams(int id) {
        WorldParameters worldParameters = new WorldParameters(id, defaultParams.maxRound, defaultParams.startDifficulty, defaultParams.roundScaling);
        //TODO apply random modifiers and stuff, add stuff to world parameters for stuff like cash starve, max monkeys, list of favored monkeys, list of bonuses that carry over from map to map etc
        if (defaultParams.maxRound < 10)
            defaultParams.maxRound++;
        defaultParams.roundScaling += 0.1f;
        return worldParameters;
    }


    public void show() {
        levels.show();
    }

    //not the opposite of activate, I'm assuming deactivation is done through the world dying
    public void hide() {
        levels.hide();
    }

}
