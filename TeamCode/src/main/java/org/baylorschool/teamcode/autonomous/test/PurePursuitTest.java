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
            new Location(-951.08, 1383.94),
            new Location(-938.89, 999.85),
            new Location(-1103.5, 566.99),
            new Location(-1255.91, 237.77),
            new Location(-1091.3, -274.35),
            new Location(-719.41, -121.93),
            new Location(-493.83, 12.19),
            new Location(-292.64, 6.1),
            new Location(91.45, 0),
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
