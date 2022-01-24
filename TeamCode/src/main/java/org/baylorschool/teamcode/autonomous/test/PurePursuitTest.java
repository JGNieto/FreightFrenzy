package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MovePurePursuit;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Path;

@Autonomous(name = "PurePursuitTest", group = "Test")
public class PurePursuitTest extends LinearOpMode {

    Location currentLocation = new Location(Places.blueRightStart);
    Odometry odometry;
    Mecanum mecanum;
    IMU imu;
    Path path = new Path(new Location[]{
            new Location(-1225.43, 1219.33),
            new Location(-1554.65, 609.67),
            new Location(-1243.72, -6.1),
            new Location(-634.05, 0),
            new Location(-18.29, -6.1),
            new Location(292.64, 371.9),
            new Location(298.74, 920.6),

    });

    @Override
    public void runOpMode() {

        imu = new IMU(hardwareMap);
        mecanum = new Mecanum(hardwareMap);
        odometry = new Odometry(mecanum.getFlMotor(), mecanum.getFrMotor(), mecanum.getBlMotor(),
                hardwareMap.get(Servo.class, Globals.servoLeftHw),
                hardwareMap.get(Servo.class, Globals.servoRightHw),
                hardwareMap.get(Servo.class, Globals.servoMiddleHw),
                imu,
                false
        );

        waitForStart();

        odometry.reset();
        odometry.calculateNewLocation(currentLocation);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        while (opModeIsActive()) {
            MovePurePursuit.movePurePursuit(currentLocation, path, this, odometry, mecanum);
        }
    }
}
