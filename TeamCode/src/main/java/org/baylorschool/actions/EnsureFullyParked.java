package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.math.AreasIntersect;

public class EnsureFullyParked {

    private static final Location parkingTargetBlue = new Location(Places.middle(2), Places.middle(2));
    private static final Location parkingTargetRed = new Location(Places.middle(2), Places.middle(-2));

    public static Location ensureFullyParked(Location currentLocation, Globals.WarehouseSide side, Mecanum mecanum, LinearOpMode opMode) {
        /*
        When we reach this point we have either grabbed freight and probably have lots of time left or
        have not managed to do it and have about one second left. In any case, we have two objectives:
         - Make sure that we are FULLY parked
         - Make sure to retract the odometry wheels (this has already been done in the OpMode code).
        Losing odometry means we lose localization, which makes the 1st goal a little more difficult.
        We use the final location we know of to see whether we have cleared the warehouse white tape with some margin.
        Also, we know we are inside the warehouse.
        */

        // Check whether we have already parked.
        if (AreasIntersect.isFullyParkedWarehouse(currentLocation, side))
            return currentLocation;

        Location target = side == Globals.WarehouseSide.RED ? parkingTargetRed : parkingTargetBlue;

        // Since we are not fully parked
        MovePurePursuit.moveTowardPositionAngle(mecanum, currentLocation, target, -1, 0);

        opMode.sleep(1000);
        mecanum.stop();

        return currentLocation;
    }
}
