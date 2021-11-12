package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="TwoBarLiftEncoders", group="Test")
public class TwoBarLiftEncoders extends LinearOpMode {
    private DcMotor roller = null;
    private DcMotor twobar1 = null;

    @Override
    public void runOpMode() {
        roller = hardwareMap.get(DcMotor.class, "roller");
        twobar1 = hardwareMap.get(DcMotor.class, "twobar1");

        roller.setDirection(DcMotor.Direction.FORWARD);
        twobar1.setDirection(DcMotor.Direction.REVERSE);

        twobar1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        twobar1.setPower(0);
        roller.setPower(0);
        waitForStart();

        twobar1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        twobar1.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        boolean movingTwobar = false;
        int targetEncoderValue = 0;

        while (opModeIsActive()) {
            if (gamepad1.dpad_up) {
                movingTwobar = true;
                twobar1.setPower(.8);
            } else if (gamepad1.dpad_down) {
                movingTwobar = true;
                twobar1.setPower(.2);
            } else {
                if (movingTwobar) {
                    movingTwobar = false;
                    targetEncoderValue = twobar1.getCurrentPosition();
                }
                if (!twobar1.isBusy()) {
                    twobar1.setTargetPosition(targetEncoderValue);
                    twobar1.setPower(.6);
                }
            }
            if (gamepad1.dpad_right) {
                roller.setPower(1);
            } else if (gamepad1.dpad_left) {
                roller.setPower(-1);
            }
        }
    }
}
