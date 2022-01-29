package org.baylorschool.teamcode.autonomous.encoders.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.EnterWarehouse;
import org.baylorschool.actions.MoveWaypointsEncoders;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.TwoBarLift;
import org.baylorschool.library.localization.Odometry;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "RedRightTSEWarehousePark", group = "Red")
public class RedRightTSEWarehousePark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private Location currentLocation = new Location(Places.redRightStart);
    private Globals.DropLevel dropLevel;
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

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();

        dropLevel = tsePipeline.getDropLevel();

        // Remove if using vuforia:
        TSEPipeline.stop(webcam);

        twoBarLift.initialize();
        twoBarLift.startThread();

        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedRightToHub), this);
        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.RED, dropLevel)), this);
        twoBarLift.releaseItem();
        twoBarLift.retract(1500);
        currentLocation = MoveWaypointsEncoders.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedRightHubToWarehousePower), this);
        EnterWarehouse.parkWarehouse(currentLocation, sensors, this);
        twoBarLift.closeThread();
    }
}
