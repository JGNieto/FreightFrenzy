package org.baylorschool.teamcode.otherbots;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.baylorschool.Globals;

@TeleOp(name="CascadingLift", group="Test")
public class CascadingLift extends LinearOpMode {

    private org.baylorschool.library.Mecanum mecanum;
    private final double ROTATION_COEFFICIENT = 1;
    private final double SLOWMODE_COEFFICIENT = 0.4;
    private final double liftPower = 0.7;
    private static final double rollerGrabPower = -1;
    private static final double rollerReleasePower = 0.5;

    private DcMotor blMotor = null;
    private DcMotor flMotor = null;
    private DcMotor brMotor = null;
    private DcMotor frMotor = null;
    private DcMotor leftCascade = null;
    private DcMotor rightCascade = null;
    private DcMotor roller = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        mecanum = new org.baylorschool.library.Mecanum(hardwareMap);

        blMotor = hardwareMap.get(DcMotor.class, "blMotor");
        flMotor = hardwareMap.get(DcMotor.class, "flMotor");
        brMotor = hardwareMap.get(DcMotor.class, "brMotor");
        frMotor = hardwareMap.get(DcMotor.class, "frMotor");
        roller =  hardwareMap.get(DcMotor.class, Globals.rollerHw);
        leftCascade = hardwareMap.get(DcMotor.class, "leftCascade");
        rightCascade = hardwareMap.get(DcMotor.class, "rightCascade");

        brMotor.setDirection(DcMotor.Direction.REVERSE);
        frMotor.setDirection(DcMotor.Direction.REVERSE);
        rightCascade.setDirection(DcMotor.Direction.REVERSE);
        blMotor.setDirection(DcMotor.Direction.FORWARD);
        flMotor.setDirection(DcMotor.Direction.FORWARD);
        leftCascade.setDirection(DcMotor.Direction.FORWARD);

        leftCascade.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightCascade.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        roller.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftCascade.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightCascade.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        boolean slowMode = false;

        final double slowModeCoefficient = 0.4;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x * ROTATION_COEFFICIENT;

            double leftPower = gamepad1.left_stick_y;
            double rightPower = gamepad1.right_stick_y;
            double motorPower = 0;

            if (gamepad1.left_bumper) {
                leftPower *= slowModeCoefficient;
                rightPower *= slowModeCoefficient;
            }

            if (gamepad1.dpad_left) {
                roller.setPower(rollerGrabPower);
            } else if (gamepad1.dpad_right) {
                roller.setPower(rollerReleasePower);
            } else {
                roller.setPower(0);
            }

            if (gamepad1.y)
                motorPower += liftPower;

            if (gamepad1.a)
                motorPower -= liftPower;

            blMotor.setPower(leftPower);
            flMotor.setPower(leftPower);
            brMotor.setPower(rightPower);
            frMotor.setPower(rightPower);
            leftCascade.setPower(motorPower);
            rightCascade.setPower(motorPower);


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