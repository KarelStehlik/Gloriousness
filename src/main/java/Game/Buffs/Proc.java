package Game.Buffs;

import Game.BulletLauncher;

public interface Proc extends AttackEffect{
    void endMod(BulletLauncher target, boolean cooldown, float angle);
}
