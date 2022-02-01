package org.baylorschool.library.math;

import org.baylorschool.library.Location;

public class SegmentMidpoint {

    public static Location segmentMidpoint(Location location1, Location location2) {
        double x = (location1.getX() + location2.getX()) / 2;
        double y = (location1.getY() + location2.getY()) / 2;

        return new Location(x, y);
    }

}
