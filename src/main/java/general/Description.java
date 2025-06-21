package general;

import Game.Game;
import Game.World;
import windowStuff.KeyListener;
import windowStuff.SimpleText;
import windowStuff.SpriteBatching;
import windowStuff.TextBox;
import windowStuff.TextModifiers;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

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
        this(title==null?null:()->TextModifiers.blue+title,
                ()->description,
                altDescription==null?null:()-> altDescription);
    }
    public Description(SimpleText.TextGenerator title, SimpleText.TextGenerator description){
        this(title,description,null);
    }
    public Description(SimpleText.TextGenerator title, SimpleText.TextGenerator description, SimpleText.TextGenerator altDescription){
        this.description=description;
        this.title=title==null ? null:()->TextModifiers.blue+ title.get();
        this.altDescription=altDescription==null?null:()-> Game.get().getUserInputListener().isKeyPressed(GLFW_KEY_LEFT_ALT)? altDescription.get():null;
    }
    public TextBox getAsTextBox(int layer, SpriteBatching bs,float cost) {
        ArrayList<SimpleText> texts=new ArrayList<>();
        if (title != null) {
            SimpleText titleText = new SimpleText(title, "Calibri", 500, 0, 0, layer
                    , 75, bs, "basic", null);
            texts.add(titleText);
        }
        SimpleText costtxt = new SimpleText(TextModifiers.green+"cost: "+cost, "Calibri", 500, 0, 0, layer
                , 25, bs, "basic", null);
        texts.add(costtxt);
        SimpleText desc = new SimpleText(description, "Calibri", 500, 0, 0, layer
                , 25, bs, "basic", null);
        texts.add(desc);
        if (altDescription != null) {
            SimpleText altdesc = new SimpleText(altDescription, "Calibri", 500, 0, 0, layer
                    , 25, bs, "basic", null);
            texts.add(altdesc);
        }
        return new TextBox(0,0,500,true,texts,layer,"textbox",bs,"basic");
    }

}
