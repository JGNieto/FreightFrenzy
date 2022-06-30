package org.baylorschool;

import org.baylorschool.library.Location;

public class Places {

    static final double tileLength = 609.6;

    public static final Location redCarouselLocation = new Location(-1390.3,-1610.8,0);
    public static final Location blueCarouselLocation = new Location(awayPerpendicular(-3),402,-90); // FIXME MEASURE Y VALUE

    // Locations after moving away from carousel to clear it.
    // See DropDuck class
    public static final Location redCarouselLocationAway = new Location(-1390.3,-1610.8,0); // FIXME MEASURE
    public static final Location blueCarouselLocationAway = new Location(0,0,-90); // FIXME MEASURE

    public static final Location redLeftStart = new Location(middle(-1.5), closeParallel(-3), 90);
    public static final Location redMiddleStart = new Location(middle(-1.5), closeParallel(-3), 90);
    public static final Location redRightStart = new Location(middle(0.5), closeParallel(-3), 90);
    public static final Location blueLeftStart = new Location(middle(0.5), closeParallel(3), -90);
    public static final Location blueMiddleStart = new Location(middle(-1.5), closeParallel(3), -90);
    public static final Location blueRightStart = new Location(middle(-1.5), closeParallel(3), -90);


    public static final Location redTeamShippingHub = new Location(middle(-0.5), middle(-1));
    public static final Location blueTeamShippingHub = new Location(middle(-0.5), middle(1));
    public static final Location coopShippingHub = new Location(middle(-1), middle(-.53));


    public enum StartLocation {
        BLUE_LEFT,
        BLUE_RIGHT,
        RED_LEFT,
        RED_RIGHT,
        RED_MIDDLE,
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being close to the origin.
     * The wheels are perpendicular to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    public static double closePerpendicular(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) - Globals.robotWidth / 2);
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being away from the origin.
     * The wheels are perpendicular to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    public static double awayPerpendicular(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) + Globals.robotWidth / 2);
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being close to the origin.
     * The wheels are parallel to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    public static double closeParallel(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) - Globals.robotLength / 2);
    }

    /**
     * Position of robot in one axis, so that the robot touches it while being away from the origin.
     * The wheels are parallel to the axis.
     * @param tile number of tiles away from the center cross
     * @return
     */
    public static double awayParallel(double tile) {
        int sign = tile < 0 ? -1 : +1;
        return sign * (Math.abs(tile * tileLength) + Globals.robotLength / 2);
    }

    /**
     * Robots center is where a perpendicular line is.
     * @param tile number of tiles away from the center cross
     * @return
     */
    public static double middle(double tile) {
        return tile * tileLength;
    }

    public static final Location[] ParkRedLeftStorageUnit = new Location[] {
            redLeftStart,
            new Location(middle(-1.5), middle(-1.5)),
            new Location(closeParallel(-3), middle(-1.5)),
    };

    public static final Location[] ParkRedRightStorageUnit = new Location[] {
            redRightStart,
            new Location(middle(-0.5), middle(-2)),
            new Location(middle(-1.5), middle(-1.5)),
            new Location(closeParallel(-3), middle(-1.5)),
    };

    public static final Location[] ParkBlueLeftStorageUnit = new Location[] {
            blueLeftStart,
            new Location(middle(-0.5), middle(2)),
            new Location(middle(-1.5), middle(1.5)),
            new Location(closeParallel(-3), middle(1.5)),
    };

    public static final Location[] ParkBlueRightStorageUnit = new Location[] {
            blueRightStart,
            new Location(middle(-1.5), middle(1.5)),
            new Location(closeParallel(-3), middle(1.5)),
    };

    public static final Location[] BlueRightToCarousel = new Location[] {
            blueRightStart,
            new Location(middle(-1.5), middle(2)),
            new Location(middle(-2.5), middle(2), -90),
    };

    public static final Location[] BlueLeftToCarousel = new Location[] {
            blueLeftStart,
            new Location(middle(-0.5), middle(2)),
            new Location(middle(-2.5), middle(2), -90),
    };

    public static final Location[] RedRightToCarousel = new Location[] {
            redRightStart,
            new Location(middle(-0.5), middle(-2)),
            new Location(middle(-2), middle(-2.5), 0).backwards(),
    };

    public static final Location[] RedLeftToCarousel = new Location[] {
            redLeftStart,
            new Location(middle(-1.5), middle(-2)),
            new Location(middle(-2), middle(-2.5), 0).backwards(),
    };

    public static final Location[] CarouselToBluePark = new Location[] {
            new Location(awayPerpendicular(-3), middle(1.5)),
    };

    public static final Location[] CarouselToRedPark = new Location[] {
            new Location(awayPerpendicular(-3), middle(-1.5)),
    };

    public static final Location[] BlueLeftHubToWarehouse = new Location[] {
            new Location(middle(-0.5), closePerpendicular(3), 30),
    };

    public static final Location[] BlueLeftHubToWarehousePower = new Location[] {
            new Location(298.74, middle(2) + 250 - 360).backwards()
    };

    public static final Location[] RedRightHubToWarehousePower = new Location[] {
            new Location(298.74, -(middle(2) + 250 - 360)).backwards()
    };

    public static final Location[] BlueRightHubToWarehouse = new Location[] {
            new Location(middle( 0), closePerpendicular(3), 30),
    };

    public static final Location[] RedRightHubToWarehouse = new Location[] {
            new Location(middle( 0), closePerpendicular(-3), -30),
    };

    public static final Location[] RedLeftHubToWarehouse = new Location[] {
            new Location(middle( -1.5), closePerpendicular(-3), -30),
    };

    public static final Location[] BlueRightToHub = new Location[] {
            blueRightStart,
            new Location(middle(-1.5), middle(2.5)),
    };

    public static final Location[] BlueLeftToHub = new Location[] {
            blueLeftStart,
            new Location(middle(0.5), middle(2.5)),
    };

    public static final Location[] RedRightToHub = new Location[] {
            redRightStart,
            new Location(middle(0.5), middle(-2.5)),
    };

    public static final Location[] RedLeftToHub = new Location[] {
            redLeftStart,
            new Location(middle(-1.5), middle(-2.5)),
    };

    public static final Location[] BlueRightHubToPark = new Location[] {
            new Location(middle(-2), middle(1.5)),
            new Location(closeParallel(-3), middle(1.5)).backwards(),
    };

    public static final Location[] RedLeftHubToPark = new Location[] {
            new Location(middle(-2), middle(-1.5)),
            new Location(closeParallel(-3), middle(-1.5)).backwards(),
    };

    public static final Location[] BlueRightHubToCarousel = new Location[] {
            new Location(middle(-2.5), middle(1.5)).backwards(),
            new Location(middle(-2.5), middle(2.5), 90).backwards(),
    };

    public static final Location[] BlueLeftHubToCarousel = new Location[] {
            new Location(middle(0.5), middle(2)),
            new Location(middle(-2), middle(2.5), 0),
    };

    public static final Location[] RedRightHubToCarousel = new Location[] {
            new Location(middle(0.5), middle(-2.5), 0).backwards(),
            new Location(middle(-2), middle(-2.5), 0),
    };

    public static final Location[] RedLeftHubToCarousel = new Location[] {
            new Location(middle(-2), middle(-2.5), 0).backwards(),
    };
}
