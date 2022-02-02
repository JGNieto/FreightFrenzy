package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.MovePurePursuit;
import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.localization.Odometry;

@Autonomous(name = "PIDTest", group = "Test")
public class PIDTest extends LinearOpMode {
    Location currentLocation = new Location(Places.middle(-2), Places.middle(2), -90);
    Odometry odometry;
    Mecanum mecanum;
    IMU imu;

    @Override
    public void runOpMode() {

        imu = new IMU(hardwareMap);
        mecanum = new Mecanum(hardwareMap);
        odometry = new Odometry(hardwareMap, imu, false);

        waitForStart();

        odometry.reset();
        odometry.calculateNewLocation(currentLocation);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MoveWaypoints.moveWaypoints(new Path(new Location(Places.middle(-2), Places.middle(1), -90)), mecanum, odometry, currentLocation, this);
    }
}

