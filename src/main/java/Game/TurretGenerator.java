package Game;

import Game.Turrets.Turret;
import Game.Turrets.Turret.Stats;
import general.Log;
import windowStuff.Button;
import windowStuff.NoSprite;
import windowStuff.Sprite;

public class TurretGenerator {

  private final World world;
  private final String image, label;
  private final MakeTurret func;
  private Turret pending = null;

  public TurretGenerator(World world, String image, String label, MakeTurret make) {
    this.world = world;
    this.image = image;
    this.label = label;
    this.func = make;
    pending=func.make();
  }

  public boolean generate(int x, int y) {
    if (!world.canFitTurret(x, y, pending.stats[Stats.size]) || !world.tryPurchase(
        pending.stats[Stats.cost])) {
      return false;
    }

    pending.place();
    return true;
  }

  public void select() {
    Log.write(this.label);
    var tool = new PlaceObjectTool(world,
        new NoSprite(),
        this::generate);
    world.setCurrentTool(tool);

    tool.
        setOnMove(pending::move).setOnDelete(()->{
          if (pending.isNotYetPlaced()) {
            pending.delete();
          }
          pending = func.make();
        });
  }

  public Button makeButton() {
    return new Button(world.getBs(), new Sprite(image, 100, 100, 200, 200, 10, "basic"),
        (int button, int action) -> {
          if (button == 0 && action == 1) {
            this.select();
          }
        }, () -> label + ": cost= " + (int)pending.stats[Stats.cost]);
  }

  @FunctionalInterface
  public interface MakeTurret {

    Turret make();
  }
}
