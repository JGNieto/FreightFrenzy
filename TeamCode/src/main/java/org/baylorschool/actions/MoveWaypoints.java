package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.ftclib.PIDFController;
import org.baylorschool.library.localization.Localization;
import org.baylorschool.library.math.MinPower;
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

            if (distanceSquared < 300 * 300)
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

        PIDFController pidX = Globals.movementPIDFController();
        PIDFController pidY = Globals.movementPIDFController();

        pidX.setTolerance(5);
        pidY.setTolerance(5);

        pidX.setSetPoint(0);
        pidY.setSetPoint(0);

        while (opMode.opModeIsActive() && !(pidX.atSetPoint() && pidY.atSetPoint())) { // PID
            currentLocation = localization.calculateNewLocation(currentLocation);

            double x = currentGoal.getX() - currentLocation.getX();
            double y = currentGoal.getY() - currentLocation.getY();

            double theta = currentLocation.getHeading();
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);

            // https://en.wikipedia.org/wiki/Rotation_of_axes
            // We rotate the axis to get xPrime and yPrime (like x' and y') in Wikipedia with the heading of the robot as 0º.
            double xPrime = (+ x * cosTheta + y * sinTheta);
            double yPrime = (- x * sinTheta + y * cosTheta);

            double xPower = pidX.calculate(xPrime) * Globals.movementPIDFCoefficient;
            double yPower = pidY.calculate(yPrime) * Globals.movementPIDFCoefficient;

            telemetry.addData("X Original", xPower);
            telemetry.addData("Y Original", yPower);

            xPower = MinPower.minPower(xPower, Globals.movementPIDFMinPower);
            yPower = MinPower.minPower(yPower, Globals.movementPIDFMinPower);

            if (pidX.atSetPoint())
                xPower = 0;

            if (pidY.atSetPoint())
                yPower = 0;

            mecanum.moveNoScaling(yPower, -xPower, 0);
            telemetry.addData("X Power", xPower);
            telemetry.addData("Y Power", yPower);
            telemetry.addData("X Error", pidX.getPositionError());
            telemetry.addData("Y Error", pidY.getPositionError());
            telemetry.update();
        }

        mecanum.stop();
        mecanum.setBackwards(false);
        localization.setBackwards(false);

        // Once we have reached the position, we adjust the rotation with PID, if needed.
        if (finalAngle != -1)
            currentLocation = rotatePID(currentLocation, localization, mecanum, finalAngle, opMode);

        if (runnable != null)
            runnable.run();

        return currentLocation;
    }

    public static Location rotatePID(Location currentLocation, Localization localization, Mecanum mecanum, double targetAngle, LinearOpMode opMode) {
        // Since the nature of angles is weird, we make the set-point be zero always, and adjust
        // the measured value (heading) accordingly, to avoid confusing the system.
        PIDFController pid = Globals.rotationPIDFController();
        pid.setSetPoint(0);
        pid.setTolerance(3);

        double angleDiff = Location.angleTurn(targetAngle, currentLocation.getHeading());
        double rotPower = pid.calculate(angleDiff) * Globals.rotationPIDFCoefficient;

        while (opMode.opModeIsActive() && !pid.atSetPoint()) {
            currentLocation = localization.calculateNewLocation(currentLocation);
            angleDiff = Location.angleTurn(targetAngle, currentLocation.getHeading());
            rotPower = pid.calculate(angleDiff) * Globals.rotationPIDFCoefficient;
            rotPower = MinPower.minPower(rotPower, Globals.rotationPIDFMinPower);

            mecanum.moveNoScaling(0, 0, rotPower);

            opMode.telemetry.addData("Angle diff", angleDiff);
            opMode.telemetry.addData("Rot power", rotPower);
            opMode.telemetry.update();
        }
        mecanum.stop();
        return currentLocation;
    }
}
