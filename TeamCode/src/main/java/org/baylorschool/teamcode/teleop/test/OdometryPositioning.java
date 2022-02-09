package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Mecanum;

@TeleOp(name="OdometryPositioning", group="Test")
public class OdometryPositioning extends LinearOpMode {

    private IMU imu;

    private final double SLOW_MODE_COEFFICIENT = 0.5;
    private final double ROTATION_COEFFICIENT = 0.8;

    // 0 = left
    // 1 = right
    // 2 = middle

    @Override
    public void runOpMode() {
        Servo left = hardwareMap.get(Servo.class, Globals.servoLeftHw);
        Servo right = hardwareMap.get(Servo.class, Globals.servoRightHw);
        Servo middle = hardwareMap.get(Servo.class, Globals.servoMiddleHw);
        Mecanum mecanum = new Mecanum(hardwareMap);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        Servo servo = left;
        int selectedServo = 0;

        waitForStart();

        boolean slowMode = false;

        while (opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rotation = gamepad1.right_stick_x * ROTATION_COEFFICIENT;

            if (gamepad1.y || gamepad1.a) {
                if (gamepad1.y)
                    selectedServo--;
                else if (gamepad1.a)
                    selectedServo++;

                if (selectedServo > 2 || selectedServo < 0) {
                    selectedServo = 0;
                }

                switch (selectedServo) {
                    case 0:
                        servo = left;
                        break;
                    case 1:
                        servo = right;
                        break;
                    case 2:
                        servo = middle;
                        break;
                }
            }

            if (gamepad1.dpad_up) {
                servo.setPosition(servo.getPosition() + 0.01);
            } else if (gamepad1.dpad_down) {
                servo.setPosition(servo.getPosition() - 0.01);
            }

            if (gamepad1.b) {
                servo.setPosition(servo.getPosition() + 0.001);
            } else if (gamepad1.x) {
                servo.setPosition(servo.getPosition() - 0.001);
            }

            if (gamepad1.dpad_right) {
                servo.setPosition(servo.getPosition() + 0.1);
            } else if (gamepad1.dpad_left) {
                servo.setPosition(servo.getPosition() - 0.1);
            }

            slowMode = slowModeToggle(gamepad1, slowMode);
            mecanum.moveGamepad(y, x, rotation, slowMode ? SLOW_MODE_COEFFICIENT : 1);

            telemetry.addData("Left", left.getPosition());
            telemetry.addData("Right", right.getPosition());
            telemetry.addData("Middle", middle.getPosition());
            telemetry.addData("Selected", selectedServo == 0 ? "Left" : selectedServo == 1 ? "Right" : "Middle");
            telemetry.update();
            sleep(100);
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
}
