package Game.Buffs;

import Game.BulletLauncher;
import Game.Projectile;

public class TankRockets implements Proc{
    Modifier<Projectile> explosive=(proj)->proj.addBeforeDeath(new Explosive<Projectile>(10,150));
    private String image="bomb";//ok this is kinda painful ngl this should be a sprite
    private String prevImage;
    private boolean active=false;
    public TankRockets(String originalImage){
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
