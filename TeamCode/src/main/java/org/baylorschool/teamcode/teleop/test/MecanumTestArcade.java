package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.baylorschool.library.Carousel;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.TwoBarLift;

@TeleOp(name="MecanumTestArcade", group="Test")
public class MecanumTestArcade extends LinearOpMode {

    private Mecanum mecanum;
    private Carousel carousel;
    private TwoBarLift lift;

    private final double SLOWMODE_COEFFICIENT = 0.5;
    private final double ROTATION_COEFFICIENT = 0.8;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        mecanum = new Mecanum(hardwareMap);
        carousel = new Carousel(hardwareMap);
        lift = new TwoBarLift(this);

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.initialize();

        boolean slowMode = false;

        while (opModeIsActive()) {
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
            mecanum.moveGamepad(y, x, rotation, slowMode ? SLOWMODE_COEFFICIENT : 1);
            lift.loopIterationTeleOp();

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
