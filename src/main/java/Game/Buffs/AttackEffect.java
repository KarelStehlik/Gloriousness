package Game.Buffs;

import Game.BulletLauncher;

public interface AttackEffect {

  void mod(BulletLauncher target, boolean cooldown, float angle);
}
