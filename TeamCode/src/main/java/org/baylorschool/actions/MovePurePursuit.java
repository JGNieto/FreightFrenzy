package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Path;
import org.baylorschool.library.math.CircleIntersect;
import org.baylorschool.library.math.PerpendicularDistance;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public class MovePurePursuit {

    // Each movement has three phases: Full speed, slipping with brakes on, and refinement.
    enum MovementState {
        FULL,
        SLIP,
        REFINE,
    }

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

        while (opMode.opModeIsActive()) {
            // Calculate the next location using the Pure Pursuit algorithm.
            currentLocation = odometry.calculateNewLocation(currentLocation);

            // Check whether we are within an acceptable distance to the final location. If so, movement is done.
            if (Location.withinTolerance(currentLocation, path.getLastLocation(), path.getTolerance()))
                break;

            // Compute target "lookahead" location according to Pure Pursuit.
            Location purePursuitTarget = getPurePursuitPoint(path, currentLocation);

            // If we are not near any path, go towards the first point of the path.
            if (purePursuitTarget == null) {
                moveTowardPosition(mecanum, currentLocation, path.getLocations().get(0), 0, 1);
            } else {
                moveTowardPositionAngle(mecanum, currentLocation, purePursuitTarget, purePursuitTarget.getHeading(), purePursuitTarget.getPurePursuitTurnSpeed());
            }
        }

        // Perfect angle.
        while (opMode.opModeIsActive()) {
            // If angle is good enough, stop movement.
            if (Location.angleWithinTolerance(currentLocation, path.getLastLocation(), path.getTolerance()))
                break;
            // Move towards correct angle.
            mecanum.moveMecanum(0, 0, getAngleTurnPower(currentLocation.getHeading(), path.getLastLocation().getHeading(), 1));
        }

        return currentLocation;
    }

    /**
     * Computes the best "lookahead" location from Pure Pursuit.
     * @param path Path of the robot.
     * @param currentLocation Current location of the robot.
     * @return Computed location or null if the robot is not near any segments.
     */
    public static Location getPurePursuitPoint(Path path, Location currentLocation) {
        if (path.getLocations().size() < 2) return null;

        Location championStartLocation = null;
        Location championEndLocation = null;
        double championDistance = -1;

        // To know where we are in the path, we calculate the shortest distance between the robot
        // and each segment, and use it.
        // TODO: Instead of an infinite line, add check to use only the segments.

        // for (int i = 1; i < path.getLocations().size() - 1; i++) {
        for (int i = 0; i < path.getLocations().size() - 1; i++) {
            Location startLocation = path.getLocations().get(i);
            Location endLocation = path.getLocations().get(i + 1);

            double distance = PerpendicularDistance.getShortestDistanceBetweenPointAndSegment(startLocation, endLocation, currentLocation);
            if (distance < championDistance || championDistance == -1) {
                championStartLocation = startLocation;
                championEndLocation = endLocation;
                championDistance = distance;
            }
        }

        // Get intersections for the optimal line.
        List<Location> intersections = CircleIntersect.getCircleLineIntersectionLocation(championStartLocation, championEndLocation, currentLocation, championEndLocation.getPurePursuitRadius());

        // Edge case: if very close to final target, go straight to it.
        // We do this after loop to avoid cutting a corner we didn't want to.
        Location lastLocation = path.getLastLocation();
        if (championEndLocation == lastLocation && Location.distance(championEndLocation, lastLocation) < 100) {
            return lastLocation;
        }

        // Get the most advanced point in the path (shortest distance to next point).

        // We reuse the championDistance variable from before, since its initial value does not matter.

        // Champion intersection is initialized as null so that null is returned if no intersections exist.
        Location championIntersection = null;

        for (Location intersection : intersections) {
            double distance = Location.distance(intersection, championEndLocation);
            if (championIntersection == null || championDistance < distance) {
                championIntersection = intersection;
                championDistance = distance;
            }
        }

        return championIntersection;
    }

    // The purpose of this function is to move the robot towards the desired position, regardless
    // of the path algorithm (i.e. regardless of Pure Pursuit or others).
    public static void moveTowardPositionAngle(Mecanum mecanum, Location currentLocation, Location target, double targetAngle, double turnSpeed) {
        double distanceToTarget = Location.distance(currentLocation, target);
        double absoluteAngleDiff = Location.angleLocations(currentLocation, target); // Angle between points.
        double relativeAngleDiff = Location.angleBound(absoluteAngleDiff - currentLocation.getHeading()); // Angle for the robot to turn.

        double relativeXDiff = Math.cos(Math.toRadians(relativeAngleDiff)) * distanceToTarget;
        double relativeYDiff = Math.sin(Math.toRadians(relativeAngleDiff)) * distanceToTarget;

        double xPower = relativeXDiff / (Math.abs(relativeXDiff) + Math.abs(relativeYDiff));
        double yPower = relativeYDiff / (Math.abs(relativeXDiff) + Math.abs(relativeYDiff));

        double rotPower = 0;

        // Don't turn when very close.
        if (distanceToTarget > 50) rotPower = getAngleTurnPower(currentLocation.getHeading(), targetAngle, turnSpeed);

        mecanum.moveMecanum(yPower, xPower, rotPower);
    }

    public static void moveTowardPositionAngle(Mecanum mecanum, Location currentLocation, Location target, double targetAngle, double turnSpeed, Telemetry telemetry) {
        double distanceToTarget = Location.distance(currentLocation, target);
        double absoluteAngleDiff = Location.angleLocations(currentLocation, target); // Angle between points.
        double relativeAngleDiff = Location.angleBound(absoluteAngleDiff - currentLocation.getHeading()); // Angle for the robot to turn.

        // Original:
        // double relativeXDiff = Math.sin(Math.toRadians(relativeAngleDiff)) * distanceToTarget;
        // double relativeYDiff = Math.cos(Math.toRadians(relativeAngleDiff)) * distanceToTarget;

        // Testing
        double relativeXDiff = Math.cos(Math.toRadians(relativeAngleDiff)) * distanceToTarget;
        double relativeYDiff = Math.sin(Math.toRadians(relativeAngleDiff)) * distanceToTarget;

        double xPower = relativeXDiff / (Math.abs(relativeXDiff) + Math.abs(relativeYDiff));
        double yPower = relativeYDiff / (Math.abs(relativeXDiff) + Math.abs(relativeYDiff));

        double rotPower = 0;

        // Don't turn when very close.
        if (distanceToTarget > 50) rotPower = getAngleTurnPower(currentLocation.getHeading(), targetAngle, turnSpeed);

        mecanum.moveMecanum(yPower, xPower, rotPower);

        telemetry.addData("Dist", distanceToTarget);
        telemetry.addData("Abs Ang", absoluteAngleDiff);
        telemetry.addData("Rel Ang", relativeAngleDiff);
        telemetry.addData("X Diff", relativeXDiff);
        telemetry.addData("Y Diff", relativeYDiff);
        telemetry.addData("X Power", xPower);
        telemetry.addData("Y Power", yPower);
    }


    public static void moveTowardPosition(Mecanum mecanum, Location currentLocation, Location target, double preferredAngle, double turnSpeed) {
        double distanceToTarget = Location.distance(currentLocation, target);
        double targetAngle; // Angle between points

        // Don't turn when very close.
        if (distanceToTarget < 50)
            targetAngle = currentLocation.getHeading();
        else
            targetAngle = Location.angleBound(Location.angleLocations(currentLocation, target) + preferredAngle);

        moveTowardPositionAngle(mecanum, currentLocation, target, targetAngle, turnSpeed);
    }

    public static double getAngleTurnPower(double currentAngle, double targetAngle, double turnSpeed) {
        // If the angle difference is more than 15º, maximum power will be applied.
        // When it gets closer, the power will be gradually reduced.
        double angleTurnMagnitude = Location.angleTurn(currentAngle, targetAngle) / 15;
        return Range.clip(angleTurnMagnitude, -1, 1) * turnSpeed;
    }
}
