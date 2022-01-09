package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.lift.Lift;

@TeleOp(name="OdometryTest", group="Test")
public class OdometryTest extends LinearOpMode {

    private Mecanum mecanum;
    private Carousel carousel;
    private Lift lift;
    private Odometry odometry;

    private final double SLOW_MODE_COEFFICIENT = 0.5;
    private final double ROTATION_COEFFICIENT = 0.8;

    private Location currentLocation = new Location(0, 0, 0);

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        mecanum = new Mecanum(hardwareMap);
        odometry = new Odometry(mecanum.getFlMotor(), mecanum.getFrMotor(), mecanum.getBlMotor(),
                hardwareMap.get(Servo.class, Globals.servoLeftHw),
                hardwareMap.get(Servo.class, Globals.servoRightHw),
                hardwareMap.get(Servo.class, Globals.servoMiddleHw),
                false
        );
        carousel = new Carousel(hardwareMap);
        lift = Globals.createNewLift(this);

        waitForStart();
        odometry.reset();
        odometry.calculateNewLocation(currentLocation);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.initialize();

        boolean slowMode = false;

        while (opModeIsActive()) {
            currentLocation = odometry.calculateNewLocation(currentLocation);
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x * ROTATION_COEFFICIENT;

            if (gamepad1.x)
                carousel.dropDuck(Carousel.CarouselSide.BLUE, this);
            else if (gamepad1.b)
                carousel.dropDuck(Carousel.CarouselSide.RED, this);
            else
                carousel.stop();

            // Detect slow mode.
            slowMode = slowModeToggle(gamepad1, slowMode);

            // If driver wants full power, give it to them.
            if (gamepad1.right_stick_button)
                rotation = gamepad1.right_stick_x;


            // Execute movement
            mecanum.moveGamepad(y, x, rotation, slowMode ? SLOW_MODE_COEFFICIENT : 1);
            lift.loopIterationTeleOp();

            // Report telemetry
            telemetry.addData("X Gamepad", x);
            telemetry.addData("Y Gamepad", y);
            telemetry.addData("Speed", y);
            telemetry.addData("Strafe", x);
            telemetry.addData("Rotation", rotation);
            telemetry.addData("Odo Left", odometry.getPreviousLeft());
            telemetry.addData("Odo Right", odometry.getPreviousRight());
            telemetry.addData("Odo Mid", odometry.getPreviousMid());
            telemetry.addData("Loc X", currentLocation.getX());
            telemetry.addData("Loc Y", currentLocation.getY());
            telemetry.addData("Loc Head", currentLocation.getHeading());
            telemetry.update();
        }
    }

    private boolean slowModeToggle(Gamepad gamepad, boolean current) {
        // If the users presses one of the buttons, set slow mode to that value, otherwise keep as is.
        if (gamepad.a) {
            return true;
        }

        if (gamepad.y) {
            return false;
        }

        return current;
    }

    private boolean slowModeHold(Gamepad gamepad) {
        return gamepad.left_bumper;
    }
}
