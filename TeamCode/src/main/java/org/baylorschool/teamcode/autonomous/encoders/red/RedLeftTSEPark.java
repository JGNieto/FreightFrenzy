package org.baylorschool.teamcode.autonomous.encoders.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MoveWaypointsEncoders;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.TwoBarLift;
import org.baylorschool.library.localization.Odometry;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "RedLeftTSEPark", group = "Red")
public class RedLeftTSEPark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private Location currentLocation = new Location(Places.redLeftStart);
    private Globals.DropLevel dropLevel;
    private Carousel carousel;
    private Odometry odometry;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        odometry = new Odometry(hardwareMap, true);
        twoBarLift = new TwoBarLift(this);
        sensors = new Sensors(hardwareMap, false);
        sensors.initialize(hardwareMap, currentLocation.getHeading());

        twoBarLift.moveDown(this);

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();
        sensors.getMecanum().updateEncoderReadings();

        dropLevel = Globals.DropLevel.MIDDLE;
        twoBarLift.initialize();
        twoBarLift.startThread();

        // Remove if using vuforia:

        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedLeftToHub), this);
        twoBarLift.releaseItem();
        twoBarLift.retract(1500);
        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedLeftHubToPark), this);
        twoBarLift.closeThread();
    }
}
