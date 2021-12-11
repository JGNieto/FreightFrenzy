package org.baylorschool.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.EnterWarehouse;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Location;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.TwoBarLift;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "BlueLeftTSEWarehousePark", group = "BlueRight")
public class BlueLeftTSEWarehousePark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Sensors sensors;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private Location currentLocation = Places.blueRightStart;
    private Globals.DropLevel dropLevel;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        twoBarLift = new TwoBarLift(this);
        sensors = new Sensors(hardwareMap, false);
        sensors.initialize(hardwareMap, currentLocation.getHeading());

        tsePipeline = new TSEPipeline(Places.StartLocation.BLUE_RIGHT, this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();

        dropLevel = tsePipeline.getDropLevel();
        webcam.stopStreaming();
        webcam.closeCameraDevice();

        twoBarLift.startThread();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueLeftToHub), this);
        twoBarLift.moveToDropLevel(dropLevel);
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(twoBarLift.getScoringLocation(currentLocation, TwoBarLift.Hub.BLUE, dropLevel)), this);
        twoBarLift.releaseItem();
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(Location.moveLocation(currentLocation, 0, -100).backwards()), this);
        twoBarLift.retract();
        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(Places.BlueLeftHubToWarehouse), this);
        currentLocation = EnterWarehouse.enterWarehouse(EnterWarehouse.WarehouseSide.BLUE, currentLocation, sensors, this);
        twoBarLift.closeThread();
    }
}
