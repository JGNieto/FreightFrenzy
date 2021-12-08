package org.baylorschool.library.math;

import org.baylorschool.library.Location;

public class PerpendicularDistance {
    // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
    // Section titled: Line defined by two points
    public static double getShortestDistanceBetweenPointAndLine(Location lineA, Location lineB, Location point) {
        double x0 = point.getX();
        double y0 = point.getY();
        double x1 = lineA.getX();
        double y1 = lineA.getY();
        double x2 = lineB.getX();
        double y2 = lineB.getY();

        double numerator = Math.abs((x2 - x1) * (y1 - y0) - (x1 - x0) * (y2 - y1));
        double denominator = Math.hypot(x2 - x1, y2 - y1); // Use hypot as shorthand for square root of squares.

        return numerator / denominator;
    }
}
