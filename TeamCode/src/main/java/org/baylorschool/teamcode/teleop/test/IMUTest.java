package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.library.IMU;

@TeleOp(name="IMUTest", group="Test")
public class IMUTest extends LinearOpMode {

    private IMU imu;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        imu = new IMU();
        imu.initializeImu(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            imu.updateOrientation();
            telemetry.addData("X Angle", imu.getAllAngles().firstAngle);
            telemetry.addData("Y Angle", imu.getAllAngles().secondAngle);
            telemetry.addData("Z Angle", imu.getAllAngles().thirdAngle);
            telemetry.update();
        }
    }
}
