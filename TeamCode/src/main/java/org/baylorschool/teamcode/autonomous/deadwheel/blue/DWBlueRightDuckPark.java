package org.baylorschool.teamcode.autonomous.deadwheel.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MoveSideways;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.TSEPipeline;
import org.baylorschool.library.debugging.DebuggingClient;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.localization.Odometry;
import org.baylorschool.library.localization.TouchSensors;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name = "DWBlueRightDuckPark", group = "Blue")
public class DWBlueRightDuckPark extends LinearOpMode {
    private Lift lift;
    private Mecanum mecanum;
    private IMU imu;
    private Carousel carousel;
    private Location currentLocation = new Location(Places.blueRightStart);
    private Globals.DropLevel dropLevel;
    private Odometry odometry;
    private ElapsedTime elapsedTime;

    private static final Location robotLocationDroppingDuck = new Location(Places.closePerpendicular(-3), 1416, -90);

    private static final Location[] hubToDuck = new Location[] {
            new Location(Places.middle(-1.5), Places.middle(2.5), -90),
            new Location(Places.middle(-2.5), Places.middle(2) + 40, -90)
    };

    private static final Location[] carouselToPark = new Location[] {
            new Location(Places.middle(-2), Places.middle(2), 0),
            new Location(Places.closeParallel(-3), Places.middle(1.5), 0),
    };

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();
        lift = Globals.createNewLift(this);
        mecanum = new Mecanum(hardwareMap);
        mecanum.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        imu = new IMU(hardwareMap);
        carousel = new Carousel(hardwareMap);
        odometry = new Odometry(hardwareMap, imu, false);
        elapsedTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

        lift.moveDown(this);

        telemetry.addData("Status", "Waiting for vision...");
        telemetry.update();

        waitForStart();
        elapsedTime.reset();
        DebuggingClient.getInstance().setRunning(true);

        telemetry.addData("Status", "Starting...");
        telemetry.update();

        dropLevel = Globals.DropLevel.MIDDLE;

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

        //currentLocation = MoveWaypoints.rotatePID(currentLocation, odometry, mecanum, 0, this);
        //lift.retract();

        // Move between the carousel and the storage unit.
        Path path = new Path(hubToDuck).setTolerance(new Location(100, 50)).setTimeout(4000).setRunnable(0, () -> lift.retract());
        currentLocation = MoveWaypoints.moveWaypoints(path, mecanum, odometry, currentLocation, this);

        // Ensure we are next to the wall.
        currentLocation = MoveSideways.moveSidewaysUntilTouch(
                TouchSensors.Direction.RIGHT,
                1000,
                odometry.getTouchSensors(),
                .4,
                currentLocation,
                mecanum,
                odometry,
                this
        );

        // Move next to the carousel.
        // We use moveSidewaysUntilTouch method to take advantage of its time limit and to avoid duplication.
        currentLocation = MoveSideways.moveSidewaysUntilTouch(
                TouchSensors.Direction.BACK,
                1000,
                odometry.getTouchSensors(),
                .3,
                currentLocation,
                mecanum,
                odometry,
                this
        );

        sleep(500);

        currentLocation = MoveSideways.moveSidewaysUntilTouch(
                TouchSensors.Direction.BACK,
                250,
                odometry.getTouchSensors(),
                .1,
                currentLocation,
                mecanum,
                odometry,
                this
        );

        Location newLocation = new Location(robotLocationDroppingDuck).setHeading(currentLocation.getHeading());
        currentLocation = new Location(newLocation);

        // Drop the duck.
        currentLocation = carousel.dropDuck(Carousel.CarouselSide.BLUE, currentLocation, this, odometry);

        // Move to park.
        currentLocation = MoveWaypoints.moveWaypoints(new Path(carouselToPark), mecanum, odometry, currentLocation, this);

        // Start withdrawing odometry. We do not need it anymore.
        odometry.withdraw();;
        //lift.closeThread();

        // Make sure we are properly parked. This is usually not a problem, only to be safe.
        currentLocation = MoveSideways.moveSidewaysUntilTouch(
                TouchSensors.Direction.BACK,
                1000,
                odometry.getTouchSensors(),
                .3,
                currentLocation,
                mecanum,
                odometry,
                this
        );

        // Stop movement.
        mecanum.stop();

        // Give the odometry time to withdraw.
        sleep(2000);
    }
}
