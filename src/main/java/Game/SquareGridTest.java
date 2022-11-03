package Game;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SquareGridTest<T extends GameObject> {

  public final int widthSquares, heightSquares, bottomSquares, leftSquares;
  private final List<ArrayList<T>> data;
  private final int squareSizePow2;
  private long idOfSearch = -9223372036854775806L;

  /**
   * ignores collisions outside the bounds of the grid.
   */
  public SquareGridTest(int left, int bottom, int width, int height, int squareSize) {
    squareSizePow2 = squareSize;
    this.widthSquares = (width >> squareSizePow2) + 1;
    this.heightSquares = (height >> squareSizePow2) + 1;
    this.bottomSquares = (bottom >> squareSizePow2) + 1;
    this.leftSquares = (left >> squareSizePow2) + 1;
    data = new ArrayList<>(widthSquares * heightSquares);
    for (int i = 0; i < widthSquares * heightSquares; i++) {
      data.add(new ArrayList<>(4));
    }
  }

  public void clear() {
    for (Collection<T> entities : data) {
      entities.clear();
    }
  }

  public void add(T in) {
    Rectangle hb = in.getHitbox();
    int bottom = Math.max((hb.y - hb.height >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((hb.x >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((hb.y >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((hb.x + hb.width >> squareSizePow2) - leftSquares, widthSquares - 1);

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        data.get(x + y * widthSquares).add(in);
      }
    }
  }

  /**
   * @param in search area
   * @return all objects in the SG that intersect the area, and some that do not if you wish to call
   *     a function on each of these objects, use SquareGrid::callForEach instead.
   */
  public List<T> get(T in) {
    return get(in.getHitbox());
  }

  /**
   * @param hb search area
   * @return all objects in the SG that intersect the area, and some that do not if you wish to call
   *     a function on each of these objects, use SquareGrid::callForEach instead.
   */
  public List<T> get(Rectangle hb) {
    List<T> detected = new LinkedList<>();
    callForEach(hb, detected::add);
    return detected;
  }

  /**
   * @param area search area
   * @return all objects in the SG that intersect the area, and some that do not if you wish to call
   *     a function on each of these objects, use SquareGrid::callForEach instead.
   */
  public List<T> get(Iterable<? extends Point> area) {
    List<T> detected = new LinkedList<>();
    callForEach(area, detected::add);
    return detected;
  }

  /**
   * @param hb the area from which to choose objects
   * @param F  the collide function to call on each object in area
   */
  public void callForEach(Rectangle hb, collideFunction<T> F) {
    idOfSearch++;

    int bottom = Math.max((hb.y - hb.height >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((hb.x >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((hb.y >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((hb.x + hb.width >> squareSizePow2) - leftSquares, widthSquares - 1);

    List<T> checked = new ArrayList<>(50);
    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        for (T box : data.get(x + y * widthSquares)) {
          if (!checked.contains(box)) {
            F.collide(box);
            checked.add(box);
          }
        }
      }
    }
  }

  /**
   * @param in the area from which to choose objects
   * @param F  the collide function to call on each object in area
   */
  public void callForEach(GameObject in, collideFunction<T> F) {
    callForEach(in.getHitbox(), F);
  }

  /**
   * @param area the area from which to choose objects
   * @param F    the collide function to call on each object in area
   */
  public void callForEach(Iterable<? extends Point> area, collideFunction<T> F) {
    idOfSearch++;
    List<T> checked = new ArrayList<>(50);
    for (Point p : area) {
      for (T box : data.get(p.x + p.y * widthSquares)) {
        if (!checked.contains(box)) {
          F.collide(box);
          checked.add(box);
        }
      }
    }
  }

  @FunctionalInterface
  interface collideFunction<T> {

    void collide(T other);
  }

  private class Member {

    T hitbox;
    long lastChecked = -9223372036854775807L;

    Member(T hb) {
      hitbox = hb;
    }

    void set(T target) {
      hitbox = target;
      lastChecked = -9223372036854775807L;
    }
  }
}