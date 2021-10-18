package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TuneFlyWheel", group="Test")
public class TuneFlyWheel extends LinearOpMode {

    private DcMotor flyWheel = null;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();


        flyWheel = hardwareMap.get(DcMotor.class, "flyWheel");

        waitForStart();
        runtime.reset();

        double flyWheelPower = 0.6;
        double lastIncrement = 0;

        while (opModeIsActive()) {
            if (runtime.milliseconds() - lastIncrement > 750) {
                double _flyWheelPower = flyWheelPower;
                if (gamepad1.a) flyWheelPower -= 0.01;
                if (gamepad1.b) flyWheelPower += 0.01;
                if (gamepad1.x) flyWheelPower -= 0.05;
                if (gamepad1.y) flyWheelPower += 0.05;
                if (_flyWheelPower != flyWheelPower) lastIncrement = runtime.milliseconds();
            }

            flyWheel.setPower(gamepad1.right_bumper ? flyWheelPower : 0);
            telemetry.addData("FlyWheel", flyWheelPower);
            telemetry.addData("Michael", "I know you are going to ask. " +
                    "The pneumatic wheel is setup to be 0.65.");
            telemetry.update();
        }
    }
}
