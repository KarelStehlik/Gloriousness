package Game;

import java.awt.*;
import java.util.List;

public interface SpacePartitioning<T extends GameObject> {

    void clear();

    void add(T in);

    /**
     * @param in search area
     * @return all objects in the SG that intersect the area, and some that do not if you wish to call
     * a function on each of these objects, use SquareGrid::callForEach instead for performance.
     */
    List<T> get(T in);

    /**
     * @param hb search area
     * @return all objects in the SG that intersect the area, and some that do not if you wish to call
     * a function on each of these objects, use SquareGrid::callForEach instead for performance.
     */
    List<T> get(Rectangle hb);

    /**
     * @param area search area
     * @return all objects in the SG that intersect the area, and some that do not if you wish to call
     * a function on each of these objects, use SquareGrid::callForEach instead for performance.
     */
    List<T> get(Iterable<? extends Point> area);

    /**
     * @param hb the area from which to choose objects
     * @param F  the collide function to call on each object in area
     */
    void callForEach(Rectangle hb, collideFunction<T> F);

    /**
     * @param in the area from which to choose objects
     * @param F  the collide function to call on each object in area
     */
    void callForEach(GameObject in, collideFunction<T> F);

    /**
     * @param area the area from which to choose objects
     * @param F    the collide function to call on each object in area
     */
    void callForEach(Iterable<? extends Point> area, collideFunction<T> F);

    @FunctionalInterface
    interface collideFunction<T> {

        void collide(T other);
    }
}
