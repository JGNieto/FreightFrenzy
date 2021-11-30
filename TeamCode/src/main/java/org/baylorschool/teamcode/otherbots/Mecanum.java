package org.baylorschool.teamcode.otherbots;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp(name="Mecanum", group="Test")
public class Mecanum extends LinearOpMode {

    private org.baylorschool.library.Mecanum mecanum;
    private final double ROTATION_COEFFICIENT = 0.8;
    private final double SLOWMODE_COEFFICIENT = 0.5;

    private DcMotor blMotor = null;
    private DcMotor flMotor = null;
    private DcMotor brMotor = null;
    private DcMotor frMotor = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        mecanum = new org.baylorschool.library.Mecanum(hardwareMap);

        blMotor = hardwareMap.get(DcMotor.class, "blMotor");
        flMotor = hardwareMap.get(DcMotor.class, "flMotor");
        brMotor = hardwareMap.get(DcMotor.class, "brMotor");
        frMotor = hardwareMap.get(DcMotor.class, "frMotor");

        brMotor.setDirection(DcMotor.Direction.REVERSE);
        frMotor.setDirection(DcMotor.Direction.REVERSE);
        blMotor.setDirection(DcMotor.Direction.FORWARD);
        flMotor.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        boolean slowMode = false;

        final double slowModeCoefficient = 0.6;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x * ROTATION_COEFFICIENT;

            double leftPower = gamepad1.left_stick_y;
            double rightPower = gamepad1.right_stick_y;

            if (gamepad1.left_bumper) {
                leftPower *= slowModeCoefficient;
                rightPower *= slowModeCoefficient;
            }

            if (gamepad1.left_bumper) {
                leftPower *= slowModeCoefficient;
                rightPower *= slowModeCoefficient;
            }

            blMotor.setPower(leftPower);
            flMotor.setPower(leftPower);
            brMotor.setPower(rightPower);
            frMotor.setPower(rightPower);

            slowMode = slowModeToggle(gamepad1, slowMode);

            if (gamepad1.right_stick_button)
                rotation = gamepad1.right_stick_x;

            mecanum.moveGamepad(y, x, rotation, slowMode ? SLOWMODE_COEFFICIENT : 1);
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
}