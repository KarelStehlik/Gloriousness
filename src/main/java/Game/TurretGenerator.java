package Game;

import Game.Turrets.Turret;
import general.Util;
import windowStuff.Button;
import windowStuff.Sprite;

public class TurretGenerator {

  private static final float COST_SCALING = 1.2f;
  private final World world;
  private final String image;
  private final Place type;
  private float cost;

  public TurretGenerator(World world, Place placeFunction, String imageName, float cost) {
    this.type = placeFunction;
    this.image = imageName;
    this.world = world;
    this.cost = cost;
  }

  public boolean generate(int x, int y) {
    if (world.getMoney() < cost) {
      return false;
    }
    world.setMoney(world.getMoney() - cost);
    cost *= COST_SCALING;
    type.place(x, y);
    return true;
  }

  public void select() {
    world.setCurrentTool(
        new PlaceObjectTool(world,
            new Sprite("image", Turret.WIDTH, Turret.HEIGHT, 10, world.getBs()).setColors(
                Util.getBaseColors(.6f)),
            this::generate)
    );
  }

  public Button makeButton() {
    return new Button(world.getBs(), new Sprite(image, 100, 100, 200, 200, 10, "basic"),
        (int button, int action) -> {
          if (button == 0 && action == 1) {
            this.select();
          }
        }, () -> type + ": cost= " + (int) cost);
  }

  @FunctionalInterface
  interface Place {

    void place(int x, int y);
  }
}
