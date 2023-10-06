package Game;

import Game.Buffs.Buff;
import windowStuff.Button;
import windowStuff.NoSprite;
import windowStuff.Sprite;

import java.util.ArrayList;
import java.util.List;

public class UpgradeGiver {
    private List<Button> buttons = new ArrayList<>(2);
    private World world;
    private static final int BOTTOM=50, LEFT=110, BUTTON_WIDTH=200, BUTTON_HEIGHT=100, BUTTON_OFFSET=120;
    public UpgradeGiver(World w){
        world=w;
    }
    private void clearOptions(){
        for(Button b : buttons){
            b.delete();
        }
    }
    private void optionPicked(int id){
        world.getPlayer().addBuff(new Buff<Player>(0, Buff.INFINITE_DURATION, Buff.TRIGGER_ON_UPDATE,p->p.stats.cd/=2));
        clearOptions();
        world.beginWave();
    }
    public void gib(int gloriousness){
        clearOptions();
        for(int i = 0; i<gloriousness;i++){
            int finalI = i;
            Button B = new Button(world.getBs(),
                    new Sprite("Button",10).setSize(BUTTON_WIDTH, BUTTON_HEIGHT).setPosition(LEFT, BOTTOM+i*BUTTON_OFFSET),
                    (b, a)->optionPicked(finalI),null);
            buttons.add(B);
            Game.get().addMouseDetect(B);
        }
    }
}
