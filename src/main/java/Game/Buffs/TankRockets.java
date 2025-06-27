package Game.Buffs;

import Game.BulletLauncher;
import Game.Projectile;
import windowStuff.Graphics;
import windowStuff.ImageData;

public class TankRockets implements Proc{
    Modifier<Projectile> explosive=(proj)->proj.addBeforeDeath(new Explosive<Projectile>(10,150));
    private ImageData image= Graphics.getImage("Bomb-0");//ok this is kinda painful ngl this should be a sprite
    private ImageData prevImage;
    private boolean active=false;
    public TankRockets(ImageData originalImage){
        prevImage=originalImage;
    }
    @Override
    public void mod(BulletLauncher target, boolean cooldown, float angle) {
        if(active){
            return;
        }
        active=true;
        target.setImage(image);
        target.addProjectileModifier(explosive);

    }

    @Override
    public void endMod(BulletLauncher target, boolean cooldown, float angle) {
        target.setImage(prevImage);
        target.removeProjectileModifier(explosive);
        active=false;
    }
}
