package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Places;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;

import java.util.Arrays;

public class EnterWarehouse {
    public enum WarehouseSide {
        RED, BLUE
    }

    public static Location enterWarehouse(WarehouseSide side, Location currentLocation, Mecanum mecanum, IMU imu, LinearOpMode opMode) {
        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        mecanum.moveMecanum(0, (side == WarehouseSide.RED ? -1 : 1) * .5, 0);
        opMode.sleep(1500);
        double yValue = Places.awayPerpendicular(3 * (side == WarehouseSide.RED ? -1 : 1));
        currentLocation.setY(yValue);
        currentLocation.setHeading(0);
        mecanum.resetEncoders();
        Location[] locations = new Location[] {
                new Location(Places.middle(2), yValue),
        };

        return MoveWaypoints.moveToWaypoints(currentLocation, null, imu, Arrays.asList(locations), opMode.telemetry, mecanum, opMode);
    }
}
