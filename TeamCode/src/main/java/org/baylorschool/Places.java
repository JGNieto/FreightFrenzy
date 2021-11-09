package org.baylorschool;

import org.baylorschool.library.Location;
import org.baylorschool.library.Path;

public class Places {
    // MILLIMETERS
    static final double robotLength = 455;
    static final double robotWidth = 430;

    static final double tileLength = 609.6;

    /**
     * Position of robot in one axis, so that the robot touches it while being close to the origin.
     * The wheels are perpendicular to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double closePerpendicular(double tile) {
        return 0;
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being away from the origin.
     * The wheels are perpendicular to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double awayPerpendicular(double tile) {
        return 0;
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being close to the origin.
     * The wheels are parallel to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double closeParallel(double tile) {
        int sign = tile < 0 ? -1 : 1;
        return sign * (Math.abs(tile * tileLength) - robotLength / 2);
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being away from the origin.
     * The wheels are parallel to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double awayParallel(double tile) {
        return 0;
    }

    /**
     * Robots center is where a perpendicular line is.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double middle(double tile) {
        return tile * tileLength;
    }

    public static Location[] ParkRedLeftStorageUnit = new Location[] {
            new Location(middle(-1), closeParallel(-3), 90),
            new Location(middle(-1), middle(-2)),
            new Location(middle(-1.5), middle(-1.5)),
            new Location(closeParallel(-3), middle(-1.5)),
    };

    public static Location[] ParkRedRightStorageUnit = new Location[] {
            new Location(middle(0), closeParallel(-3), 90),
            new Location(middle(0), middle(-2)),
            new Location(middle(-1.5), middle(-1.5)),
            new Location(closeParallel(-3), middle(-1.5)),
    };

    public static Location[] ParkBlueLeftStorageUnit = new Location[] {
            new Location(middle(-1), closeParallel(3), -90),
            new Location(middle(-1), middle(2)),
            new Location(middle(-1.5), middle(1.5)),
            new Location(closeParallel(-3), middle(1.5)),
    };

    public static Location[] ParkBlueRightStorageUnit = new Location[] {
            new Location(middle(0), closeParallel(3), -90),
            new Location(middle(0), middle(2)),
            new Location(middle(-1.5), middle(1.5)),
            new Location(closeParallel(-3), middle(1.5)),
    };


}
