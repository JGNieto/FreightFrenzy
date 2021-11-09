package org.baylorschool.teamcode.teleop.test;


import com.qualcomm.hardware.motors.RevRoboticsCoreHexMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="TwoBarLift", group="Test")
public class TwoBarLift extends LinearOpMode {

    private DcMotor roller;
    private DcMotor twobar1;

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

        while (opModeIsActive()) {

            if (gamepad1.a) {
                twobar1.setPower(.8);
            } else if (gamepad1.y) {
                twobar1.setPower(-.4);
            } else {
                twobar1.setPower(0);
            } if (gamepad1.b) {
                roller.setPower(.1);
            } else if (gamepad1.x) {
                roller.setPower(-.1);
            } else {
                roller.setPower(0);
            }
        }
    }
}
