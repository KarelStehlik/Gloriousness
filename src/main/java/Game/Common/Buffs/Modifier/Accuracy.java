package Game.Common.Buffs.Modifier;

import Game.Common.Buffs.Buff.StatBuff;
import Game.Common.Projectile;
import Game.Common.Turrets.Mortar;
import Game.Common.Turrets.Turret;
import Game.Misc.Game;
import Game.Misc.GameObject;
import GlobalUse.Data;
import GlobalUse.Log;
import GlobalUse.Util;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static Game.Common.Projectile.Stats.speed;


public class Accuracy {

    public static void mod(Projectile target, float spreadx, float spready) {
        double finalSpreadx, finalSpready;
        float duration=target.getStats()[Projectile.Stats.duration];
        Vector2f targetPos = new Vector2f(target.getX() + target.vx * duration
                , target.getY() + target.vy * duration);
        if (spreadx == 0) {
            finalSpreadx = 0;
        } else {
            finalSpreadx = Math.pow(Data.gameMechanicsRng.nextFloat(0, (float) Math.sqrt(spreadx)),
                2);
            if (Data.gameMechanicsRng.nextBoolean()) {
                finalSpreadx *= -1;
            }
        }
        if (spready == 0) {
            finalSpready = 0;
        } else {
            finalSpready = Math.pow(Data.gameMechanicsRng.nextFloat(0, (float) Math.sqrt(spready)),
                2);
            if (Data.gameMechanicsRng.nextBoolean()) {
                finalSpready *= -1;
            }
        }
        Vector2d finalpos = new Vector2d(finalSpreadx + targetPos.x, finalSpready + targetPos.y);
        double newDistance = Util.distanceNotSquared(target.getX() - finalpos.x, target.getY()- finalpos.y);
        float newSpeed=(float) newDistance/duration;
        target.addBuff(new StatBuff<>(StatBuff.Type.FINALLY_ADDED, speed, -target.getStats()[speed] + newSpeed));
        target.setRotation(Util.get_rotation((float)finalpos.x-target.getX(),(float)finalpos.y-target.getY()));
    }
}
