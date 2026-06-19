package Game.Common.Buffs.Modifier;

import Game.Common.Projectile;
import GlobalUse.Data;
import GlobalUse.Util;


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
