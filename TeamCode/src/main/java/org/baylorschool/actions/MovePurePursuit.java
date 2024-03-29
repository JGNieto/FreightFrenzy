package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import org.baylorschool.Globals;
import org.baylorschool.library.ExecutionFrequency;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.localization.Odometry;
import org.baylorschool.library.math.CircleIntersect;
import org.baylorschool.library.math.MinPower;
import org.baylorschool.library.math.PerpendicularDistance;
import org.baylorschool.library.math.SegmentMidpoint;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public class MovePurePursuit {
    private static final double MOVEMENT_COEFFICIENT = .8;

    /**
     * Move the robot following a path of waypoints using the Pure Pursuit algorithm.
     * @param currentLocation Current location of the robot.
     * @param path Path to follow.
     * @param opMode OpMode instance.
     * @param odometry Odometry instance.
     * @param mecanum Mecanum instance.
     * @return New location of the robot.
     */
    public static Location movePurePursuit(Location currentLocation, Path path, LinearOpMode opMode, Odometry odometry, Mecanum mecanum) {
        // Ensure the first path line is calculated from the initial location.
        path.initialLocation(new Location(currentLocation));

        ExecutionFrequency executionFrequency = new ExecutionFrequency(opMode.telemetry);

        while (opMode.opModeIsActive()) {
            // Calculate the next location using the Pure Pursuit algorithm.
            currentLocation = odometry.calculateNewLocation(currentLocation);
            currentLocation.reportTelemetry(opMode.telemetry);

            // Check whether we are within an acceptable distance to the final location. If so, movement is done.
            if (path.getLocations().size() == 1)
                break;

            opMode.telemetry.addData("Locations Left", path.getLocations().size());

            // Compute target "lookahead" location according to Pure Pursuit.
            Location purePursuitTarget = getPurePursuitPoint(path, currentLocation, opMode.telemetry);

            if (purePursuitTarget == null) { // If we are not near any path
                opMode.telemetry.addLine("Null Target");
                if (path.getLocations().size() > 1) {
                    // Moving to the first point sometimes causes problems if there is more than one point.
                    // Thus, we go toward the midpoint of the first segment.
                    Location location1 = path.getLocations().get(0);
                    Location location2 = path.getLocations().get(1);
                    Location midpoint = SegmentMidpoint.segmentMidpoint(location1, location2);
                    midpoint.reportTelemetry(opMode.telemetry);
                    moveTowardPosition(mecanum, currentLocation, midpoint, location1.getPurePursuitAngle(), location1.getPurePursuitTurnSpeed(), opMode.telemetry);
                } else {
                    Location location = path.getLocations().get(0);
                    moveTowardPosition(mecanum, currentLocation, location, location.getPurePursuitAngle(), location.getPurePursuitTurnSpeed(), opMode.telemetry);
                }
            } else {
                moveTowardPositionAngle(mecanum, currentLocation, purePursuitTarget, purePursuitTarget.getHeading(), purePursuitTarget.getPurePursuitTurnSpeed(), opMode.telemetry);
                purePursuitTarget.reportTelemetry(opMode.telemetry);
            }

            executionFrequency.execution();
            opMode.telemetry.update();
        }

        currentLocation = MoveWaypoints.moveWaypoints(path, mecanum, odometry, currentLocation, opMode);

        return currentLocation;
    }

    /**
     * Computes the best "lookahead" location from Pure Pursuit.
     * @param path Path of the robot.
     * @param currentLocation Current location of the robot.
     * @return Computed location or null if the robot is not near any segments.
     */
    public static Location getPurePursuitPoint(Path path, Location currentLocation, Telemetry telemetry) {
        if (path.getLocations().size() <= 2) return null;

        int championStartLocationIndex = 0;
        double championDistance = -1;

        // To know where we are in the path, we calculate the shortest distance between the robot
        // and each segment, and use it.
        // TODO: Improve edge case checking.

        // for (int i = 1; i < path.getLocations().size() - 1; i++) {
        for (int i = 0; i < path.getLocations().size() - 1; i++) {
            Location startLocation = path.getLocations().get(i);
            Location endLocation = path.getLocations().get(i + 1);

            double distance = PerpendicularDistance.getShortestDistanceBetweenPointAndSegmentSquared(startLocation, endLocation, currentLocation);
            if (distance < championDistance || championDistance == -1) {
                championStartLocationIndex = i;
                championDistance = distance;
            }
        }

        telemetry.addData("Ch Start Location", championStartLocationIndex);

        // To avoid going back and redoing previous paths, we remove waypoints we have already passed.
        // This is inefficient if we remove more than one because of the way ArrayList is implemented.
        // However, removing more than one is very rare, so we accept this inefficiency.
        for (int i = 0; i < championStartLocationIndex; i++) {
            telemetry.log().add("Removing location. Left: " + (path.getLocations().size() - 1));

            Runnable runnable = path.getLocations().get(i).getRunnable();
            if (runnable != null)
                runnable.run();

            path.getLocations().remove(i);
        }

        if (path.getLocations().size() <= 2) return null;

        Location lastLocation = path.getLastLocation();
        Location championEndLocation = path.getLocations().get(championStartLocationIndex + 1);

        // Edge case: if very close to final target, go straight to it.
        // We do this after loop to avoid cutting a corner we didn't want to.
        if (championEndLocation == lastLocation && Location.distanceSquared(championEndLocation, lastLocation) < 100 * 100) {
            return lastLocation;
        }

        Location championIntersection = null;

        for (int i = championStartLocationIndex; i < path.getLocations().size() - 1; i++) {
            // Get intersections for the optimal line.
            Location startLocation = path.getLocations().get(i);
            Location endLocation = path.getLocations().get(i + 1);

            double smallX = Math.min(startLocation.getX(), endLocation.getX());
            double bigX = Math.max(startLocation.getX(), endLocation.getX());
            double smallY = Math.min(startLocation.getY(), endLocation.getY());
            double bigY = Math.max(startLocation.getY(), endLocation.getY());

            // Distance squared is used to avoid doing a square root, which is very expensive.
            double distanceRobotToEndLocation = Location.distanceSquared(currentLocation, endLocation);

            List<Location> intersections = CircleIntersect.getCircleLineIntersectionLocation(startLocation, endLocation, currentLocation, endLocation.getPurePursuitRadius());

            for (Location intersection : intersections) {
                // Testing code. Original:
                // if (intersection.getX() < smallX || intersection.getY() < smallY || intersection.getX() > bigX || intersection.getY() > bigY)
                //  continue;

                // Testing:
                if ((intersection.getX() < smallX || intersection.getY() < smallY) && (intersection.getX() > bigX || intersection.getY() > bigY))
                    continue;

                double distance = Location.distanceSquared(intersection, endLocation);
                if (distance > distanceRobotToEndLocation) continue;

                if (championIntersection == null || championDistance < distance) {
                    championIntersection = intersection;
                    championDistance = distance;
                    championEndLocation = endLocation;
                }
            }

            if (championIntersection != null) {
                telemetry.addData("Champ Int", i);
                break;
            }
        }

        if (championIntersection != null) {
            championIntersection.setHeading(Location.angleLocations(currentLocation, championEndLocation));

            // Do not turn when very close.
            if (Location.distanceSquared(currentLocation, championEndLocation) < championEndLocation.getPurePursuitDistanceStopTurning2())
                championIntersection.setPurePursuitTurnSpeed(0);
            else
                championIntersection.setPurePursuitTurnSpeed(championEndLocation.getPurePursuitTurnSpeed());
        }
        return championIntersection;
    }

    // The purpose of this function is to move the robot towards the desired position, regardless
    // of the path algorithm (i.e. regardless of Pure Pursuit or others).
    public static void moveTowardPositionAngle(Mecanum mecanum, Location currentLocation, Location target, double targetAngle, double turnSpeed) {
        moveTowardPositionAngle(mecanum, currentLocation, target, targetAngle, turnSpeed, null);
    }

    public static void moveTowardPositionAngle(Mecanum mecanum, Location currentLocation, Location target, double targetAngle, double turnSpeed, Telemetry telemetry) {
        moveTowardPositionAngle(mecanum, currentLocation, target, targetAngle, turnSpeed, MOVEMENT_COEFFICIENT, telemetry);
    }

    public static void moveTowardPositionAngle(Mecanum mecanum, Location currentLocation, Location target, double targetAngle, double turnSpeed, double speed, Telemetry telemetry) {
        double distanceToTarget = Location.distance(currentLocation, target);
        double absoluteAngleDiff = Location.angleLocations(currentLocation, target); // Angle between points.
        double relativeAngleDiff = Location.angleBound(absoluteAngleDiff - currentLocation.getHeading()); // Angle for the robot to turn.

        double relativeXDiff = Math.sin(Math.toRadians(relativeAngleDiff)) * distanceToTarget;
        double relativeYDiff = Math.cos(Math.toRadians(relativeAngleDiff)) * distanceToTarget;

        double divisor = Math.abs(relativeXDiff) + Math.abs(relativeYDiff);
        double xPower = relativeXDiff / divisor;
        double yPower = relativeYDiff / divisor;

        double rotPower = getAngleTurnPower(currentLocation.getHeading(), targetAngle, turnSpeed);

        mecanum.moveCustomScaling(yPower, xPower, rotPower, speed);

        if (telemetry != null) {
            telemetry.addData("Dist", distanceToTarget);
            telemetry.addData("Target Ang", targetAngle);
            telemetry.addData("Abs Ang", absoluteAngleDiff);
            telemetry.addData("Rel Ang", relativeAngleDiff);
            telemetry.addData("X Diff", relativeXDiff);
            telemetry.addData("Y Diff", relativeYDiff);
            telemetry.addData("X Power", xPower);
            telemetry.addData("Y Power", yPower);
            telemetry.addData("Rot Power", rotPower);
        }
    }

    public static void moveTowardPosition(Mecanum mecanum, Location currentLocation, Location target, double preferredAngle, double turnSpeed) {
        moveTowardPosition(mecanum, currentLocation, target, preferredAngle, turnSpeed, null);
    }

    public static void moveTowardPosition(Mecanum mecanum, Location currentLocation, Location target, double preferredAngle, double turnSpeed, Telemetry telemetry) {
        moveTowardPosition(mecanum, currentLocation, target, preferredAngle, turnSpeed, MOVEMENT_COEFFICIENT, telemetry);
    }

    public static void moveTowardPosition(Mecanum mecanum, Location currentLocation, Location target, double preferredAngle, double turnSpeed, double speed, Telemetry telemetry) {
        double distanceToTarget = Location.distanceSquared(currentLocation, target);
        double targetAngle; // Angle between points

        // Don't turn when very close.
        if (distanceToTarget < target.getPurePursuitDistanceStopTurning2())
            targetAngle = currentLocation.getHeading();
        else
            targetAngle = Location.angleBound(Location.angleLocations(currentLocation, target) + preferredAngle);

        moveTowardPositionAngle(mecanum, currentLocation, target, targetAngle, turnSpeed, speed, telemetry);
    }

    public static double getAngleTurnPower(double currentAngle, double targetAngle, double turnSpeed) {
        // If the angle difference is more than 15º, maximum power will be applied.
        // When it gets closer, the power will be gradually reduced.
        double angleTurnMagnitude = Location.angleTurn(currentAngle, targetAngle) / 15.0;
        return MinPower.minPower(Range.clip(angleTurnMagnitude, -.75, .75) * turnSpeed, Globals.rotationMinPower);
    }
}
