package org.baylorschool.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.lift.Lift;

@TeleOp(name="MainTeleOP", group="Competition")
public class MainTeleOP extends LinearOpMode {

    private Mecanum mecanum;
    private Carousel carousel;
    private Lift lift;
    private Odometry odometry;

    private final double SLOW_MODE_COEFFICIENT = 0.5;
    private final double ROTATION_COEFFICIENT = 0.8;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        odometry = new Odometry(hardwareMap, true);
        mecanum = new Mecanum(hardwareMap);
        carousel = new Carousel(hardwareMap);
        lift = Globals.createNewLift(this);

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.initialize();

        boolean slowMode = false;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x * ROTATION_COEFFICIENT;

            if (gamepad2.x)
                carousel.dropDuck(Carousel.CarouselSide.BLUE, this);
            else if (gamepad2.b)
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
            lift.loopIterationTeleOp(gamepad2);

            // Report telemetry
            telemetry.addData("X Gamepad", x);
            telemetry.addData("Y Gamepad", y);
            telemetry.addData("Speed", y);
            telemetry.addData("Strafe", x);
            telemetry.addData("Rotation", rotation);
            telemetry.addData("EncoderFR", mecanum.getFrMotor().getCurrentPosition());
            telemetry.addData("EncoderBR", mecanum.getBrMotor().getCurrentPosition());
            telemetry.addData("EncoderFL", mecanum.getFlMotor().getCurrentPosition());
            telemetry.addData("EncoderBL", mecanum.getBlMotor().getCurrentPosition());
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
