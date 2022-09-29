package Game;


import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class SquareGrid<T extends GameObject> {

  private final List<ArrayList<LinkedList<Member>>> data;
  private static final int SQUARE_SIZE_POW2 = 5;
  public final int widthSquares, heightSquares, bottomSquares, leftSquares;
  private long idOfSearch = -9223372036854775806L;

  private class Member {

    T hitbox;
    long lastChecked = -9223372036854775807L;

    Member(T hb) {
      hitbox = hb;
    }
  }

  // ignores collisions outside the bounds of the grid.
  public SquareGrid(int left, int bottom, int width, int height) {
    this.widthSquares = (width >> SQUARE_SIZE_POW2) + 1;
    this.heightSquares = (height >> SQUARE_SIZE_POW2) + 1;
    this.bottomSquares = (bottom >> SQUARE_SIZE_POW2) + 1;
    this.leftSquares = (left >> SQUARE_SIZE_POW2) + 1;
    data = new ArrayList<>(widthSquares);
    clear();
  }

  public void clear() {
    data.clear();
    for (int x = 0; x < widthSquares; x++) {
      data.add(new ArrayList<>(heightSquares));
      for (int y = 0; y < heightSquares; y++) {
        data.get(x).add(new LinkedList<>());
      }
    }
  }

  public void add(T in) {
    Rectangle hb = in.getHitbox();
    int bottom = Math.max((hb.y - hb.height >> SQUARE_SIZE_POW2) - bottomSquares, 0);
    int left = Math.max((hb.x >> SQUARE_SIZE_POW2) - leftSquares,0);
    int top = Math.min((hb.y >> SQUARE_SIZE_POW2) - bottomSquares, heightSquares-1);
    int right = Math.min((hb.x + hb.width >> SQUARE_SIZE_POW2) - leftSquares, widthSquares-1);
    Member m = new Member(in);

    for (int x = left; x <= right; x++) {
      for (int y = bottom; y <= top; y++) {
        data.get(x).get(y).add(m);
      }
    }
  }

  /**
   * @param in search area
   * @return all objects in the SG that intersect the area, and some that do not
   * if you wish to call a function on each of these objects, use SquareGrid::callForEach instead.
   */
  public List<T> get(T in) {
    return get(in.getHitbox());
  }

  /**
   * @param hb search area
   * @return all objects in the SG that intersect the area, and some that do not
   * if you wish to call a function on each of these objects, use SquareGrid::callForEach instead.
   */
  public List<T> get(Rectangle hb) {
    idOfSearch++;

    int bottom = Math.max((hb.y - hb.height >> SQUARE_SIZE_POW2) - bottomSquares, 0);
    int left = Math.max((hb.x >> SQUARE_SIZE_POW2) - leftSquares,0);
    int top = Math.min((hb.y >> SQUARE_SIZE_POW2) - bottomSquares, heightSquares-1);
    int right = Math.min((hb.x + hb.width >> SQUARE_SIZE_POW2) - leftSquares, widthSquares-1);

    List<T> detected = new LinkedList<>();

    for (int x = left; x <= right; x++) {
      for (int y = bottom; y <= top; y++) {
        for (Member box : data.get(x).get(y)) {
          if (box.lastChecked != idOfSearch) {
            detected.add(box.hitbox);
            box.lastChecked = idOfSearch;
          }
        }
      }
    }
    return detected;
  }

  /**
   * @param area search area
   * @return all objects in the SG that intersect the area, and some that do not
   * if you wish to call a function on each of these objects, use SquareGrid::callForEach instead.
   */
  public List<T> get(Iterable<? extends Point> area) {
    idOfSearch++;
    List<T> detected = new LinkedList<>();
    for (Point p : area) {
      for (Member box : data.get(p.x).get(p.y)) {
        if (box.lastChecked != idOfSearch) {
          detected.add(box.hitbox);
          box.lastChecked = idOfSearch;
        }
      }
    }
    return detected;
  }


  /**
   * @param hb the area from which to choose objects
   * @param F the collide function to call on each object in area
   */
  public void callForEach(Rectangle hb, collideFuncion<T> F){
    idOfSearch++;

    int bottom = Math.max((hb.y - hb.height >> SQUARE_SIZE_POW2) - bottomSquares, 0);
    int left = Math.max((hb.x >> SQUARE_SIZE_POW2) - leftSquares,0);
    int top = Math.min((hb.y >> SQUARE_SIZE_POW2) - bottomSquares, heightSquares-1);
    int right = Math.min((hb.x + hb.width >> SQUARE_SIZE_POW2) - leftSquares, widthSquares-1);

    for (int x = left; x <= right; x++) {
      for (int y = bottom; y <= top; y++) {
        for (Member box : data.get(x).get(y)) {
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
   * @param F the collide function to call on each object in area
   */
  public void callForEach(GameObject in, collideFuncion<T> F){callForEach(in.getHitbox(), F);}

  /**
   * @param area the area from which to choose objects
   * @param F the collide function to call on each object in area
   */
  public void callForEach(Iterable<? extends Point> area, collideFuncion<T> F) {
    idOfSearch++;
    for (Point p : area) {
      for (Member box : data.get(p.x).get(p.y)) {
        if (box.lastChecked != idOfSearch) {
          F.collide(box.hitbox);
          box.lastChecked = idOfSearch;
        }
      }
    }
  }

  @FunctionalInterface
  interface collideFuncion<T>{
    void collide(T other);
  }
}