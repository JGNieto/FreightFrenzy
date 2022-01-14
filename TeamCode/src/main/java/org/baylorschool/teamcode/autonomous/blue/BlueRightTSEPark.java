package org.baylorschool.teamcode.autonomous.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.TwoBarLift;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "BlueRightTSEPark", group = "Blue")
public class BlueRightTSEPark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private Location currentLocation = new Location(Places.blueRightStart);
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

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        twoBarLift.moveDown();

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();
        sensors.getMecanum().updateEncoderReadings();
        dropLevel = tsePipeline.getDropLevel();

        twoBarLift.initialize();
        twoBarLift.startThread();

        // Remove if using vuforia:
        TSEPipeline.stop(webcam);

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightToHub), this);
        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.BLUE, dropLevel)), this);
        twoBarLift.releaseItem();
        twoBarLift.retract(1500);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueRightHubToPark), this);
        // currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(Places.BlueRightHubToPark[0].forward()), this);
        // currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(Places.BlueRightHubToPark[1].forward()), this);
        twoBarLift.closeThread();
    }
}
