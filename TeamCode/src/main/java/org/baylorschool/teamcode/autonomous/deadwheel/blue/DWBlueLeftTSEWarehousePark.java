package org.baylorschool.teamcode.autonomous.deadwheel.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.lift.TwoBarLift;
import org.baylorschool.library.localization.Odometry;
import org.openftc.easyopencv.OpenCvWebcam;

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
        mecanum = new Mecanum(hardwareMap);
        imu = new IMU(hardwareMap);
        odometry = new Odometry(mecanum, hardwareMap, imu, false);

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        twoBarLift.moveDown(this);
        twoBarLift.setTelemetryEnabled(true);

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();

        telemetry.addData("Status", "Starting...");
        telemetry.update();

        dropLevel = tsePipeline.getDropLevel();

        // Remove if using vuforia:
        TSEPipeline.stop(webcam);
        twoBarLift.startThread();
        twoBarLift.moveToDropLevel(dropLevel);

        telemetry.addData("Status", "Waiting...");
        telemetry.update();

        sleep(1000); // Wait for lift to move.
        Location scoringLocation = twoBarLift.getScoringLocation(currentLocation, Lift.Hub.BLUE, dropLevel);

        telemetry.addData("Status", "Moving");
        telemetry.update();
        MoveWaypoints.moveWaypoints(new Path(new Location[]{
                new Location(159, 1158),
                scoringLocation,
        }), mecanum, odometry, currentLocation, this);
        twoBarLift.releaseItem();
        twoBarLift.closeThread();
    }
}
