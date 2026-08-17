package Game.WorldStuff.MapElements.MapData;

import Game.Misc.GameObject;
import Game.WorldStuff.TdWorld;
import GlobalUse.Log;

import java.awt.*;
import java.util.ArrayList;

public class MapData{
    public ArrayList<Point> mapPoints=new ArrayList<>();
    @FunctionalInterface
    public interface NewObjectFunction {
        GameObject make(TdWorld world);
    }
    public ArrayList<NewObjectFunction> mapObjects=new ArrayList<>();
    public MapData(){

    }
    public void addPoint(Point e){
        mapPoints.add(e);
    }
    public void addObject(NewObjectFunction e){
        mapObjects.add(e);
    }
    public void add(String[] thing){
        String type=thing[0];
        int len=thing.length;
        switch(type){
            case ("Node")->{
                String[] point = thing[1].split(",");
                if(len!=2||point.length!=2){
                    String data="";
                    for(String str:thing){
                        data+=" "+str;
                    }
                    Log.write("unexpected node length, data:"+data);
                }
                addPoint(new Point(Integer.parseInt(point[0]), Integer.parseInt(point[1])));
            }
            case("Rect")->{
                String[] point = thing[1].split(",");
                if(len!=2||point.length!=4){
                    String data="";
                    for(String str:thing){
                        data+=" "+str;
                    }
                    Log.write("unexpected rect length, data:"+data);
                }
                addObject((world)->new RectBlocker(
                        Integer.parseInt(point[0]),Integer.parseInt(point[1])
                        ,Integer.parseInt(point[2]),Integer.parseInt(point[3])
                        ,false,world));
            }
            default->
                Log.write("What the fuck is this map object??!"+type);

        }
    }


}