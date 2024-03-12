package Game;

import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengles.GLES20.GL_ONE;
import static org.lwjgl.opengles.GLES20.GL_SRC_COLOR;

import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Turrets.BasicTurret;
import Game.Turrets.EatingTurret;
import Game.Turrets.EmpoweringTurret;
import Game.Turrets.IgniteTurret;
import Game.Turrets.SlowTurret;
import general.Constants;
import general.Data;
import general.Log;
import imgui.ImGui;
import imgui.type.ImBoolean;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector2f;
import windowStuff.Button;
import windowStuff.ButtonArray;
import windowStuff.NoSprite;
import windowStuff.SingleAnimationSprite;
import windowStuff.Sprite;
import windowStuff.SpriteBatching;
import windowStuff.Text;

public class World implements TickDetect, MouseDetect, KeyboardDetect {

  public static final int WIDTH = 1920;
  public static final int HEIGHT = 1080;
  private final Options options = new Options();
  private final SpriteBatching bs;
  private final SquareGridMobs mobsGrid;
  private final List<TdMob> mobsList;
  private final SquareGrid<Projectile> projectilesGrid;
  private final List<Projectile> projectilesList;
  private final Player player;
  private final Sprite mapSprite;
  private final List<Point> mapData;
  private final Text resourceTracker;
  private final MobSpawner mobSpawner = new MobSpawner();
  private final UpgradeGiver upgrades = new UpgradeGiver(this);
  private Tool currentTool;
  private int tick = 0;
  private int health = Constants.StartingHealth;
  private double money = 1234567890;
  private int wave = 0;
  private boolean waveRunning = true;
  public World() {
    Game game = Game.get();
    game.addMouseDetect(this);
    game.addKeyDetect(this);
    BasicCollides.init(this);
    mobsGrid = new SquareGridMobs(-500, -500, WIDTH + 1000, HEIGHT + 1000,
        Optimization.MobGridSquareSize);
    mobsList = new ArrayList<>(1024);
    projectilesGrid = new SquareGrid<Projectile>(-500, -500, WIDTH + 1000, HEIGHT + 1000,
        Optimization.ProjectileGridSquareSize);
    projectilesList = new ArrayList<>(128);
    bs = game.getSpriteBatching("main");
    getBs().getCamera().moveTo(0, -0, 20);
    player = new Player(this);
    String mapName = Data.listMaps()[0];
    mapSprite = new Sprite(mapName, Constants.screenSize.x / 2f, Constants.screenSize.y / 2f,
        Constants.screenSize.x, Constants.screenSize.y, 0, "basic");
    bs.addSprite(mapSprite);
    mapData = Data.getMapData(mapName);

    TurretGenerator test = BasicTurret.generator(this);

    TurretGenerator testDotTurret = IgniteTurret.generator(this);

    TurretGenerator testSlowTurret = SlowTurret.generator(this);

    TurretGenerator testEmp = EmpoweringTurret.generator(this);

    TurretGenerator testEating = EatingTurret.generator(this);

    TurretGenerator[] availableTurrets = new TurretGenerator[]{test, testDotTurret, testSlowTurret,
        testEmp, testEating};

    ButtonArray turretBar = new ButtonArray(2,
        Arrays.stream(availableTurrets).map(tg -> tg.makeButton()).toArray(Button[]::new),
        new Sprite("Button", 4).addToBs(bs), 75, Constants.screenSize.x, Constants.screenSize.y, 10,
        1, 1);
    game.addMouseDetect(turretBar);

    game.addMouseDetect(new Button(bs, new NoSprite().setSize(100, 100).setPosition(410, 660)
        , (button, action) -> {
      if (action == 0) {
        return;
      }
      float x = game.getUserInputListener().getX(), y = game.getUserInputListener().getY();
      for (int i = 0; i < (options.laggyGong ? 10000 : 1); i++) {
        explosionVisual(x, y, 100, true, "Explosion1-0");
      }
      player.addBuff(
          new StatBuff<Player>(Type.MORE, Float.POSITIVE_INFINITY, player.stats.cd, 0.5f));
    }, null));

    resourceTracker = new Text("Lives: " + health + "\nCash: " + (int) getMoney(), "Calibri", 500,
        0, 1050, 10, 40, bs);

    currentTool = new PlaceObjectTool(this, new NoSprite(), (x, y) -> false);
    currentTool.delete();
    beginWave();
  }

  public void explosionVisual(float x, float y, float size, boolean shockwave, String image) {
    Game game = Game.get();
    if (shockwave) {
      game.addTickable(
          new Animation(
              new Sprite("Shockwave", x, y, size, size, 3, "basic").addToBs(bs).setOpacity(0.7f), 3
          ).setLinearScaling(new Vector2f(size / 3, size / 3)).setOpacityScaling(-0.01f)
      );
    }
    Sprite sp = new SingleAnimationSprite(image, .7f, x, y, size * 5, size * 5, 4, "basic").
        addToBs(bs).setRotation(Data.unstableRng.nextFloat(360));
  }

  public void aoeDamage(int x, int y, int size, float damage, DamageType type) {
    mobsGrid.callForEachCircle(x, y, size, mob -> mob.takeDamage(damage, type));
  }

  public int getHealth() {
    return health;
  }

  public void changeHealth(int change) {
    health += change;
    resourceTracker.setText("Lives: " + health + "\nCash: " + (int) getMoney());
  }

  public List<Point> getMapData() {
    return mapData;
  }

  public Player getPlayer() {
    return player;
  }

