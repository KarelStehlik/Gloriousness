package Game;

import general.Util;
import windowStuff.Button;
import windowStuff.Sprite;

public class TurretGenerator {

  private static final float COST_SCALING = 1.2f;
  World world;
  BulletLauncher templateLauncher;
  String type;
  String image;
  float cost;

  public TurretGenerator(World world, String type, String imageName, float cost) {
    this.type = type;
    this.image = imageName;
    this.world = world;
    this.templateLauncher = new BulletLauncher(world, "Egg", 0, 0, 0,
        50, 50, 0, 50, 0, 0);
    this.cost = cost;
  }

  public BulletLauncher getTemplateLauncher() {
    return templateLauncher;
  }

  public TurretGenerator addOnMobCollide(Projectile.OnCollideComponent<Mob> collide) {
    templateLauncher.addMobCollide(collide);
    return this;
  }

  public TurretGenerator addOnProjectileCollide(Projectile.OnCollideComponent<Player> collide) {
    templateLauncher.addPlayerCollide(collide);
    return this;
  }

  public TurretGenerator addOnPlayerCollide(Projectile.OnCollideComponent<Projectile> collide) {
    templateLauncher.addProjectileCollide(collide);
    return this;
  }

  public boolean generate(int x, int y) {
    if (world.getMoney() < cost) {
      return false;
    }
    world.setMoney(world.getMoney() - cost);
    cost *= COST_SCALING;
    Turret t = new Turret(world, x, y, image, new BulletLauncher(templateLauncher), type);
    return true;
  }

  public void select() {
    if (world.currentTool != null) {
      world.currentTool.delete();
    }
    world.currentTool =
        new PlaceObjectTool(world,
            new Sprite("image", Turret.WIDTH, Turret.HEIGHT, 10, world.getBs()).setColors(
                Util.getBaseColors(.6f)),
            this::generate);
  }

  public Button makeButton(int layer) {
    return new Button(world.getBs(), new Sprite(image, 100, 100, 200, 200, 10, "basic"),
        (int button, int action) -> {
          if (button == 0 && action == 1) {
            this.select();
          }
        }, () -> type + ": cost= " + (int) cost);
  }
}