package Game.WorldStuff.MapElements;

import Game.WorldStuff.Game;
import Game.WorldStuff.GameModifiers;
import Game.WorldStuff.TdWorld;
import Game.WorldStuff.WorldParameters;
import GlobalUse.Constants;
import GlobalUse.Data;
import GlobalUse.Log;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.GraphicsOnly.Sprite.Sprite;
import windowStuff.GraphicsOnly.Sprite.SpriteBatching;
import windowStuff.GraphicsOnly.Text.SimpleText;
import windowStuff.GraphicsOnly.Text.TextBox;

import java.util.ArrayList;

public class Island {
    private ButtonArray levels;
    private int[] position;
    private boolean[] mapDone;
    MapSelect mapSelect;
    int index;
    Sprite lockSprite;
    Button image;
    private boolean locked=true;
    private int size=120;
    private GameModifiers mods=new GameModifiers();

    public Island(int[] pos,int index,MapSelect mapSelect) {
        this.position= new int[]{pos[0], pos[1]};
        this.index=index;
        levels=makeLevels();
        lockSprite=new Sprite("lock", 2).
            setPosition(pos[0], pos[1]+size ).
            setSize(size/2, size/2);
        this.mapSelect = mapSelect;
    }

    public void activate(SpriteBatching bs) {
        levels.addAllToBs(bs);
        lockSprite.addToBs(bs);

        ArrayList<SimpleText> descs=new ArrayList<>();
        for(String s:mods.texts){
            descs.add(new SimpleText(s, "Calibri", 450, 0, 0, Constants.layerInterval.ui.min+10,
                    35, bs, "basic", "textbox"));
        }
        Sprite textBackground=new Sprite("textbox",Constants.layerInterval.ui.min,"basic");
        this.image=new Button(bs,new Sprite("island", 1).
                setPosition(position[0], position[1]+size ).
                setSize(size*2,size*2).setNaturalHeight(),(x,y)->{},new TextBox(0,0,500,true,descs,textBackground));


        Game.get().addMouseDetect(image);
        Game.get().addMouseDetect(levels);
        levels.show();
    }


    private ButtonArray makeLevels() {
        int mapCount = Data.listMaps().length;
        int levelcount=(int)(Math.random()*3d)+1;
        Button[] buttons = new Button[levelcount];
        mapDone=new boolean[levelcount];
        for(int i=0;i<levelcount;i++){
            mapDone[i]=false;
        }
        int[] maps=new int[levelcount];
        for(int i=0;i<levelcount;i++){
            maps[i]=(int)(Math.random()*(mapCount-1));
        }
        for (int i = 0; i < levelcount; i++) {
            buttons[i] = makeMapButton(maps[i],i);
        }
        return new ButtonArray(2,
                buttons,
                new Sprite("Button", 10), 75, position[0], position[1], 10,
                0.5f, 0.5f);
    }
    private boolean mapAvailable(int index){
        return (!locked)&&(!mapDone[index]);
    }

    private Button makeMapButton(int id,int index) {

        String mapName = Data.listMaps()[id];
        Sprite sp = new Sprite(mapName, 9).setSize(10, 10);
        Button b = new Button(Game.get().getSpriteBatching("main"), sp, (x, y) -> {
            if(!mapAvailable(index))
                return;
            Button button=levels.getButton(index);
            button.getSprite().setImage("bananacheckmark");
            mapDone[index]=true;
            triggerLevel(id);
        });
        return b;
    }

    private void triggerLevel(int id){
        if(islandDone())
            mapSelect.islandDone(this.index);
        mapSelect.triggerLevel(generateWorldParams(id));
    }
    private boolean islandDone(){
        for(int i=0;i<mapDone.length;i++)
            if(!mapDone[i])
                return false;
        return true;
    }

    //the pre random default for the next map - updated every map.
    //map gets overwritten
    private WorldParameters defaultParams = new WorldParameters(0, 10, 0, 1);

    private WorldParameters generateWorldParams(int id) {
        WorldParameters worldParameters = new WorldParameters(id, defaultParams.maxRound, defaultParams.startDifficulty, defaultParams.roundScaling,mods);
        return worldParameters;
    }
    public void unlock(){
        locked=false;
        lockSprite.delete();
    }

    public void show() {
        levels.show();
    }

    //not the opposite of activate, I'm assuming deactivation is done through the world dying
    public void hide() {
        levels.hide();
    }
    public void hideInBunkerFromNuke(){
        //prevents it from getting deleted by call to game.nuke()
        Game.get().removeMouseDetect(levels);
        Game.get().removeMouseDetect(image);
    }

}
