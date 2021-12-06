package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="ServoTest", group="Test")
public class ServoTest extends LinearOpMode {

    Servo servo; // CHANGE THIS TO THE SERVO NAME

    @Override
    public void runOpMode() {

        servo = hardwareMap.get(Servo.class, "servo"); //CHANGE THIS TO WHATEVER IT IS IN THE HW MAP
        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.dpad_left) {
               servo.setPosition(0);
               telemetry.addData("Servo Position", "Left");
            }
            if (gamepad1.dpad_right) {
                servo.setPosition(.8);
                telemetry.addData("Servo Position", "Right");
            }





        }
    }
}