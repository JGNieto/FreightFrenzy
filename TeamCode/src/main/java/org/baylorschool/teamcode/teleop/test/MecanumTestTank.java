package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="MecanumTestTank", group="Test")
@Disabled
public class MecanumTestTank extends LinearOpMode {

    private DcMotor blMotor = null;
    private DcMotor flMotor = null;
    private DcMotor brMotor = null;
    private DcMotor frMotor = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        blMotor = hardwareMap.get(DcMotor.class, "blMotor");
        flMotor = hardwareMap.get(DcMotor.class, "flMotor");
        brMotor = hardwareMap.get(DcMotor.class, "brMotor");
        frMotor = hardwareMap.get(DcMotor.class, "frMotor");

        brMotor.setDirection(DcMotor.Direction.REVERSE);
        frMotor.setDirection(DcMotor.Direction.REVERSE);
        blMotor.setDirection(DcMotor.Direction.FORWARD);
        flMotor.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();

        final double slowModeCoefficient = 0.6;

        while (opModeIsActive()) {
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
        }
    }
}
