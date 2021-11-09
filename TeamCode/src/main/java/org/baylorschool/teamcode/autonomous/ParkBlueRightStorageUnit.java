package org.baylorschool.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.actions.MoveWaypoints;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.Vuforia;

import java.util.Arrays;

@Autonomous(name="Park Storage Unit BR", group ="BlueRight")
public class ParkBlueRightStorageUnit extends LinearOpMode {

    private Location currentLocation = new Location(-609.6, 1608, -90);
    private Sensors sensors;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Getting ready");
        telemetry.update();

        sensors = new Sensors(hardwareMap, false);
        sensors.initialize(hardwareMap, currentLocation.getHeading());

        Location[] locations = new Location[] {
                new Location(-609.6, 1219.2),
                new Location(-914.4, 914.4),
                new Location(-1564, 914.4)
        };

        telemetry.addData("Status", "Ready!");
        telemetry.update();

        waitForStart();
        sensors.getMecanum().resetEncoders();

        currentLocation = MoveWaypoints.moveToWaypoints(currentLocation, sensors.getVuforia(), sensors.getImu(), Arrays.asList(locations), telemetry, sensors.getMecanum(), 180, this);

        sensors.end();
    }
}
