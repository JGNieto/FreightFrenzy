package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Places;
import org.baylorschool.actions.MoveWaypointsEncoders;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;

import java.util.Arrays;

@Disabled
@Autonomous(name="Path Test", group ="Test")
public class PathTest extends LinearOpMode {

    private Location currentLocation;
    private Sensors sensors;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Getting ready");
        telemetry.update();

        Location[] locations = Places.ParkRedRightStorageUnit;
        currentLocation = locations[0];

        sensors = new Sensors(hardwareMap, false);
        sensors.initialize(hardwareMap, currentLocation.getHeading());

        telemetry.addData("Status", "Ready!");
        telemetry.update();

        waitForStart();
        sensors.getMecanum().resetEncoders();

        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors.getVuforia(), sensors.getImu(), Arrays.asList(locations), telemetry, sensors.getMecanum(), this);

        sensors.end();
    }
}
