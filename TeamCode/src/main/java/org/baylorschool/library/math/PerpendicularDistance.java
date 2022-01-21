package org.baylorschool.library.math;

import org.baylorschool.library.Location;

public class PerpendicularDistance {
    // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
    // Section titled: Line defined by two points
    /**
     * Computes the shortest distance between a point and an INFINITE LINE defined by two points.
     * @param lineA First point that defines line (x and y).
     * @param lineB Second point that defines line (x and y).
     * @param point Point of which distance is computed.
     * @return Distance between point and line.
     */
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

    // https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
    // Adapted quano's answer.
    /**
     * Computes the shortest distance between a point and an SEGMENT line defined by two points.
     * @param lineA First point that defines line (x and y).
     * @param lineB Second point that defines line (x and y).
     * @param point Point of which distance is computed.
     * @return Distance between point and segment.
     */
    public static double getShortestDistanceBetweenPointAndSegment(Location lineA, Location lineB, Location point) {
        double x1 = lineA.getX();
        double y1 = lineA.getY();
        double x2 = lineB.getX();
        double y2 = lineB.getY();
        double x3 = point.getX();
        double y3 = point.getY();

        double px=x2-x1;
        double py=y2-y1;
        double temp=(px*px)+(py*py);
        double u=((x3 - x1) * px + (y3 - y1) * py) / (temp);
        if (u>1) {
            u=1;
        } else if (u<0) {
            u=0;
        }

        double x = x1 + u * px;
        double y = y1 + u * py;

        double dx = x - x3;
        double dy = y - y3;
        return Math.sqrt(dx*dx + dy*dy);
    }
}
