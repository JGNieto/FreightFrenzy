package org.baylorschool.teamcode.autonomous.deadwheel.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.EnterWarehouse;
import org.baylorschool.actions.GrabFreightBlindlyCoopHub;
import org.baylorschool.actions.GrabFreightBlindlyCoopHub2;
import org.baylorschool.actions.MoveSideways;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.debugging.DebuggingClient;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.localization.Odometry;
import org.baylorschool.library.localization.TouchSensors;

@Autonomous(name = "DWRedLeftTSEDuckCoopCyclePark", group = "Red")
public class DWBlueLeftTSEDuckCoopCyclePark extends LinearOpMode {
    private Lift lift;
    private Mecanum mecanum;
    private IMU imu;
    private Carousel carousel;
    private Location currentLocation = new Location(Places.redLeftStart);
    private Globals.DropLevel dropLevel;
    private Odometry odometry;
    private ElapsedTime elapsedTime;

    private static final Location duckLocation = new Location(Places.middle(2.5), Places.awayPerpendicular(-2.2), 0);
    private static final Location[] carouselToCycling = new Location[] {
            new Location(Places.closeParallel(-2.9) + 100, Places.middle(1.8), 0),
            new Location(Places.closeParallel(-2.9), Places.middle(1.25), 90),
    };

    private static final Location[] intakingToHub = new Location[] {
            new Location(Places.closeParallel(-3) + 100, Places.middle(1.37), 0),
            new Location(Places.middle(-2), Places.middle(1.125), 90),
    };

    private int cycles;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        lift = Globals.createNewLift(this);
        mecanum = new Mecanum(hardwareMap);
        mecanum.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        imu = new IMU(hardwareMap);
        carousel = new Carousel(hardwareMap);
        odometry = new Odometry(hardwareMap, imu, false);
        elapsedTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        lift.moveDown(this);

        telemetry.addData("Status", "Waiting...");
        telemetry.update();

        waitForStart();
        elapsedTime.reset();
        DebuggingClient.getInstance().setRunning(true);

        telemetry.addData("Status", "Starting...");
        telemetry.update();

        dropLevel = Globals.DropLevel.COOP;

        lift.startThread();

        lift.moveToDropLevel(dropLevel);

        Location scoringLocation = lift.getScoringLocation(currentLocation, Lift.Hub.COOP, dropLevel);

        telemetry.addData("Status", "Moving");
        telemetry.update();
        currentLocation = MoveWaypoints.moveWaypoints(new Path(new Location[]{
                scoringLocation,
        }), mecanum, odometry, currentLocation, this);
        mecanum.setPower(0);

        telemetry.addData("Status", "Dropping");
        telemetry.update();

        lift.releaseItemLocalization(currentLocation, odometry);

        currentLocation = MoveWaypoints.moveWaypoints(new Path(Location.moveLocation(new Location(currentLocation), 0, -150).setPurePursuitTurnSpeed(0)).setTimeout(1000), mecanum, odometry, currentLocation, this);

        lift.retract();

        while (opModeIsActive()) {
            currentLocation = MoveWaypoints.moveWaypoints(new Path(carouselToCycling), mecanum, odometry, currentLocation, this);
            lift.retract();

            currentLocation = GrabFreightBlindlyCoopHub2.grabFreightBlindly(currentLocation, mecanum, lift, odometry, this, Globals.WarehouseSide.BLUE, 0, odometry.getColorSensors());
            lift.setRollerState(Lift.RollerState.STOP);
            lift.retract();

            telemetry.log().add(String.valueOf(Globals.matchLength - elapsedTime.time()));

            dropLevel = Globals.DropLevel.COOP;

            lift.startThread();
            lift.moveToDropLevel(dropLevel);

            currentLocation = MoveWaypoints.moveWaypoints(new Path(intakingToHub), mecanum, odometry, currentLocation, this);
            mecanum.stop();

            lift.releaseItemLocalization(currentLocation, odometry);

            telemetry.addData("Status", "Moving");
            telemetry.update();

            if (lift.getCapturedElements() == 1) {
                cycles += 1;
            }

            if (cycles >= 1) {
                break;
            }
        }

        // Move between the carousel and the storage unit.
        currentLocation = MoveWaypoints.moveWaypoints(new Path(duckLocation).setTolerance(new Location(100, 50)).setTimeout(1000), mecanum, odometry, currentLocation, this);

        // Ensure we are next to the wall.
        currentLocation = MoveSideways.moveSidewaysUntilTouch(
                TouchSensors.Direction.BACK,
                650,
                odometry.getTouchSensors(),
                1,
                currentLocation,
                mecanum,
                odometry,
                this
        );

        // Move next to the carousel.
        // We use moveSidewaysUntilTouch method to take advantage of its time limit and to avoid duplication.
        currentLocation = MoveSideways.moveSidewaysUntilTouch(
                TouchSensors.Direction.RIGHT,
                1200,
                odometry.getTouchSensors(),
                1,
                currentLocation,
                mecanum,
                odometry,
                this
        );

        currentLocation = MoveSideways.moveSidewaysUntilTouch(
                TouchSensors.Direction.BACK,
                200,
                odometry.getTouchSensors(),
                .5,
                currentLocation,
                mecanum,
                odometry,
                this
        );

        // Drop the duck.
        currentLocation = carousel.dropDuck(Carousel.CarouselSide.RED, currentLocation, this, odometry);

        // Start withdrawing odometry. We do not need it anymore.
        odometry.withdraw();
        lift.closeThread();

        // Stop movement.
        mecanum.stop();
        // Give the odometry time to withdraw.
        sleep(2000);
    }
}
