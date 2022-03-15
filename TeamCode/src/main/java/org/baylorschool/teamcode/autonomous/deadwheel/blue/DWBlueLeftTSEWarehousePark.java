package org.baylorschool.teamcode.autonomous.deadwheel.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.EnterWarehouse;
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

import java.util.ArrayList;

@Autonomous(name = "DWBlueLeftTSEWarehousePark", group = "Blue")
public class DWBlueLeftTSEWarehousePark extends LinearOpMode {

    private Lift lift;
    private Mecanum mecanum;
    private TSEPipeline tsePipeline;
    private OpenCvWebcam webcam;
    private IMU imu;
    private Location currentLocation = new Location(Places.blueLeftStart);
    private Globals.DropLevel dropLevel;
    private Odometry odometry;
    private ElapsedTime elapsedTime;

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

        Location scoringLocation = lift.getScoringLocation(currentLocation, Lift.Hub.BLUE, dropLevel);

        telemetry.addData("Status", "Moving");
        telemetry.update();
        currentLocation = MoveWaypoints.moveWaypoints(new Path(new Location[]{
                scoringLocation,
        }), mecanum, odometry, currentLocation, this);
        mecanum.stop();

        telemetry.addData("Status", "Dropping");
        telemetry.update();

        lift.releaseItemLocalization(currentLocation, odometry);

        currentLocation = MoveWaypoints.rotatePID(currentLocation, odometry, mecanum, 0, this);
        lift.retract();

        currentLocation = EnterWarehouse.enterWarehouseOdometryTouch(Globals.WarehouseSide.BLUE, currentLocation, mecanum, odometry, new ArrayList<>(), this, () -> lift.setRollerState(Lift.RollerState.GRABBING), odometry.getTouchSensors());

        // To make sure we are fully parked and have time to retract odometry, we set a limit on grabFreightBlindly.
        //double timeLeft = Globals.matchLength - elapsedTime.time();
        //timeLeft -= 1000; // Want to give one second for odometry retraction.
        //currentLocation = GrabFreightBlindly.grabFreightBlindly(currentLocation, mecanum, lift, odometry, this, Globals.WarehouseSide.BLUE, timeLeft);
        mecanum.stop();

        // We need to retract odometry ASAP, so that is the first thing we do.
        odometry.withdraw();

        // Close the thread: we do not need it anymore.
        lift.closeThread();

        // Sleep so that the opmode runs until it is forced to stop or 2 seconds. This is to raise the odometry.
        sleep(2000);
    }
}
