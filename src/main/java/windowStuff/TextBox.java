package windowStuff;

import general.Log;

import java.util.ArrayList;
import java.util.logging.Logger;

public class TextBox implements Text{ //not sure if imgonna use this,
    // but it should allow you to manage several texts better eg:title+subtexts
    private ArrayList<SimpleText> texts;
    private int borderSize=20; //I dunno I gave up, honestly text doesn't really make sense, but like I guess this kind of works (for the wrong reasons)
    private int x, y;
    private final AbstractSprite background;
    private boolean rearrange;
    private boolean hidden=false;
    private int maxWidth;
    private int layer;

    public TextBox(int x,int y,int maxWidth,boolean rearrange,ArrayList<SimpleText> texts){
        this.texts=texts;
        this.maxWidth=maxWidth;
        this.rearrange=rearrange;
        this.x=x;
        this.y=y;
        background= new NoSprite();
        if(rearrange){
            arrange();
        }
        borderSize=0;

    }
    public TextBox(int x,int y,int maxWidth,boolean rearrange,ArrayList<SimpleText> texts,int layer,
                   String backgroundImage, SpriteBatching bs,String shader){
        this.texts=texts;
        this.maxWidth=maxWidth;
        this.x=x;
        this.y=y;
        this.layer=layer;
        for(SimpleText text:texts){
            text.setLayer(layer);
        }
        if (backgroundImage == null) {
            background = new NoSprite();
        } else {
            background = new Sprite(backgroundImage, 0, 0, layer, shader);
            background.addToBs(bs);
        }
        this.rearrange=rearrange;
        if(rearrange){
            arrange();
        }
    }
    public void addText(SimpleText text){
        texts.add(text);
    }
    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
    private int getHeight(){
        int height=0;
        for(SimpleText text:texts){
            height+=text.getHeight();
        }
        return height;
    }

    @Override
    public void update() {
        if(hidden){
            return;
        }
        for(SimpleText text:texts){
            text.update();
        }
        if(rearrange){
            arrange();
        }
    }

    private void arrange() {
        if(!rearrange){
            //no rearrange with background was never tested
            int up=0,down=0,left=0,right=0;

            for(SimpleText text:texts) {
                if (text.getY() < down) {
                    down = text.getY();
                } else if (text.getY()+text.getHeight() > up){
                    up=(int) (text.getY()+text.getHeight());
                }
                if (text.getX() -text.getMaxWidth()/2< left) {
                    left = text.getX()-text.getMaxWidth()/2;
                } else if (text.getX()+text.getMaxWidth()/2 > up){
                    right=(int) (text.getX()+text.getHeight()/2);
                }
                background.setPosition(left+(left-right)/2f, down+(up-down)/2f-texts.get(texts.size()-1).getFontSize()/2);
                background.setSize(left-right, up-down+texts.get(texts.size()-1).getFontSize());
            }
            return;
        }
        int currentY=y;
        int height=getHeight();
        for(SimpleText text:texts){
            if(text.isHidden()){
                continue;
            }
            currentY-=text.getHeight();
            text.move(x-maxWidth/2,currentY+height);
        }
        background.setSize(maxWidth, y-currentY+borderSize*2);
    }

    @Override
    public void hide() {
        hidden=true;
        background.setHidden(true);
        for(SimpleText text:texts){
            text.hide();
        }
    }

    @Override
    public void show() {
        hidden=false;
        background.setHidden(false);
        for(SimpleText text:texts){
            text.show();
        }
        if(rearrange){
            arrange();
        }
    }
    public void hide(int textIndex) {
        texts.get(textIndex).hide();
        if(rearrange){
            arrange();
        }
    }
    public void show(int textIndex) {
        texts.get(textIndex).show();
        if(rearrange){
            arrange();
        }
    }

    @Override
    public void move(int newX, int newY) {
        int displaceX=newX-x;
        int displaceY=newY-y;
        for(SimpleText text:texts){
            text.move(text.getX()+displaceX
                    ,text.getY()+displaceY);
        }
        x=newX;
        y=newY;
        background.setPosition(newX , newY + getHeight()/2f-borderSize);

    }

    @Override
    public void delete() {
        for(SimpleText text:texts){
            text.delete();
        }
        if(background!=null){
            background.delete();
        }
        texts.clear();
    }
}
