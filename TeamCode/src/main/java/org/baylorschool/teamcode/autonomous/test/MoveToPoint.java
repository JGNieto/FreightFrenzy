package org.baylorschool.teamcode.autonomous.test;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Places;
import org.baylorschool.actions.MovePurePursuit;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;

@Autonomous(name = "MoveToPointTest", group = "Test")
public class MoveToPoint extends LinearOpMode {

    private Mecanum mecanum;

    @Override
    public void runOpMode() {
        Location startLocation = new Location(Places.closePerpendicular(-3), Places.middle(2), 90);
        Location targetLocation = new Location(Places.middle(-1), 0);

        mecanum = new Mecanum(hardwareMap);
        IMU imu = new IMU();
        imu.initializeImu(hardwareMap);
        imu.forceValue(startLocation.getHeading());

        waitForStart();

        while (opModeIsActive()) {
            startLocation.setHeading(imu.getHeading());
            MovePurePursuit.moveTowardPositionAngle(mecanum, startLocation, targetLocation, 0, .1, telemetry);
            telemetry.update();
        }
    }
}
