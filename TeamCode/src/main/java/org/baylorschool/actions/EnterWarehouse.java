package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Places;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;

import java.util.Arrays;

public class EnterWarehouse {
    public enum WarehouseSide {
        RED, BLUE
    }

    public static Location enterWarehouse(WarehouseSide side, Location currentLocation, Sensors sensors, LinearOpMode opMode) {
        sensors.getMecanum().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sensors.getMecanum().moveMecanum(0, (side == WarehouseSide.RED ? -1 : 1) * .5, 0);
        opMode.sleep(1500);
        double yValue = Places.awayPerpendicular(3 * (side == WarehouseSide.RED ? -1 : 1));
        currentLocation.setY(yValue);
        currentLocation.setHeading(0);
        sensors.getMecanum().resetEncoders();
        Location[] locations = new Location[] {
                new Location(Places.middle(2), yValue),
        };

        return MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(locations), opMode);
    }
}
