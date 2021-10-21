package org.baylorschool.teamcode.autonomous.test.vuforia;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.Vuforia;

import java.util.Arrays;

@Autonomous(name="Park", group ="VuforiaTest")
public class Park extends LinearOpMode {

    private Vuforia vuforia;
    private Location currentLocation = new Location(0, -914.4, 0, 0, 0, -90);
    private Mecanum mecanum;
    private Path path;
    private IMU imu;

    @Override
    public void runOpMode() {
        vuforia = new Vuforia(hardwareMap);
        vuforia.initializeParamers(false);

        Location[] locations = new Location[]{
                new Location(0, 0, 0, 0, 0, 0)
        };

        mecanum = new Mecanum(hardwareMap);
        path = new Path(Arrays.asList(locations), new Location(10,10,-1,-1,-1,3));
        imu = new IMU();

        waitForStart();
        vuforia.startTracking();
        mecanum.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        while (opModeIsActive()) {
            Location currentGoal = path.currentGoal();
            if (currentGoal == null) {
                requestOpModeStop();
                continue;
            }

            mecanum.updateEncoderReadings();

            currentLocation = Location.updateLocation(currentLocation, vuforia, imu, telemetry, mecanum);
            double targetAngle = Location.angleLocations(currentLocation, currentGoal);

            if (!currentLocation.rotationTolerance(targetAngle, path.getTolerance().getHeading())) {
                mecanum.rotate(Location.angleTurn(currentLocation.getHeading(), targetAngle));
            } else if (!mecanum.isBusy()) {
                mecanum.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                mecanum.setTargetDistance(50);
                mecanum.setPowerAutonomous();
            }

            currentLocation.reportTelemtry(telemetry);
            telemetry.update();
        }

        vuforia.stopTracking();
    }

}
