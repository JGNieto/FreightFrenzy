package org.baylorschool;

import org.baylorschool.library.Location;
import org.baylorschool.library.Path;

public class Places {
    // MILLIMETERS
    static final double robotLength = 455;
    static final double robotWidth = 430;

    static final double tileLength = 609.6;

    public static final Location redCarouselLocation = new Location(-1390.3,-1610.8,0);
    public static final Location blueCarouselLocation = new Location(0,0,-90); // FIXME MEASURE

    public static final Location redLeftStart = new Location(middle(-1), closeParallel(-3), 90);
    public static final Location redRightStart = new Location(middle(0), closeParallel(-3), 90);
    public static final Location bLueLeftStart = new Location(middle(-1), closeParallel(3), -90);
    public static final Location blueRightStart = new Location(middle(0), closeParallel(3), -90);


    /**
     * Position of robot in one axis, so that the robot touches it while being close to the origin.
     * The wheels are perpendicular to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double closePerpendicular(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) - robotWidth / 2);
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being away from the origin.
     * The wheels are perpendicular to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double awayPerpendicular(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) + robotWidth / 2);
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being close to the origin.
     * The wheels are parallel to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double closeParallel(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) - robotLength / 2);
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being away from the origin.
     * The wheels are parallel to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double awayParallel(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) + robotLength / 2);
    }

    /**
     * Robots center is where a perpendicular line is.
     * @param tile number of tiles away from the center cross
     * @return
     */
    static double middle(double tile) {
        return tile * tileLength;
    }

    public static final Location[] ParkRedLeftStorageUnit = new Location[] {
            redLeftStart,
            new Location(middle(-1), middle(-2)),
            new Location(middle(-1.5), middle(-1.5)),
            new Location(closeParallel(-3), middle(-1.5)),
    };

    public static final Location[] ParkRedRightStorageUnit = new Location[] {
            redRightStart,
            new Location(middle(0), middle(-2)),
            new Location(middle(-1.5), middle(-1.5)),
            new Location(closeParallel(-3), middle(-1.5)),
    };

    public static final Location[] ParkBlueLeftStorageUnit = new Location[] {
            bLueLeftStart,
            new Location(middle(-1), middle(2)),
            new Location(middle(-1.5), middle(1.5)),
            new Location(closeParallel(-3), middle(1.5)),
    };

    public static final Location[] ParkBlueRightStorageUnit = new Location[] {
            blueRightStart,
            new Location(middle(0), middle(2)),
            new Location(middle(-1.5), middle(1.5)),
            new Location(closeParallel(-3), middle(1.5)),
    };

    public static final Location[] BlueRightToCarousel = new Location[] {
            blueRightStart,
            new Location(middle(0), middle(2)),
            new Location(middle(-2.5), middle(2), -90),
    };

    public static final Location[] BlueLeftToCarousel = new Location[] {
            bLueLeftStart,
            new Location(middle(-1), middle(2)),
            new Location(middle(-2.5), middle(2), -90),
    };

    public static final Location[] RedRightToCarousel = new Location[] {
            redRightStart,
            new Location(middle(0), middle(-2)),
            new Location(middle(-2), middle(-2.5), 0).backwards(),
    };

    public static final Location[] RedLeftToCarousel = new Location[] {
            redLeftStart,
            new Location(middle(-1), middle(-2)),
            new Location(middle(-2), middle(-2.5), 0).backwards(),
    };

    public static final Location[] CarouselToBluePark = new Location[] {
            new Location(closePerpendicular(-3), middle(1.5), -90),
    };

    public static final Location[] CarouselToRedPark = new Location[] {
            /**
             * The robot's length is close to the wall which make it unable to go anywhere.
            */
    };
}