package general;

import Game.World;
import windowStuff.KeyListener;
import windowStuff.SimpleText;
import windowStuff.SpriteBatching;
import windowStuff.TextBox;

import java.util.ArrayList;

public class Description {
    public SimpleText.TextGenerator description;
    public SimpleText.TextGenerator altDescription;
    public SimpleText.TextGenerator title;
    public Description(String description){
        this(null,description,null);
    }
    public Description(String title,String description){
        this(title,description,null);
    }
    public Description(String title,String description,String altDescription){
        this(title==null?null:()->title,()->description,altDescription==null?null:()->altDescription);
    }
    public Description(SimpleText.TextGenerator title, SimpleText.TextGenerator description){
        this(title,description,null);
    }
    public Description(SimpleText.TextGenerator title, SimpleText.TextGenerator description, SimpleText.TextGenerator altDescription){
        this.description=description;
        this.title=title;
//        if(altDescription!=null){
//            altDescription=()->if(.KeyListener)
//        }
        this.altDescription=altDescription;
    }
    public TextBox getAsTextBox(int layer, SpriteBatching bs,float cost) {
        ArrayList<SimpleText> texts=new ArrayList<>();
        if (title != null) {
            Log.write(title);
            Log.write(title.get());
            SimpleText titleText = new SimpleText(title, "Calibri", 450, 0, 0, layer
                    , 75, bs, "basic", null);
            texts.add(titleText);
        }
        SimpleText costtxt = new SimpleText("cost: "+cost, "Calibri", 450, 0, 0, layer
                , 25, bs, "basic", null);
        texts.add(costtxt);
        SimpleText desc = new SimpleText(description, "Calibri", 450, 0, 0, layer
                , 25, bs, "basic", null);
        texts.add(desc);
        if (altDescription != null) {
            SimpleText altdesc = new SimpleText(altDescription, "Calibri", 450, 0, 0, layer
                    , 25, bs, "basic", null);
            texts.add(altdesc);
        }
        return new TextBox(0,0,450,true,texts,layer,"textbox",bs,"basic");
    }

}
