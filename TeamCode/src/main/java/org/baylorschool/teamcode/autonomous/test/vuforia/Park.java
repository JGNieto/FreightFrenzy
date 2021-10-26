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
        telemetry.addData("Status", "Getting ready");
        telemetry.update();
        vuforia = new Vuforia(hardwareMap);
        vuforia.initializeParamers(false);

        Location[] locations = new Location[]{
                new Location(0, 0, 0, 0, 0, 0),
                new Location(-1219.2, 0, 0, 0, 0, 0)
        };

        mecanum = new Mecanum(hardwareMap);
        path = new Path(Arrays.asList(locations), new Location(100,100,-1,-1,-1,3));
        imu = new IMU();

        imu.initializeImu(hardwareMap);
        imu.forceValue(currentLocation.getHeading());

        vuforia.startTracking();

        telemetry.addData("Status", "Ready!");
        telemetry.update();

        waitForStart();
        mecanum.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        mecanum.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        boolean wasBusy = false;

        while (opModeIsActive()) {
            mecanum.updateEncoderReadings();
            currentLocation = Location.updateLocation(currentLocation, vuforia, imu, telemetry, mecanum);
            path.checkGoal(currentLocation);
            // FIXME: REMOVE NEXT LINE
            path.checkGoalTelemetry(currentLocation, telemetry);
            Location currentGoal = path.currentGoal();

            if (currentGoal == null) {
                requestOpModeStop();
                continue;
            }

            double targetAngle = Location.angleLocations(currentLocation, currentGoal);
            telemetry.addData("Angle Diff", Location.angleTurn(currentLocation.getHeading(), targetAngle));
            telemetry.addData("Locations Left", path.getLocations().size());
            if (!mecanum.isBusy()) {
                if (wasBusy) {
                    sleep(500);
                    wasBusy = false;
                    continue;
                }
                wasBusy = true;
                if (!currentLocation.rotationTolerance(targetAngle, path.getTolerance().getHeading())) {
                    mecanum.rotate(Location.angleTurn(currentLocation.getHeading(), targetAngle));
                } else {
                    mecanum.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    mecanum.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    mecanum.setTargetDistance(Location.distance(currentLocation, currentGoal));
                    mecanum.setPowerAutonomous();
                }
            }

            currentLocation.reportTelemtry(telemetry);

            telemetry.addData("Encoders Delta", "{FR, FL, BR, BL} = %.0f, %.0f, %.0f, %.0f",
                    mecanum.getLatestDeltaFr(),
                    mecanum.getLatestDeltaFl(),
                    mecanum.getLatestDeltaBr(),
                    mecanum.getLatestDeltaBl()
            );
            telemetry.addData("Encoders Value", "{FR, FL, BR, BL} = %d, %d, %d, %d",
                    mecanum.getLastReadingFr(),
                    mecanum.getLastReadingFl(),
                    mecanum.getLastReadingBr(),
                    mecanum.getLastReadingBl()
            );
            telemetry.addData("Target", mecanum.getBlMotor().getTargetPosition());
            telemetry.update();
        }

        vuforia.stopTracking();
    }

}
