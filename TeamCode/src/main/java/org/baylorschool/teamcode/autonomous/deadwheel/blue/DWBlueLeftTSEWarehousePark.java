package org.baylorschool.teamcode.autonomous.deadwheel.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.EnterWarehouse;
import org.baylorschool.actions.MovePurePursuit;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Path;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.lift.TwoBarLift;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.Arrays;
import java.util.Collections;

@Autonomous(name = "DWBlueLeftTSEWarehousePark", group = "Blue")
public class DWBlueLeftTSEWarehousePark extends LinearOpMode {

    private TwoBarLift twoBarLift;
    private Mecanum mecanum;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private IMU imu;
    private Location currentLocation = new Location(Places.blueLeftStart);
    private Globals.DropLevel dropLevel;
    private Odometry odometry;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        twoBarLift = new TwoBarLift(this);
        odometry = new Odometry(mecanum, hardwareMap, imu, false);

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        twoBarLift.moveDown(this);

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();

        dropLevel = tsePipeline.getDropLevel();

        // Remove if using vuforia:
        TSEPipeline.stop(webcam);

        twoBarLift.initialize();
        twoBarLift.moveToDropLevel(dropLevel);
        twoBarLift.startThread();
        sleep(200); // Wait for lift to move.
        Location scoringLocation = twoBarLift.getScoringLocation(currentLocation, Lift.Hub.BLUE, dropLevel);
        MovePurePursuit.movePurePursuit(currentLocation, new Path(scoringLocation), this, odometry, mecanum);
        twoBarLift.releaseItem();
        twoBarLift.closeThread();
    }
}
