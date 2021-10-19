package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.baylorschool.library.Mecanum;

@TeleOp(name="MecanumTestArcade", group="Test")
public class MecanumTestArcade extends LinearOpMode {

    private Mecanum mecanum;
    private final double SLOWMODE_COEFFICIENT = 0.5;
    private final double ROTATION_COEFFICIENT = 0.8;
    private final double FLYWHEEL_SPEED = 0.8; // Pneumatic wheel: 0.65

    private DcMotor flyWheel = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        mecanum = new Mecanum(hardwareMap, Mecanum.Side.RIGHT,
                "blMotor", "flMotor", "brMotor", "frMotor");


        flyWheel = hardwareMap.get(DcMotor.class, "flyWheel");
        waitForStart();

        boolean slowMode = false;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x * ROTATION_COEFFICIENT;
            double flyWheelPower = 0;

            if (gamepad1.a) flyWheelPower += FLYWHEEL_SPEED;
            if (gamepad1.b) flyWheelPower -= FLYWHEEL_SPEED;

            // Detect slow mode.
            slowMode = slowModeToggle(gamepad1, slowMode);

            if (slowMode)
                rotation *= SLOWMODE_COEFFICIENT;

            // If driver wants full power, give it to them.
            if (gamepad1.right_stick_button)
                rotation = gamepad1.right_stick_x;

            // Execute movement
            mecanum.moveGamepad(y, x, rotation, slowMode ? SLOWMODE_COEFFICIENT : 1);
            flyWheel.setPower(flyWheelPower);

            // Report telemetry
            telemetry.addData("Speed", y);
            telemetry.addData("Strafe", x);
            telemetry.addData("Rotation", rotation);
            telemetry.addData("FlyWheel", flyWheelPower);
            telemetry.update();
        }
    }

    private boolean slowModeToggle(Gamepad gamepad, boolean current) {
        // If the users presses one of the buttons, set slow mode to that value, otherwise keep as is.
        if (gamepad.left_bumper) {
            return true;
        }

        if (gamepad.right_bumper) {
            return false;
        }

        return current;
    }

    private boolean slowModeHold(Gamepad gamepad) {
        return gamepad.left_bumper;
    }
}