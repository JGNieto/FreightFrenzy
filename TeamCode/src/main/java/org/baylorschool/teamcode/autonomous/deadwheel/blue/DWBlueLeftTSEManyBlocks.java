package org.baylorschool.teamcode.autonomous.deadwheel.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.EnterWarehouse;
import org.baylorschool.actions.GrabFreightBlindly;
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

import java.util.ArrayList;

@Autonomous(name = "DWBlueLeftTSEWarehouseManyBlocks", group = "Blue")
public class DWBlueLeftTSEManyBlocks extends LinearOpMode {

    private TwoBarLift twoBarLift;
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
        twoBarLift = new TwoBarLift(this);
        mecanum = new Mecanum(hardwareMap);
        mecanum.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        imu = new IMU(hardwareMap);
        odometry = new Odometry(hardwareMap, imu, false);
        elapsedTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        tsePipeline = new TSEPipeline(this);
        webcam = TSEPipeline.openWebcam(this, tsePipeline);
        twoBarLift.moveDown(this);

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();
        elapsedTime.startTime();

        telemetry.addData("Status", "Starting...");
        telemetry.update();

        dropLevel = tsePipeline.getDropLevel();

        // Remove if using vuforia:
        TSEPipeline.stop(webcam);
        twoBarLift.startThread();

        // Move to and from the warehouse.
        while (opModeIsActive()) {
            twoBarLift.moveToDropLevel(dropLevel);
            Location scoringLocation = twoBarLift.getScoringLocation(currentLocation, Lift.Hub.BLUE, dropLevel);

            telemetry.addData("Status", "Moving");
            telemetry.update();

            currentLocation = MoveWaypoints.moveWaypoints(new Path(new Location[]{
                    scoringLocation,
            }), mecanum, odometry, currentLocation, this);
            mecanum.stop();

            telemetry.addData("Status", "Dropping");
            telemetry.update();

            long startDropTime = System.currentTimeMillis();
            twoBarLift.setRollerState(Lift.RollerState.RELEASING);

            while (opModeIsActive()) {
                currentLocation = odometry.calculateNewLocation(currentLocation);
                if (System.currentTimeMillis() - startDropTime >= 2000) break;
            }

            twoBarLift.setRollerState(Lift.RollerState.STOP);

            currentLocation = MoveWaypoints.rotatePID(currentLocation, odometry, mecanum, 0, this);
            twoBarLift.retract();

            currentLocation = EnterWarehouse.enterWarehouseOdometryTouch(Globals.WarehouseSide.BLUE, currentLocation, mecanum, odometry, new ArrayList<>(), this, null/*() -> twoBarLift.retract()*/, odometry.getTouchSensors());

            currentLocation = GrabFreightBlindly.grabFreightBlindly(currentLocation, mecanum, twoBarLift, odometry, this, Globals.WarehouseSide.BLUE);

            telemetry.log().add(String.valueOf(Globals.matchLength - elapsedTime.time()));

            if (Globals.matchLength - elapsedTime.time() < Globals.minTimeExitWarehouse)
                break;

            currentLocation = EnterWarehouse.exitWarehouse(Globals.WarehouseSide.BLUE, currentLocation, mecanum, odometry, this, odometry.getTouchSensors(), null, null);

            dropLevel = Globals.DropLevel.TOP;
        }

        telemetry.update();

        odometry.withdraw();
        twoBarLift.closeThread();
    }
}
