package Game;

import Game.Mobs.TdMob;
import Game.Mobs.TdMob.TrackProgress;
import general.Util;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;

public class SquareGridMobs extends SquareGrid<TdMob> {

  public SquareGridMobs(int left, int bottom, int width, int height, int squareSize) {
    super(left, bottom, width, height, squareSize);
  }

  public void filled() {
    for (ArrayList<TdMob> l : data) {
      l.sort(Comparator.comparing((TdMob m) -> m.getProgress()).reversed());
    }
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
}
