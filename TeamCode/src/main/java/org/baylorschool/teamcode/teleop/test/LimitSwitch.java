package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp(name = "LimitSwitch", group = "Sensor")
public class LimitSwitch extends LinearOpMode {

    DigitalChannel limitSwitch;
    DcMotor roller;

    @Override
    public void runOpMode() {

        roller = hardwareMap.get(DcMotor.class,"roller");
        limitSwitch = hardwareMap.get(DigitalChannel.class, "limit_switch");
        limitSwitch.setMode(DigitalChannel.Mode.INPUT);

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.left_bumper & !limitSwitch.getState()) {
                roller.setPower(1);
                telemetry.addData("Limit Switch", "pressed");
            } else if (gamepad1.right_bumper) {
                roller.setPower(-1);
                telemetry.addData("Limit Switch", "not pressed");
            } else {
                roller.setPower(0);
            }
            telemetry.update();

        }
    }
}

