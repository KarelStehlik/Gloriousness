package Game;

import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengles.GLES20.GL_ONE;
import static org.lwjgl.opengles.GLES20.GL_SRC_COLOR;

import Game.Ability.AbilityGroup;
import Game.Buffs.StatBuff;
import Game.Buffs.StatBuff.Type;
import Game.Buffs.VoidFunc;
import Game.Mobs.Black;
import Game.Mobs.Blue;
import Game.Mobs.Ceramic;
import Game.Mobs.Green;
import Game.Mobs.Lead;
import Game.Mobs.Moab;
import Game.Mobs.Pink;
import Game.Mobs.Red;
import Game.Mobs.SmallMoab;
import Game.Mobs.TdMob;
import Game.Mobs.TdMob.MoveAlongTrack;
import Game.Mobs.TdMob.Stats;
import Game.Mobs.Yellow;
import Game.Turrets.BasicTurret;
import Game.Turrets.Druid;
import Game.Turrets.EatingTurret;
import Game.Turrets.EmpoweringTurret;
import Game.Turrets.IgniteTurret;
import Game.Turrets.Necromancer;
import Game.Turrets.SlowTurret;
import Game.Turrets.Turret;
import general.Constants;
import general.Data;
import general.Log;
import general.Util;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
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
  private static final int MAP = 1;
  public final List<TrackPoint> spacPoints = new ArrayList<>(500);

  public Options getOptions() {
    return options;
  }

  private final Options options = new Options();
  private final SpriteBatching bs;
  private final SquareGridMobs mobsGrid;

  public ArrayList<TdMob> getMobsList() {
    return mobsList;
  }

  private final ArrayList<TdMob> mobsList;
  private final SquareGrid<Projectile> projectilesGrid;
  private final ArrayList<Projectile> projectilesList;
  private final Player player;
  private final Sprite mapSprite;
  private final List<Point> mapData;
  private final Text resourceTracker;
  private final MobSpawner mobSpawner = new MobSpawner();
  private final UpgradeGiver upgrades = new UpgradeGiver(this);
  private final List<Turret> turrets = new ArrayList<>(1);
  private final List<VoidFunc> queuedEvents = new ArrayList<>(1);
  private Tool currentTool;

  public int getTick() {
    return tick;
  }

  private int tick = 0;
  private int health = Constants.StartingHealth;
  private double money = 100;
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

    String mapName = Data.listMaps()[MAP];

    mapSprite = new Sprite(mapName, Constants.screenSize.x / 2f, Constants.screenSize.y / 2f,
        Constants.screenSize.x, Constants.screenSize.y, 0, "basic");
    bs.addSprite(mapSprite);
    mapData = Data.getMapData(mapName);

    TurretGenerator test = BasicTurret.generator(this);

    TurretGenerator testDotTurret = IgniteTurret.generator(this);

    TurretGenerator testSlowTurret = SlowTurret.generator(this);

    TurretGenerator testEmp = EmpoweringTurret.generator(this);

    TurretGenerator testEating = EatingTurret.generator(this);

    TurretGenerator necro = Necromancer.generator(this);

    TurretGenerator druid = Druid.generator(this);

    TurretGenerator[] availableTurrets = new TurretGenerator[]{test, testDotTurret, testSlowTurret,
        testEmp, testEating, necro, druid};

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
      for (int i = 0; i < (options.laggyGong ? 2000 : 1); i++) {
        explosionVisual(x, y, 100, true, "Explosion1-0");
      }
    }, null));

    resourceTracker = new Text("Lives: " + health + "\nCash: " + (long) getMoney(), "Calibri", 500,
        0, 1050, 10, 40, bs);
    resourceTracker.setColors(Util.getColors(1, 1, 1));

    currentTool = new PlaceObjectTool(this, new NoSprite(), (x, y) -> false);
    currentTool.delete();
    beginWave();
    calcSpacPoints();
  }

  private void updateResourceTracker() {
    resourceTracker.setText("Lives: " + health + "\nCash: " + (long) getMoney() + "\nWave " + wave);
  }

  public void addEvent(VoidFunc e) {
    queuedEvents.add(e);
  }

  public boolean tryPurchase(float cost) {
    if (money < cost) {
      return false;
    }
    setMoney(money - cost);
    return true;
  }

  private void calcSpacPoints() {
    GameObject fakeBloon = new GameObject(mapData.get(0).x, mapData.get(0).y, 0, 0, this);
    float[] speed = new float[1];
    speed[0] = 1;
    TdMob.MoveAlongTrack<GameObject> mover = new MoveAlongTrack<>(false, mapData,
        new Point(0, 0), speed, 0, o -> {
    });
    while (!mover.isDone()) {
      spacPoints.add(new TrackPoint((int) fakeBloon.x, (int) fakeBloon.y,
          mover.getProgress().getCheckpoint()));
      mover.tick(fakeBloon);
    }
  }

  public Animation lesserExplosionVisual(float x, float y, float size) {
    float duration = .2f;
    float scaling = size * 2 / 1000 * Game.tickIntervalMillis / duration;
    Sprite sp = new Sprite("Shockwave", 4).setPosition(x, y).setSize(0, 0).addToBs(bs)
        .setColors(Util.getColors(2.4f, .6f, 0));
    var anim = new Animation(sp, duration).setLinearScaling(new Vector2f(scaling, scaling));
    Game.get().addTickable(anim);
    return anim;
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
    Sprite sp = new SingleAnimationSprite(image, .7f, x, y, size * 2, size * 2, 4, "basic").
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
    updateResourceTracker();
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

  private final CustomBuffCheat customBuffCheat = new CustomBuffCheat();

  public List<Turret> getTurrets() {
    return turrets;
  }

  private class CustomBuffCheat {

    private static final String[] types = new String[]{"MORE", "INCREASED", "ADDED",
        "FINALLY_ADDED"};
    private static final String[] stats = new String[]{"speed", "aspd", "projSize", "projSpeed",
        "projPierce", "projDuration", "projPower"};
    private final ImInt buffType = new ImInt(0);
    private final ImInt buffStat = new ImInt(0);
    private final float[] value = new float[]{0};

    void imGui() {

      ImGui.combo("type", buffType, types);
      ImGui.combo("stat", buffStat, stats);
      ImGui.dragFloat("amount", value, 1);
      if (ImGui.button("apply")) {
        apply();
      }
    }

    void apply() {
      addEvent(() -> {
        String btString = types[buffType.get()];
        StatBuff.Type type = switch (btString) {
          case "INCREASED" -> Type.INCREASED;
          case "ADDED" -> Type.ADDED;
          case "FINALLY_ADDED" -> Type.FINALLY_ADDED;
          default -> Type.MORE;
        };
        String bsString = stats[buffStat.get()];
        int stat = switch (bsString) {
          case "aspd" -> 2;
          case "projSize" -> 3;
          case "projSpeed" -> 4;
          case "projPierce" -> 5;
          case "projDuration" -> 6;
          case "projPower" -> 7;
          default -> 0;
        };

        player.addBuff(new StatBuff<Player>(type, stat, value[0]));

      });
    }
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
    if (ImGui.collapsingHeader("Well, fuck you too")) {
      ImBoolean path = new ImBoolean(options.ultimateCrosspathing);

      if (ImGui.button("give cash")) {
        setMoney(money + 999999);
      }
      if (ImGui.button("yeet projectiles (" + projectilesList.size() + ")")) {
        addEvent(() -> {
          projectilesList.forEach(Projectile::delete);
          projectilesList.clear();
        });
      }
      if (ImGui.button("yeet mobs (" + mobsList.size() + ")")) {
        addEvent(() -> {
          mobsList.forEach(TdMob::delete);
          mobsList.clear();
        });
      }
      if (ImGui.checkbox("5-5-5 allowed", path)) {
        options.ultimateCrosspathing = path.get();
      }

      ImBoolean cheat = new ImBoolean(mobSpawner.cheat);
      if (ImGui.checkbox("Waves go brr", cheat)) {
        mobSpawner.cheat = cheat.get();
      }

      int[] currWave = new int[]{wave};
      if (ImGui.dragInt("Wave", currWave, 1, 0, 1000000)) {
        addEvent(() -> {
          wave = currWave[0] - 1;
          updateResourceTracker();
        });
      }
      if (ImGui.collapsingHeader("Add buff to player")) {
        customBuffCheat.imGui();
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
  public int getLayer() {
    return 5;
  }

  @Override
  public boolean onMouseButton(int button, double x, double y, int action, int mods) {
    if (!currentTool.WasDeleted()) {
      return currentTool.onMouseButton(button, x, y, action, mods);
    }
    return false;
  }

  @Override
  public boolean onScroll(double scroll) {
    if (!currentTool.WasDeleted()) {
      return currentTool.onScroll(scroll);
    }
    return false;
  }

  @Override
  public boolean onMouseMove(float newX, float newY) {
    if (!currentTool.WasDeleted()) {
      return currentTool.onMouseMove(newX, newY);
    }
    return false;
  }

  //private Timer timer = new Log.Timer();
  @Override
  public void onGameTick(int tick) {
    for (var e : queuedEvents) {
      e.apply();
    }
    queuedEvents.clear();

    //Log.write("start: "+timer.elapsedNano(true)/1000000);
    if (mobSpawner.cheat) {
      //noinspection ForLoopReplaceableByForEach
      for (int i = 0; i < mobsList.size(); i++) {
        mobsList.get(i).takeDamage(1000, DamageType.TRUE);
      }
    }
    tickEntities(mobsGrid, mobsList);
    mobsGrid.filled();

    //Log.write("mobs: "+timer.elapsedNano(true)/1000000);
    AbilityGroup.instances.removeIf(AbilityGroup::WasDeleted);

    if (waveRunning) {
      this.tick++;
      player.onGameTick(tick);
      AbilityGroup.instances.forEach(g -> g.onGameTick(tick));
      int undeleted = 0;
      for (int current = 0; current < turrets.size(); current++) {
        if (current != undeleted) {
          turrets.set(undeleted, turrets.get(current));
        }
        if (!turrets.get(current).WasDeleted()) {
          turrets.get(current).onGameTick(tick);
          undeleted++;
        }
      }
      if (turrets.size() > undeleted) {
        turrets.subList(undeleted, turrets.size()).clear();
      }

      tickEntities(projectilesGrid, projectilesList);
      for (var proj : projectilesList) {
        proj.handleProjectileCollision();
      }
      //Log.write("projs: "+timer.elapsedNano(true)/1000000);
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
      final ArrayList<T> list) {
    grid.clear();

    int undeleted = 0;
    for (int current = 0; current < list.size(); current++) {
      if (current != undeleted) {
        list.set(undeleted, list.get(current));
      }
      if (!list.get(current).WasDeleted()) {
        list.get(current).onGameTick(tick);
        undeleted++;
      }
    }
    if (list.size() > undeleted) {
      list.subList(undeleted, list.size()).clear();
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
    updateResourceTracker();
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
    turrets.forEach(Turret::endOfRound);
    if (mobSpawner.cheat) {
      beginWave();
    } else {
      upgrades.gib(wave);
      waveRunning = false;
    }
  }

  public void beginWave() {
    wave++;
    updateResourceTracker();
    waveRunning = true;
    mobSpawner.onBeginWave(wave);
    Text text = new Text("Wave " + wave, "Calibri", 1800, 60, Constants.screenSize.y / 2, 10,
        490 / (float) ((int) Math.log10(wave) + 7) * 7,
        bs, "colorCycle2", "Button");
    Game.get().addTickable(new CallAfterDuration(text::delete, 1000));
    text.setColors(Util.getCycle2colors(1));
  }

  public boolean canFitTurret(int x, int y, float size) {
    for (Iterator<Turret> iterator = turrets.iterator(); iterator.hasNext(); ) {
      Turret t = iterator.next();
      if (t.WasDeleted()) {
        iterator.remove();
      } else if (!t.isNotYetPlaced() && Util.distanceSquared(x - t.x, y - t.y)
          < Util.square(size + t.stats[Turret.Stats.size])) {
        return false;
      }
    }
    for (TrackPoint p : spacPoints) {
      if (Util.distanceSquared(p.x - x, p.y - y) < size * size) {
        return false;
      }
    }
    return true;
  }

  public void addTurret(Turret turret) {
    turrets.add(turret);
  }

  public static class TrackPoint {

    private final float x;
    private final float y;
    private final int node;

    public TrackPoint(float x, float y, int node) {
      this.x = x;
      this.y = y;
      this.node = node;
    }

    public float getX() {
      return x;
    }

    public float getY() {
      return y;
    }

    public int getNode() {
      return node;
    }
  }

  public static class Options {

    public boolean isFuckified() {
      return fuckified;
    }

    public boolean isLaggyGong() {
      return laggyGong;
    }

    public boolean isUltimateCrosspathing() {
      return ultimateCrosspathing;
    }

    private boolean fuckified = false;
    private boolean laggyGong = false;
    private boolean ultimateCrosspathing = false;
  }

  private static class Optimization {

    private static final int MobGridSquareSize = 6;
    private static final int ProjectileGridSquareSize = 7;
  }

  private class MobSpawner {

    private final List<BloonSpawn> bloons = List.of(
        new BloonSpawn(200, Moab::new),
        new BloonSpawn(100, SmallMoab::new),
        new BloonSpawn(20, Ceramic::new),
        new BloonSpawn(12, Lead::new),
        new BloonSpawn(7, Black::new),
        new BloonSpawn(4, Pink::new),
        new BloonSpawn(3.5f, Yellow::new),
        new BloonSpawn(2.8f, Green::new),
        new BloonSpawn(2, Blue::new),
        new BloonSpawn(1, Red::new)
    );
    public boolean cheat = false;
    private float mobsToSpawn = 0;
    private float mobsPerTick = 1;
    private float spawningProcess = 0;
    private BloonSpawn next;

    private BloonSpawn selectNectBloon(float toSpawn) {
      for (var loon : bloons) {
        if (loon.cost < wave * 4 && loon.cost < toSpawn
            && Data.gameMechanicsRng.nextFloat() < toSpawn / loon.cost / 200) {
          return loon;
        }
      }
      return bloons.get(bloons.size() - 1);
    }

    private float scaling() {
      return cheat ? 1 : (float) (Math.pow(1 + Math.max(wave, 10) - 10, 1.4) // scaling after 10
          + Math.pow(1 + Math.max(wave, 40) - 40, 1) - 1 // real scaling after 40
          + Math.pow(1 + Math.max(wave, 100) - 100, 2) - 1// steep scaling after 100
          + Math.pow(1.1, Math.max(wave, 200) - 200) - 1); // exponential after 250
    }

    private void add(TdMob e) {
      final float hpScaling = scaling();
      final float spdScaling = (float) Math.pow(scaling(), 0.1);
      e.addBuff(
          new StatBuff<TdMob>(Type.MORE, Stats.health,
              hpScaling));
      e.addBuff(
          new StatBuff<TdMob>(Type.MORE, Stats.speed,
              spdScaling));
      addEnemy(e);
    }

    private void onBeginWave(int waveNum) {
      mobsToSpawn = cheat ? 1 : Math.min(50000, (float) (50 * Math.pow(waveNum, 1.1)));
      mobsPerTick = cheat ? 1 : Math.min(200, 0.1f * waveNum);
      spawningProcess = 0;
    }

    private void run() {
      spawningProcess += Math.min(mobsPerTick, mobsToSpawn);
      mobsToSpawn = Math.max(0, mobsToSpawn - mobsPerTick);
      if (mobsToSpawn + spawningProcess < bloons.get(bloons.size() - 1).cost) {
        if (mobsList.isEmpty()) {
          endWave();
        }
        return;
      }
      if (next == null) {
        next = selectNectBloon(mobsToSpawn);
      }
      while (next.cost <= spawningProcess) {
        spawningProcess -= next.cost;
        add(next.spawn());
        next = selectNectBloon(mobsToSpawn);
      }
    }

    class BloonSpawn {

      private final float cost;
      private final Spawner spawn;

      BloonSpawn(float cost, Spawner newBloon) {
        this.cost = cost;
        spawn = newBloon;
      }

      float getCost() {
        return cost;
      }

      TdMob spawn() {
        return spawn.spawn(World.this);
      }

      @FunctionalInterface
      interface Spawner {

        TdMob spawn(World w);
      }
    }
  }
}
