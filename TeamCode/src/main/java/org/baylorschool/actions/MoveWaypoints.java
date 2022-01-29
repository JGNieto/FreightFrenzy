package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.ftclib.PIDFController;
import org.baylorschool.library.localization.Localization;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MoveWaypoints {

    public static Location moveWaypoints(Path path, Mecanum mecanum, Localization localization, Location currentLocation, LinearOpMode opMode) {
        Telemetry telemetry = opMode.telemetry;
        double finalAngle = path.getLastLocation().getHeading();
        Runnable runnable = path.getLastLocation().getRunnable();

        // We will move without caring too much about precision for all the points except for the last one.
        if (path.getLocations().size() >= 2) {
            while (opMode.opModeIsActive()) {
                currentLocation = localization.calculateNewLocation(currentLocation);

                // Note: checkGoal checks whether we have reached a goal and BOTH removes it from
                // the list of future goals AND returns whether it has removed anything.
                // The code is written like *that* for readability.
                if (path.checkGoal(currentLocation)) {
                    if (path.getLocations().size() < 2)
                        break;
                }

                Location currentGoal = path.currentGoal();

                currentLocation.reportTelemetry(telemetry);

                if (currentGoal.getHeading() != -1) {
                    telemetry.addData("Angle", "Absolute");
                    MovePurePursuit.moveTowardPositionAngle(mecanum, currentLocation, currentGoal, currentGoal.getHeading(), currentGoal.getPurePursuitTurnSpeed(), telemetry);
                } else {
                    telemetry.addData("Angle", "Relative");
                    MovePurePursuit.moveTowardPosition(mecanum, currentLocation, currentGoal, 0, currentGoal.getPurePursuitTurnSpeed(), telemetry);
                }

                telemetry.addData("Target...", "%.0f, %.0f, %.0f", currentGoal.getX(), currentGoal.getY(), currentGoal.getHeading());
                telemetry.update();
            }
        }

        Location currentGoal = path.currentGoal();

        // For the last point, we move without precision until we get close to it. Then, we move with PID.
        while (opMode.opModeIsActive()) { // No precision
            currentLocation = localization.calculateNewLocation(currentLocation);

            double distanceSquared = Location.distanceSquared(currentLocation, currentGoal);

            if (distanceSquared < 100 * 100)
                break;

            currentLocation.reportTelemetry(telemetry);

            if (currentGoal.getHeading() != -1) {
                telemetry.addData("Angle", "Absolute");
                MovePurePursuit.moveTowardPositionAngle(mecanum, currentLocation, currentGoal, currentGoal.getHeading(), currentGoal.getPurePursuitTurnSpeed(), telemetry);
            } else {
                telemetry.addData("Angle", "Relative");
                MovePurePursuit.moveTowardPosition(mecanum, currentLocation, currentGoal, 0, currentGoal.getPurePursuitTurnSpeed(), telemetry);
            }

            telemetry.addData("Last location", "%.0f, %.0f, %.0f", currentGoal.getX(), currentGoal.getY(), currentGoal.getHeading());
            telemetry.update();
        }

        PIDFController pidX = new PIDFController(0.37, 0.05, 1.02, 0.7);
        PIDFController pidY = new PIDFController(0.37, 0.05, 1.02, 0.7);

        while (opMode.opModeIsActive()) { // PID
            currentLocation = localization.calculateNewLocation(currentLocation);
            
        }

        mecanum.stop();
        mecanum.setBackwards(false);
        localization.setBackwards(false);

        // Once we have reached the position, we adjust the rotation with PID.
        currentLocation = rotatePID(currentLocation, localization, mecanum, finalAngle, opMode);

        if (runnable != null)
            runnable.run();

        return currentLocation;
    }

    public static Location rotatePID(Location currentLocation, Localization localization, Mecanum mecanum, double targetAngle, LinearOpMode opMode) {
        // Since the nature of angles is weird, we make the set-point be zero always, and adjust
        // the measured value (heading) accordingly, to avoid confusing the system.
        PIDFController pid = new PIDFController(0.37, 0.05, 1.02, 0.7);
        pid.setSetPoint(0);
        pid.setTolerance(3);

        double angleDiff = Location.angleTurn(targetAngle, currentLocation.getHeading());
        double rotPower = pid.calculate(angleDiff);

        while (opMode.opModeIsActive() && !pid.atSetPoint()) {
            currentLocation = localization.calculateNewLocation(currentLocation);
            angleDiff = Location.angleTurn(targetAngle, currentLocation.getHeading());
            rotPower = pid.calculate(angleDiff);
            mecanum.moveNoScaling(0, 0, rotPower);

            opMode.telemetry.addData("Angle diff", angleDiff);
            opMode.telemetry.addData("Rot power", rotPower);
            opMode.telemetry.update();
        }
        mecanum.stop();
        return currentLocation;
    }
}
