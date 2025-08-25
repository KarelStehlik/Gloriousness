package Game.Turrets;

import Game.Enums.TargetingOption;
import Game.Projectile;
import Game.TdWorld;
import Game.TransformAnimation;
import general.Data;
import general.Util;
import java.awt.Point;
import windowStuff.ImageData;
import windowStuff.Sprite;

public class Lightning extends Projectile {

  private final ImageData img;
  private float lastStruckX, lastStruckY;

  // pierce == chains
  // speed == chaining range
  public Lightning(TdWorld world, ImageData image, float X, float Y, float speed,
      float rotation, int width, float aspectRatio, int pierce, float size, float duration,
      float power) {
    super(world, image, X, Y, speed, rotation, width, aspectRatio, pierce, size, duration, power);
    sprite.setHidden(true);
    img = image;
    lastStruckX = x;
    lastStruckY = y;
  }

  private void snapToEnemy() {
    targetedMob = world.getMobsGrid().search(new Point((int) x, (int) y), (int) stats[Stats.speed],
        TargetingOption.STRONG, mob -> !(alreadyHitMobs.contains(mob) || mob.WasDeleted()));
    if (targetedMob == null) {
      return;
    }
    move(targetedMob.getX(), targetedMob.getY());
    collide(targetedMob);
  }

  @Override
  public void move(float _x, float _y) {
    x = _x;
    y = _y;
    Sprite s = new Sprite(sprite);
    s.setHidden(false);
    s.addToBs(world.getBs());
    s.setPosition((x + lastStruckX) / 2, (y + lastStruckY) / 2);
    s.setSize((float) Math.sqrt(Util.distanceSquared(x - lastStruckX, y - lastStruckY)), width);
    s.setRotation(Util.get_rotation(x - lastStruckX, y - lastStruckY));
    s.setDeleteOnAnimationEnd(true);
    s.playAnimation(new TransformAnimation(1).setOpacityScaling(-0.07f));
    lastStruckX = x;
    lastStruckY = y;
  }

  @Override
  public void onGameTick(int tick) {
  }

  private void fork(int chains) {
    float startx = x;
    float starty = y;
    for (int i = 0; i < chains; i++) {
      snapToEnemy();
      x += (Data.gameMechanicsRng.nextFloat() - 0.5f) * 80;
      y += (Data.gameMechanicsRng.nextFloat() - 0.5f) * 80;
      move(x, y);
    }
    x = startx;
    y = starty;
    lastStruckX = x;
    lastStruckY = y;
  }

  @Override
  public void onGameTickP2() {
    bh.tick();
    for (int i = 0; i < stats[Stats.pierce]; i++) {
      snapToEnemy();
      x += (Data.gameMechanicsRng.nextFloat() - 0.5f) * 150;
      y += (Data.gameMechanicsRng.nextFloat() - 0.5f) * 150;
      move(x, y);
      fork(Data.gameMechanicsRng.nextInt((int) stats[Stats.pierce] - i + 1));
    }
    for (var eff : beforeDeath) {
      eff.mod(this);
    }
    delete();
  }
}
