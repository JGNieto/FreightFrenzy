package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;

@TeleOp(name="ServoTest", group="Test")
public class ServoTest extends LinearOpMode {

    Servo servoLeft;
    Servo servoRight;
    Servo servoMiddle;

    @Override
    public void runOpMode() {

        servoLeft = hardwareMap.get(Servo.class, Globals.servoLeftHw);
        servoRight = hardwareMap.get(Servo.class, Globals.servoRightHw);
        servoMiddle = hardwareMap.get(Servo.class, Globals.servoMiddleHw);
        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.dpad_left) {
                servoLeft.setPosition(Globals.positionWithdrawnLeft);
                servoRight.setPosition(Globals.positionWithdrawnRight);
                servoMiddle.setPosition(Globals.positionWithdrawnMiddle);
                telemetry.addData("Servo Position", "Withdraw");
            }
            if (gamepad1.dpad_right) {
                servoLeft.setPosition(Globals.positionOpenLeft);
                servoRight.setPosition(Globals.positionOpenRight);
                servoMiddle.setPosition(Globals.positionOpenMiddle);
                telemetry.addData("Servo Position", "Open");
            }

            telemetry.update();
        }
    }
}