package Game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;

import Game.Buffs.VoidFunc;
import general.Util;
import general.Util.Cycle2Colors;
import java.util.ArrayList;
import java.util.List;
import windowStuff.Button;
import windowStuff.SimpleText;
import windowStuff.Sprite;

public class Ability {

  public static class AbilityGroup implements KeyboardDetect, TickDetect {

    public final Button button;
    public final Sprite bg;
    private final List<Ability> abilities = new ArrayList<>(1);
    public static final List<AbilityGroup> instances = new ArrayList<>(1);
    private final long id;

    AbilityGroup(String image, SimpleText.TextGenerator text, long id) {
      bg = new Sprite("Button", 9).setSize(90, 90).addToBs(Game.get().getSpriteBatching("main"));
      button = new Button(
          Game.get().getSpriteBatching("main"),
          new Sprite(image, 10).setSize(75, 75),
          (x, y) -> this.triggered(),
          () -> {
            var min = abilities.stream().mapToInt(a -> (int) (a.cooldownRemaining / 1000f)).min();
            return text.get() + "\nCooldown:" + (min.isPresent() ? min.getAsInt() : "???")
                + "\nUses: " + abilities.stream().filter(a -> a.cooldownRemaining <= 0).count();
          }
      );
      button.getSprite().setShader("colorCycle2").setColors(new Cycle2Colors().setStrength(0.5f).get());
      Game.get().addMouseDetect(button);
      Game.get().addTickable(button);
      Game.get().addKeyDetect(this);
      instances.add(this);
      setup(instances.size() - 1);
      this.id = id;
    }

    private int keyBind;

    private void setup(int position) {
      button.getSprite().setPosition(275 + position * 110, 50);
      bg.setPosition(275 + position * 110, 50);
      keyBind = GLFW_KEY_1 + position;
    }

    private boolean ready = true;

    private void setReady(boolean r) {
      if (ready != r) {
        ready = r;
        if (r) {
          button.getSprite().setShader("colorCycle2").setColors(new Cycle2Colors().setStrength(0.5f).get());
        } else {
          button.getSprite().setShader("basic").setColors(Util.getColors(.2f, .2f, .2f));
        }
      }
    }

    private void triggered() {
      int i = 0;
      for (; i < abilities.size(); i++) {
        Ability a = abilities.get(i);
        if (a.cooldownRemaining <= 0) {
          a.onTrigger.apply();
          a.cooldownRemaining += a.cooldown;
          break;
        }
      }
      for (; i < abilities.size(); i++) {
        Ability a = abilities.get(i);
        if (a.cooldownRemaining <= 0) {
          return;
        }
      }
      setReady(false);
    }

    @Override
    public void onGameTick(int tick) {
      for (Ability a : abilities) {
        if (a.cooldownRemaining > 0) {
          a.cooldownRemaining -= Game.tickIntervalMillis;
        } else {
          setReady(true);
        }
      }
    }

    @Override
    public void onKeyPress(int key, int action, int mods) {

      if (key == keyBind && action == 1 && mods == 0) {
        triggered();
      }
    }

    @Override
    public void delete() {
      button.delete();
      bg.delete();
      boolean deleted = false;
      int i = 0;
      for (AbilityGroup a : instances) {
        if (a == this) {
          deleted = true;
          i--;
        }
        if (deleted) {
          a.setup(i);
        }
        i++;
      }
    }

    @Override
    public boolean WasDeleted() {
      return button.WasDeleted();
    }

    public void add(Ability a) {
      abilities.add(a);
      if (a.cooldownRemaining <= 0) {
        setReady(true);
      }
    }

    public void remove(Ability ability) {
      abilities.remove(ability);

      if (abilities.isEmpty()) {
        delete();
        return;
      }

      for (Ability a : abilities) {
        if (a.cooldownRemaining <= 0) {
          return;
        }
      }
      setReady(false);
    }
  }

  private final float cooldown;
  private float cooldownRemaining;
  private final VoidFunc onTrigger;
  private final long id;

  public static Ability add(String image, float cooldown, SimpleText.TextGenerator text,
      VoidFunc onTrigger, long id) {
    Ability a = new Ability(cooldown, onTrigger, id);
    for (AbilityGroup g : AbilityGroup.instances) {
      if (g.id == id) {
        g.add(a);
        return a;
      }
    }
    AbilityGroup g = new AbilityGroup(image, text, id);
    g.add(a);
    return a;
  }

  public void delete() {
    for (AbilityGroup g : AbilityGroup.instances) {
      if (g.id == id) {
        g.remove(this);
        return;
      }
    }
  }

  private Ability(float cooldown, VoidFunc onTrigger, long id) {
    this.cooldown = cooldown;
    this.onTrigger = onTrigger;
    this.id = id;
  }
}
