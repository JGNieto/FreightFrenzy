package org.baylorschool.teamcode.otherbots;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "CascadeLiftTest", group = "Test")
public class CascadeLift extends LinearOpMode {

    final double liftPower = .4;
    final double grabPower = .4;

    private DcMotor leftCascade;
    private DcMotor rightCascade;
    private DcMotor grabberMotor;

    @Override
    public void runOpMode() {

        leftCascade = hardwareMap.get(DcMotor.class, "leftCascade");
        rightCascade = hardwareMap.get(DcMotor.class, "rightCascade");
        grabberMotor = hardwareMap.get(DcMotor.class, "grabber");

        leftCascade.setDirection(DcMotorSimple.Direction.REVERSE);
        rightCascade.setDirection(DcMotorSimple.Direction.REVERSE);

        leftCascade.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightCascade.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        grabberMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();
        while (opModeIsActive()) {
            double motorPower = 0;

            if (gamepad1.y)
                motorPower += liftPower;

            if (gamepad1.a)
                motorPower -= liftPower;

            leftCascade.setPower(motorPower);
            rightCascade.setPower(motorPower);
            grabberMotor.setPower(gamepad1.left_bumper ? grabPower : 0);
        }
    }
}
