package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.actions.MoveWaypointsEncoders;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;

import java.util.Arrays;

@Disabled
@Autonomous(name="Park Storage Unit BR", group ="Blue")
public class ParkBlueRightStorageUnit extends LinearOpMode {

    private Location currentLocation = new Location(-609.6, 1608, -90);
    private Sensors sensors;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Getting ready");
        telemetry.update();

        sensors = new Sensors(hardwareMap, false);
        sensors.initialize(hardwareMap, currentLocation.getHeading());

        Location[] locations = new Location[] {
                new Location(-609.6, 1219.2),
                new Location(-914.4, 914.4),
                new Location(-1564, 914.4, 180)
        };

        telemetry.addData("Status", "Ready!");
        telemetry.update();

        waitForStart();
        sensors.getMecanum().resetEncoders();

        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors.getVuforia(), sensors.getImu(), Arrays.asList(locations), telemetry, sensors.getMecanum(), this);

        sensors.end();
    }
}
