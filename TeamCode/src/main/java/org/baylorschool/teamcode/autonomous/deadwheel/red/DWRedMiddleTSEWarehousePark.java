package org.baylorschool.teamcode.autonomous.deadwheel.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.debugging.DebuggingClient;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.localization.Odometry;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "DWRedMiddleTSEWarehousePark", group = "Red")
public class DWRedMiddleTSEWarehousePark extends LinearOpMode {
    private Lift lift;
    private Mecanum mecanum;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private IMU imu;
    private Location currentLocation = new Location(Places.redMiddleStart);
    private Globals.DropLevel dropLevel;
    private Odometry odometry;
    private ElapsedTime elapsedTime;

    private static final Location[] goingToParkStage1 = new Location[] {
            new Location(Places.middle(-.8), Places.closeParallel(-2.3), 0),
            new Location(Places.awayParallel(.1), Places.middle(-1.55), 0)
    };

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        lift = Globals.createNewLift(this);
        mecanum = new Mecanum(hardwareMap);
        mecanum.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        imu = new IMU(hardwareMap);
        odometry = new Odometry(hardwareMap, imu, false);
        elapsedTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        lift.moveDown(this);


        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();
        elapsedTime.reset();
        DebuggingClient.getInstance().setRunning(true);

        telemetry.addData("Status", "Starting...");
        telemetry.update();

        dropLevel = tsePipeline.getDropLevel();

        // Remove if using vuforia:
        TSEPipeline.stop(webcam);
        lift.startThread();
        lift.moveToDropLevel(dropLevel);

        telemetry.addData("Status", "Waiting...");
        telemetry.update();

        Location scoringLocation = lift.getScoringLocation(currentLocation, Lift.Hub.RED, dropLevel);

       telemetry.addData("Status", "Moving");
        telemetry.update();
        currentLocation = MoveWaypoints.moveWaypoints(new Path(new Location[]{
                scoringLocation,
        }), mecanum, odometry, currentLocation, this);
        mecanum.stop();

        telemetry.addData("Status", "Dropping");
        telemetry.update();

        lift.releaseItemLocalization(currentLocation, odometry);

        currentLocation = MoveWaypoints.moveWaypoints(new Path(Location.moveLocation(new Location(currentLocation), 0, -100).setPurePursuitTurnSpeed(0)).setTimeout(1000), mecanum, odometry, currentLocation, this);

        currentLocation = MoveWaypoints.rotatePID(currentLocation, odometry, mecanum, 0, this);
        lift.retract();

        // Move between the carousel and the storage unit.
        currentLocation = MoveWaypoints.moveWaypoints(new Path(goingToParkStage1).setTolerance(new Location(100, 50)).setTimeout(1000), mecanum, odometry, currentLocation, this);

        currentLocation = MoveWaypoints.rotatePID(currentLocation, odometry, mecanum, 0, this);
        odometry.withdraw();
        lift.closeThread();

        sleep(5000);

        mecanum.setPower(-1);

        sleep(1300);

        // Stop movement.
        mecanum.stop();

        // Give the odometry time to withdraw.
        sleep(2000);
    }
}
