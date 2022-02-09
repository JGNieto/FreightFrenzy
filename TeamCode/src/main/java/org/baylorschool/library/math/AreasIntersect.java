package org.baylorschool.library.math;

import org.baylorschool.Globals;
import org.baylorschool.library.Location;

public class AreasIntersect {

    static class Area {
        final Point point1;
        final Point point2;
        final Point point3;
        final Point point4;

        public Area(Point point1, Point point2, Point point3, Point point4) {
            this.point1 = point1;
            this.point2 = point2;
            this.point3 = point3;
            this.point4 = point4;
        }
    }

    // We make a new point class, instead of the Location class, because Location has a lot of stuff we do not need.
    static class Point {
        final double x;
        final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Point(Location location) {
            this.x = location.getX();
            this.y = location.getY();
        }
    }

    public static boolean isFullyParkedWarehouse(Location location, Globals.WarehouseSide side) {
        // FIXME: WE NEED TO IMPLEMENT THIS
        // For now, to be safe, return false.
        return false;
    }
}
