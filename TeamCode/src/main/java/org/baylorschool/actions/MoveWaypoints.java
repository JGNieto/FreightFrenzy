package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.Vuforia;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public class MoveWaypoints {

    public static Location moveToWaypoints(Location currentLocation, Vuforia vuforia, IMU imu, List<Location> locations, Telemetry telemetry, Mecanum mecanum, LinearOpMode opMode, double finalAngle) {
        Path path = new Path(locations, new Location(50, 50, -1, -1, -1, 3));
        boolean wasBusy = false; // FIXME Test without "wasBusy" feature.
        while (opMode.opModeIsActive()) {
            mecanum.updateEncoderReadings();
            currentLocation = Location.updateLocation(currentLocation, vuforia, imu, telemetry, mecanum);
            path.checkGoal(currentLocation);
            Location currentGoal = path.currentGoal();

            if (currentGoal == null) {
                break;
            }

            double targetAngle = Location.angleLocations(currentLocation, currentGoal);
            telemetry.addData("Angle Diff", Location.angleTurn(currentLocation.getHeading(), targetAngle));
            telemetry.addData("Locations Left", path.getLocations().size());
            if (!mecanum.isBusy()) {
                if (wasBusy) {
                    opMode.sleep(500);
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
        if (finalAngle != -1) {
            mecanum.rotate(Location.angleTurn(currentLocation.getHeading(), finalAngle));
            while (mecanum.isBusy()) {
            }
        }
        return currentLocation;
    }

    public static Location moveToWaypoints(Location currentLocation, Vuforia vuforia, IMU imu, List<Location> locations, Telemetry telemetry, Mecanum mecanum, LinearOpMode opMode) {
        return moveToWaypoints(currentLocation, vuforia, imu, locations, telemetry, mecanum, opMode, locations.get(locations.size() - 1).getHeading());
    }
}
