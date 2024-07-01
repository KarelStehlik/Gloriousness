package Game;

import Game.Mobs.TdMob;
import Game.Mobs.TdMob.TrackProgress;
import general.Util;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
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

  // is approximate, may return mobs that are out of range.
  public TdMob getStrong(Point centre, int radius) {
    idOfSearch++;

    int bottom = Math.max((centre.y - radius >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((centre.x - radius >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((centre.y + radius >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((centre.x + radius >> squareSizePow2) - leftSquares, widthSquares - 1);

    TdMob best = null;
    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        TdMob candidate = strongest.get(x + y * widthSquares);
        if (candidate == null) {
        } else if (best == null) {
          best = candidate;
        } else if (candidate.getStats()[TdMob.Stats.health] > best.getStats()[TdMob.Stats.health] ||
            (candidate.getStats()[TdMob.Stats.health] == best.getStats()[TdMob.Stats.health]
                && candidate.getProgress().compareTo(best.getProgress()) > 0)) {
          best = candidate;
        }
      }
    }
    return best;
  }

  public TdMob getFirst(Point centre, int radius) {
    idOfSearch++;

    int bottom = Math.max((centre.y - radius >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((centre.x - radius >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((centre.y + radius >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((centre.x + radius >> squareSizePow2) - leftSquares, widthSquares - 1);

    TdMob.TrackProgress best = new TrackProgress(-1, 0);
    TdMob result = null;

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        for (TdMob box : data.get(x + y * widthSquares)) {
          if (box.lastChecked == idOfSearch || box.getProgress().compareTo(best) < 0) {
            break;
          } // this is the most advanced box in the square. if we have already seen it, no other one can be farther.
          box.lastChecked = idOfSearch;
          if (Util.distanceSquared(box.x - centre.x, box.y - centre.y)
              < radius * radius) {
            result = box;
            best = result.getProgress();
            break;
          }
        }
      }
    }
    return result;
  }


  public TdMob getLast(Point centre, int radius) {
    idOfSearch++;

    int bottom = Math.max((centre.y - radius >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((centre.x - radius >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((centre.y + radius >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((centre.x + radius >> squareSizePow2) - leftSquares, widthSquares - 1);

    TdMob.TrackProgress best = new TrackProgress(Integer.MAX_VALUE, 0);
    TdMob result = null;

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {

        ArrayList<TdMob> get = data.get(x + y * widthSquares);
        for (int i = get.size() - 1; i >= 0; i--) {
          TdMob box = get.get(i);

          if (box.lastChecked == idOfSearch || box.getProgress().compareTo(best) > 0) {
            break;
          } // this is the least advanced box in the square.
          box.lastChecked = idOfSearch;
          if (Util.distanceSquared(box.x - centre.x, box.y - centre.y)
              < radius * radius) {
            result = box;
            best = result.getProgress();
            break;
          }
        }
      }
    }
    return result;
  }


  public TdMob getFirst(Point centre, int radius, Predicate<? super TdMob> condition) {
    idOfSearch++;

    int bottom = Math.max((centre.y - radius >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((centre.x - radius >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((centre.y + radius >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((centre.x + radius >> squareSizePow2) - leftSquares, widthSquares - 1);

    TdMob.TrackProgress best = new TrackProgress(-1, 0);
    TdMob result = null;

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        for (TdMob box : data.get(x + y * widthSquares)) {
          if (box.lastChecked == idOfSearch || box.getProgress().compareTo(best) < 0) {
            break;
          } // this is the most advanced box in the square. if we have already seen it, no other one can be farther.
          box.lastChecked = idOfSearch;
          if (Util.distanceSquared(box.x - centre.x, box.y - centre.y)
              < radius * radius && condition.test(box)) {
            result = box;
            best = box.getProgress();
            break;
          }
        }
      }
    }
    return result;
  }
}
