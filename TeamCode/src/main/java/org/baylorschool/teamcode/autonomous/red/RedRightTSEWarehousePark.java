package org.baylorschool.teamcode.autonomous.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.EnterWarehouse;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Location;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.TwoBarLift;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "RedRightTSEWarehousePark", group = "Red")
public class RedRightTSEWarehousePark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private Location currentLocation = Places.redRightStart;
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

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        twoBarLift.initializeSync(this);

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();

        dropLevel = tsePipeline.getDropLevel();

        // Remove if using vuforia:
        TSEPipeline.stop(webcam);

        //twoBarLift.startThread();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedRightToHub), this);
        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.RED, dropLevel)), this);
        twoBarLift.releaseItem();
        twoBarLift.retract(1500);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.RedRightHubToWarehousePower), this);
        EnterWarehouse.parkWarehouse(currentLocation, sensors, this);
        twoBarLift.closeThread();
    }
}
