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
import org.baylorschool.library.Path;
import org.baylorschool.library.localization.Odometry;

@Autonomous(name = "PurePursuitTest", group = "Test")
public class PurePursuitTest extends LinearOpMode {
    Location currentLocation = new Location(Places.blueRightStart);
    Odometry odometry;
    Mecanum mecanum;
    IMU imu;
    Path path = new Path(new Location[]{
            /*new Location(-1579.04, 951.08),
            new Location(-1219.33, 585.28),
            new Location(-987.66, 335.32),
            new Location(-1219.33, -18.29),
            new Location(-1536.36, -298.74),
            new Location(-1231.53, -627.96),
            new Location(-1572.94, -902.31),
            new Location(-896.21, -1560.75),
            new Location(Places.middle(2), Places.middle(-1), 180),*/
            new Location(-1512, 915),
            new Location(-1524, -890),
            new Location(-841, -1177),
            new Location(-317, -1390),
            new Location(91, -1146),
            new Location(250, -921),
            new Location(268, 256),
            new Location(299, 969),
            new Location(146, 1451),
            new Location(-329, 1451),
            new Location(-530, 1524),
            new Location(-902, 1536),

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

        telemetry.setMsTransmissionInterval(10);

        odometry.reset();
        odometry.calculateNewLocation(currentLocation);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        while (opModeIsActive())
            MovePurePursuit.movePurePursuit(currentLocation, path, this, odometry, mecanum);
        mecanum.stop();
    }
}

