package org.baylorschool.teamcode.autonomous.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.actions.GrabFreightBlindly;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.localization.Odometry;
import org.baylorschool.library.lift.TwoBarLift;

@Autonomous(name = "BlindWarehouseTest", group = "Test")
public class BlindWarehouseTest extends LinearOpMode {

    Globals.WarehouseSide warehouseSide = Globals.WarehouseSide.RED;
    Location currentLocation = new Location(Places.middle(1), Places.closePerpendicular(warehouseSide == Globals.WarehouseSide.BLUE ? 3 : -3));
    Odometry odometry;
    Mecanum mecanum;
    IMU imu;
    TwoBarLift lift;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Loading...");
        telemetry.update();

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
        lift.moveDown(this);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();

        lift.initialize();
        lift.setTelemetryEnabled(false);
        lift.startThread();

        odometry.reset();
        odometry.calculateNewLocation(currentLocation);
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        GrabFreightBlindly.grabFreightBlindly(currentLocation, mecanum, lift, odometry, this, warehouseSide);

        lift.closeThread();
    }
}