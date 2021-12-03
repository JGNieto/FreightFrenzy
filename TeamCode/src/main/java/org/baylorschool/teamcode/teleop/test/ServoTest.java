package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="ServoTest", group="Test")
public class ServoTest extends LinearOpMode {

    Servo servo;

    @Override
    public void runOpMode() {

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_left) {
                servo.setDirection(Servo.Direction.FORWARD);
                telemetry.addData("Servo Position", "Left");
            } else if (gamepad1.dpad_right) {
                servo.setDirection(Servo.Direction.REVERSE);
                telemetry.addData("Servo Position", "Right");
            } else {

            }



        }
    }
}