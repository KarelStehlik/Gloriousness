package Game.Misc;

import Game.Enums.TargetingOption;
import Game.Mobs.MobClasses.TdMob;
import GlobalUse.Log;
import GlobalUse.Util;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;

public class SquareGridMobs extends SquareGrid<TdMob> {

  private final ArrayList<TdMob> strongest;

  public SquareGridMobs(int left, int bottom, int width, int height, int squareSize) {
    super(left, bottom, width, height, squareSize);
    strongest = new ArrayList<>(data.size());
    for (var d : data) {
      strongest.add(null);
    }
  }

  @Override
  public void clear() {
    for (int i = 0; i < data.size(); i++) {
      ArrayList<TdMob> entities = data.get(i);
      entities.clear();
      strongest.set(i, null);
    }
  }

  @Override
  public void add(TdMob in) {
    Rectangle hb = in.getHitbox();
    int bottom = Math.max((hb.y - hb.height >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((hb.x >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((hb.y >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((hb.x + hb.width >> squareSizePow2) - leftSquares, widthSquares - 1);

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        int index = x + y * widthSquares;
        data.get(index).add(in);
        if (strongest.get(index) == null || in.getStats()[TdMob.Stats.health] > strongest.get(index)
            .getStats()[TdMob.Stats.health]) {
          strongest.set(index, in);
        }
      }
    }
  }

  public void filled() {
    for (ArrayList<TdMob> l : data) {
      l.sort(Comparator.comparing(TdMob::getProgress).reversed());
    }
  }

  public TdMob search(Point centre, int radius, TargetingOption targeting) {
    ArrayList<TdMob> found = search(centre, radius, targeting, null, 1);
    return found.isEmpty() ? null : found.get(0);
  }

  public ArrayList<TdMob> search(Point centre, int radius, TargetingOption targeting,
      int maxCount) {
    return search(centre, radius, targeting, null, maxCount);
  }

  public TdMob search(Point centre, int radius, TargetingOption targeting,
      Predicate<? super TdMob> condition) {
    ArrayList<TdMob> found = search(centre, radius, targeting, condition, 1);
    return found.isEmpty() ? null : found.get(0);
  }

  public ArrayList<TdMob> search(Point centre, int radius, TargetingOption targeting,
      Predicate<? super TdMob> condition, int maxCount) {
    idOfSearch++;
    if (condition == null) {
      condition = mob -> true;
    }
    int bottom = Math.max((centre.y - radius >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((centre.x - radius >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((centre.y + radius >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((centre.x + radius >> squareSizePow2) - leftSquares, widthSquares - 1);
    TdMob leastGood = null;//the worst good-enough-so-far bloon
    Comparator<TdMob> comp = targeting.getComparator();
    ArrayList<TdMob> result = new ArrayList<>(maxCount) {
      @Override
      public boolean add(TdMob obj) {
        int index = Collections.binarySearch(this, obj,
            comp); //finds index at which this would be sorted
        //if element is not present it's -index-1
        //I guess so that 0 index not present is distinct
        if (index < 0) {
          index = -index - 1;
        }
        super.add(index, obj);
        return true;
      }
    };

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        for (int index = 0; ; index++) {
          TdMob bloon = getBesiInBox(x, y, index, targeting);
          if (bloon == null) {
            break;
          }
          if (leastGood != null && (comp.compare(bloon, leastGood) <= 0)) {
            break;
          }
          if (bloon.lastChecked == idOfSearch) {
            if (maxCount
                == 1) {// this is the index best bloon in the square. if we have already seen it, no other one can be better.
              break;
            }
            continue;
          }
          bloon.lastChecked = idOfSearch;
          if (Util.distanceSquared(bloon.x - centre.x, bloon.y - centre.y)
              < radius * radius && condition.test(bloon)) {

            if (result.size() == maxCount) {
              result.remove(0);
              result.add(bloon);//will be sorted bc we override the add method
            } else {
              result.add(bloon);
            }
            if (result.size() == maxCount) {
              leastGood = result.get(0);
            }
            if (maxCount == 1) {
              break;
            }
          }
        }
      }
    }
    return result;
  }

  public TdMob getBesiInBox(int x, int y, int index,
      TargetingOption targeting) {//returns index best bloon according to condition
    ArrayList<TdMob> byProgress = data.get(
        x + y * widthSquares);                                                       //in box x y
    if (index >= byProgress.size()) {
      return null;
    }
    switch (targeting) {
      case FIRST -> {
        return byProgress.get(index);
      }
      case STRONG -> {
        if (index == 0) {
          return strongest.get(x + y * widthSquares);
        }
        TdMob improvise = byProgress.get(index - 1);
        if (improvise == strongest.get(
            x + y * widthSquares)) { //to not return strongest twice, gets last
          //this should be second strongest and we will pretend that it is
          return byProgress.get(byProgress.size() - 1);
        } else {
          return improvise;
        }
      }
      case LAST -> {
        return byProgress.get(byProgress.size() - 1 - index);
      }
      default -> {
        Log.write("UNKNOWN PRIORITY STRATEGY OF " + targeting);
        return null;
      }
    }
  }
}
