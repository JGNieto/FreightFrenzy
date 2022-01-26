package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.GrabFreightBlindly;
import org.baylorschool.actions.MovePurePursuit;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Path;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.lift.TwoBarLift;

@Autonomous(name = "PurePursuitTest", group = "Test")
public class PurePursuitTest extends LinearOpMode {

    Location currentLocation = new Location(Places.middle(1), Places.closePerpendicular(3));
    Odometry odometry;
    Mecanum mecanum;
    IMU imu;
    Lift lift;

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
        lift = new TwoBarLift(this);
        lift.initialize();
        lift.startThread();

        waitForStart();

        odometry.reset();
        odometry.calculateNewLocation(currentLocation);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        GrabFreightBlindly.grabFreightBlindly(currentLocation, mecanum, lift, odometry, this, Globals.WarehouseSide.BLUE);
    }
}
