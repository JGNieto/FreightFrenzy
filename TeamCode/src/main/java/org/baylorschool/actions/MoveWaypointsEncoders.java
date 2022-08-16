package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.Vuforia;
import org.baylorschool.library.localization.Localization;
import org.baylorschool.library.localization.MotorEncoders;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public class MoveWaypointsEncoders {

    public static Location moveToWaypoints(Location currentLocation, Localization localization, List<Location> locations, Telemetry telemetry, Mecanum mecanum, LinearOpMode opMode, double finalAngle) {
        Path path = new Path(locations);
        boolean wasBusy = false; // TODO Test without "wasBusy" feature.
        while (opMode.opModeIsActive()) {
            currentLocation = localization.calculateNewLocation(currentLocation);
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
                    if (currentGoal.isBackwards()) {
                        mecanum.setBackwards(true);
                        localization.setBackwards(true);
                    } else {
                        mecanum.setBackwards(false);
                        localization.setBackwards(false);
                    }
                    opMode.sleep(200);
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

            currentLocation.reportTelemetry(telemetry);

            telemetry.addData("Target...", "%.0f, %.0f, %.0f", currentGoal.getX(), currentGoal.getY(), currentGoal.getHeading());

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
        mecanum.setBackwards(false);
        localization.setBackwards(false);
        if (finalAngle != -1) {
            mecanum.rotate(Location.angleTurn(currentLocation.getHeading(), finalAngle));
            while (mecanum.isBusy()) {
            }
        }
        return currentLocation;
    }

    public static Location moveToWaypoints(Location currentLocation, Vuforia vuforia, IMU imu, List<Location> locations, Telemetry telemetry, Mecanum mecanum, LinearOpMode opMode) {
        MotorEncoders motorEncoders = new MotorEncoders(mecanum, imu, telemetry, vuforia);
        return moveToWaypoints(currentLocation, motorEncoders, locations, telemetry, mecanum, opMode, locations.get(locations.size() - 1).getHeading());
    }

    public static Location moveToWaypoints(Location currentLocation, Sensors sensors, List<Location> locations, LinearOpMode opMode) {
        return moveToWaypoints(currentLocation, sensors.getVuforia(), sensors.getImu(), locations, opMode.telemetry, sensors.getMecanum(), opMode);
    }
}