  public List<Projectile> getProjectilesList() {
    return projectilesList;
  }

  protected void endGame() {
    Log.write("gjghjghjg");
  }

  public void addEnemy(TdMob e) {
    mobsList.add(e);
  }

  // TODO: imgui runs in the graphics thread, careful changing game variables
  public void showPauseMenu() {
    //ImGui.showDemoWindow();
    ImGui.begin("Options");
    if (ImGui.collapsingHeader("Some placeholder options")) {
      ImBoolean fuck = new ImBoolean(options.fuckified);
      if (ImGui.checkbox("Some bullshit setting", fuck)) {
        options.fuckified = fuck.get();
        if (options.fuckified) {
          glBlendFunc(GL_SRC_COLOR, GL_ONE);
        } else {
          glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }
      }

      ImBoolean gong = new ImBoolean(options.laggyGong);
      if (ImGui.checkbox("Gong lag", gong)) {
        options.laggyGong = gong.get();
      }
    }
    ImGui.end();
  }

  @Override
  public void onKeyPress(int key, int action, int mods) {
    if (!currentTool.WasDeleted()) {
      currentTool.onKeyPress(key, action, mods);
    }
  }

  @Override
  public void onMouseButton(int button, double x, double y, int action, int mods) {
    if (!currentTool.WasDeleted()) {
      currentTool.onMouseButton(button, x, y, action, mods);
    }
  }

  @Override
  public void onScroll(double scroll) {
    if (!currentTool.WasDeleted()) {
      currentTool.onScroll(scroll);
    }
  }

  @Override
  public void onMouseMove(float newX, float newY) {
    if (!currentTool.WasDeleted()) {
      currentTool.onMouseMove(newX, newY);
    }
  }

  //private Timer timer = new Log.Timer();
  @Override
  public void onGameTick(int tick) {
    this.tick++;

    //Log.write("start: "+timer.elapsedNano(true)/1000000);

    tickEntities(mobsGrid, mobsList);
    mobsGrid.filled();

    //Log.write("mobs: "+timer.elapsedNano(true)/1000000);

    tickEntities(projectilesGrid, projectilesList);
    for (var proj : projectilesList) {
      proj.handleProjectileCollision();
    }

    //Log.write("projs: "+timer.elapsedNano(true)/1000000);

    player.onGameTick(tick);
    if (waveRunning) {
      mobSpawner.run();
    }

    //Log.write("other: "+timer.elapsedNano(true)/1000000);
  }

  @Override
  public void delete() {
    mapSprite.delete();
  }

  @Override
  public boolean WasDeleted() {
    return false;
  }

  private <T extends GameObject & TickDetect> void tickEntities(SpacePartitioning<T> grid,
      List<T> list) {
    grid.clear();

    for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
      T e = iterator.next();
      if (e.WasDeleted()) {
        iterator.remove();
      } else {
        e.onGameTick(tick);
      }
    }
  }

  public SpriteBatching getBs() {
    return bs;
  }

  public SquareGridMobs getMobsGrid() {
    return mobsGrid;
  }

  SquareGrid<Projectile> getProjectilesGrid() {
    return projectilesGrid;
  }

  public double getMoney() {
    return money;
  }

  public void setMoney(double money) {
    this.money = money;
    resourceTracker.setText("Lives: " + health + "\nCash: " + (int) getMoney());
  }

  public Tool getCurrentTool() {
    return currentTool;
  }

  public void setCurrentTool(Tool currentTool) {
    if (this.currentTool != null) {
      this.currentTool.delete();
    }
    this.currentTool = currentTool;
  }

  public void endWave() {
    upgrades.gib(wave);
    waveRunning = false;
  }

  public void beginWave() {
    wave++;
    waveRunning = true;
    mobSpawner.onBeginWave(wave);
    Text text = new Text("Wave " + wave, "Calibri", 2000, 0, Constants.screenSize.y / 2, 10, 500,
        bs);
    Game.get().addTickable(new CallAfterDuration(text::delete, 1000));
  }

  private static class Options {

    private boolean fuckified = false;
    private boolean laggyGong = false;
  }

  private static class Optimization {

    private static final int MobGridSquareSize = 6;
    private static final int ProjectileGridSquareSize = 6;
  }

  private class MobSpawner {

    private float mobsToSpawn = 0;
    private float mobsPerTick = 1;
    private float targetMobsToSpawn = 0;

    private static float scaling(int wave) {
      return (float) Math.pow(1 + wave / 5f, 1.4);
    }

    private void onBeginWave(int waveNum) {
      mobsToSpawn = 20 * waveNum;
      targetMobsToSpawn = mobsToSpawn;
      mobsPerTick = 0.03f * waveNum;
    }

    private void run() {
      targetMobsToSpawn -= mobsPerTick;
      while (mobsToSpawn > targetMobsToSpawn && mobsToSpawn > 0) {
        mobsToSpawn--;
        TdMob e = new BasicMob(World.this);
        final float hpScaling = scaling(wave) * 1000;
        final float spdScaling = (float) Math.pow(scaling(wave), 0.2);
        e.addBuff(
            new StatBuff<TdMob>(Type.MORE, Float.POSITIVE_INFINITY, e.baseStats.health, hpScaling));
        e.addBuff(
            new StatBuff<TdMob>(Type.MORE, Float.POSITIVE_INFINITY, e.baseStats.speed, spdScaling));
        addEnemy(e);
      }
      if (mobsList.isEmpty() && mobsToSpawn == 0) {
        endWave();
      }
    }
  }
}
