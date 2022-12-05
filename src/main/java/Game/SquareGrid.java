package Game;


import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SquareGrid<T extends GameObject> implements SpacePartitioning<T> {

  public final int widthSquares, heightSquares, bottomSquares, leftSquares;
  protected final List<LinkedList<Member>> data;
  protected final int squareSizePow2;
  protected long idOfSearch = -9223372036854775806L;

  /**
   * ignores collisions outside the bounds of the grid.
   */
  public SquareGrid(int left, int bottom, int width, int height, int squareSize) {
    squareSizePow2 = squareSize;
    this.widthSquares = (width >> squareSizePow2) + 1;
    this.heightSquares = (height >> squareSizePow2) + 1;
    this.bottomSquares = (bottom >> squareSizePow2) + 1;
    this.leftSquares = (left >> squareSizePow2) + 1;
    data = new ArrayList<>(widthSquares * heightSquares);
    for (int i = 0; i < widthSquares * heightSquares; i++) {
      data.add(new LinkedList<>());
    }
  }

  @Override
  public void clear() {
    for (Collection<Member> entities : data) {
      entities.clear();
    }
  }

  @Override
  public void add(T in) {
    Rectangle hb = in.getHitbox();
    int bottom = Math.max((hb.y - hb.height >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((hb.x >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((hb.y >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((hb.x + hb.width >> squareSizePow2) - leftSquares, widthSquares - 1);
    Member m = new Member(in);

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        data.get(x + y * widthSquares).add(m);
      }
    }
  }

  /**
   * @param in search area
   * @return all objects in the SG that intersect the area, and some that do not if you wish to call
   *     a function on each of these objects, use SquareGrid::callForEach instead.
   */
  @Override
  public List<T> get(T in) {
    return get(in.getHitbox());
  }

  /**
   * @param hb search area
   * @return all objects in the SG that intersect the area, and some that do not if you wish to call
   *     a function on each of these objects, use SquareGrid::callForEach instead.
   */
  @Override
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
  @Override
  public List<T> get(Iterable<? extends Point> area) {
    List<T> detected = new LinkedList<>();
    callForEach(area, detected::add);
    return detected;
  }

  /**
   * @param hb the area from which to choose objects
   * @param F  the collide function to call on each object in area
   */
  @Override
  public void callForEach(Rectangle hb, collideFunction<T> F) {
    idOfSearch++;

    int bottom = Math.max((hb.y - hb.height >> squareSizePow2) - bottomSquares, 0);
    int left = Math.max((hb.x >> squareSizePow2) - leftSquares, 0);
    int top = Math.min((hb.y >> squareSizePow2) - bottomSquares, heightSquares - 1);
    int right = Math.min((hb.x + hb.width >> squareSizePow2) - leftSquares, widthSquares - 1);

    for (int y = bottom; y <= top; y++) {
      for (int x = left; x <= right; x++) {
        for (Member box : data.get(x + y * widthSquares)) {
          if (box.lastChecked != idOfSearch) {
            F.collide(box.hitbox);
            box.lastChecked = idOfSearch;
          }
        }
      }
    }
  }

  /**
   * @param in the area from which to choose objects
   * @param F  the collide function to call on each object in area
   */
  @Override
  public void callForEach(GameObject in, collideFunction<T> F) {
    callForEach(in.getHitbox(), F);
  }

  /**
   * @param area the area from which to choose objects
   * @param F    the collide function to call on each object in area
   */
  @Override
  public void callForEach(Iterable<? extends Point> area, collideFunction<T> F) {
    idOfSearch++;
    for (Point p : area) {
      for (Member box : data.get(p.x + p.y * widthSquares)) {
        if (box.lastChecked != idOfSearch) {
          F.collide(box.hitbox);
          box.lastChecked = idOfSearch;
        }
      }
    }
  }

  protected class Member {

    T hitbox;
    long lastChecked = -9223372036854775807L;

    Member(T hb) {
      hitbox = hb;
    }
  }
}