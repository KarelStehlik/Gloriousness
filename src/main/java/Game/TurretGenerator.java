package Game;

import general.Util;
import windowStuff.Button;
import windowStuff.Sprite;

public class TurretGenerator {

  private static final float COST_SCALING = 1.2f;
  private final World world;
  private final String image, label;
  private final Place type;
  private final float size;
  private final float visualSize;
  private final float range;
  private float cost;

  public TurretGenerator(World world, String label, Place placeFunction, String imageName,
      float cost, float size, float visualSize, float range) {
    this.type = placeFunction;
    this.image = imageName;
    this.world = world;
    this.cost = cost;
    this.label = label;
    this.size = size;
    this.visualSize = visualSize;
    this.range = range;
  }

  public boolean generate(int x, int y) {
    if (!world.canFitTurret(x, y, size) || !world.tryPurchase(cost)) {
      return false;
    }
    cost *= COST_SCALING;
    type.place(x, y);
    return true;
  }

  public void select() {
    var tool = new PlaceObjectTool(world,
        new Sprite(image, visualSize, visualSize, 10, world.getBs()).setColors(
            Util.getBaseColors(.6f)),
        this::generate);
    world.setCurrentTool(tool);
    tool.addSprite(new Sprite("Shockwave", 1).
        setSize(2 * range, 2 * range).
        addToBs(world.getBs()).
        setOpacity(0.2f).setPosition(-1000, -1000));
  }

  public Button makeButton() {
    return new Button(world.getBs(), new Sprite(image, 100, 100, 200, 200, 10, "basic"),
        (int button, int action) -> {
          if (button == 0 && action == 1) {
            this.select();
          }
        }, () -> label + ": cost= " + (int) cost);
  }

  @FunctionalInterface
  public interface Place {

    void place(int x, int y);
  }
}
