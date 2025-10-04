package Game.Misc;

import Game.Common.Turrets.Turret;
import Game.Common.Turrets.Turret.Stats;
import windowStuff.Button;
import windowStuff.GraphicsOnly.Sprite.NoSprite;
import windowStuff.GraphicsOnly.Sprite.Sprite;

public class TurretGenerator {

  private final TdWorld world;
  private final String image, label;
  private final MakeTurret func;
  private Turret pending = null;

  public TurretGenerator(TdWorld world, String image, String label, MakeTurret make) {
    this.world = world;
    this.image = image;
    this.label = label;
    this.func = make;
    pending = func.make();
  }

  public boolean generate(int x, int y) {
    if (!world.canFitTurret(x, y, pending.stats[Stats.size]) || !world.tryPurchase(
        pending.stats[Stats.cost])) {
      return false;
    }

    pending.place();
    pending = func.make();
    return true;
  }

  public void select() {
    var tool = new PlaceObjectTool(world,
        new NoSprite(),
        this::generate);
    world.setCurrentTool(tool);
    this.pending.move(Game.get().getUserInputListener().getX(),
        Game.get().getUserInputListener().getY());
    world.lastTurret=this;

    tool.
        setOnMove((x, y) -> this.pending.move(x, y)).setOnDelete(() -> {
          if (this.pending.isNotYetPlaced()) {
            this.pending.move(-1000, -1000);
          } else {
            this.pending = func.make();
          }
        });
  }

  public Button makeButton() {
    return new Button(world.getBs(), new Sprite(image, 10).setPosition(100, 100).setSize(200, 200),
        (int button, int action) -> {
          if (button == 0 && action == 1) {
            this.select();
          }
        }, () -> label + ": cost= " + (int) pending.stats[Stats.cost]);
  }

  @FunctionalInterface
  public interface MakeTurret {

    Turret make();
  }
}
