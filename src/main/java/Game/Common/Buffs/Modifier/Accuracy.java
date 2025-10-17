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
        float finalSpreadx, finalSpready;
        float duration=target.getStats()[Projectile.Stats.duration];

        if (spreadx == 0) {
            finalSpreadx = 0;
        } else {
            finalSpreadx = Util.square(Data.gameMechanicsRng.nextFloat(0, (float) Math.sqrt(spreadx)));
            if (Data.gameMechanicsRng.nextBoolean()) {
                finalSpreadx *= -1;
            }
        }
        if (spready == 0) {
            finalSpready = 0;
        } else {
            finalSpready = Util.square(Data.gameMechanicsRng.nextFloat(0, (float) Math.sqrt(spready)));
            if (Data.gameMechanicsRng.nextBoolean()) {
                finalSpready *= -1;
            }
        }
        float dvx = finalSpreadx/duration;
        float dvy = finalSpready/duration;
        target.accelerate(dvx,dvy);
    }
}
