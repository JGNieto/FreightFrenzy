package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Path;
import org.baylorschool.library.Sensors;
import org.baylorschool.library.math.CircleIntersect;
import org.baylorschool.library.math.PerpendicularDistance;

import java.util.ArrayList;
import java.util.List;

public class MovePurePursuit {

    // Each movement has three phases: Full speed, slipping with brakes on, and refinement.
    enum MovementState {
        FULL,
        SLIP,
        REFINE,
    }

    // Calculate pure pursuit points and move to them.
    public static Location movePurePursuit(Location currentLocation, Path path, LinearOpMode opMode, Odometry odometry, Mecanum mecanum) {
        // Ensure the first path line is calculated from the initial location.
        path.initialLocation(new Location(currentLocation));

        while (opMode.opModeIsActive()) {
            currentLocation = odometry.calculateNewLocation(currentLocation);

            if (Location.withinTolerance(currentLocation, path.getLastLocation(), path.getTolerance()))
                break;

            Location purePursuitTarget = getPurePursuitPoint(path, currentLocation);

            // TODO Different preferred angle and turnSpeed depending on segment.
            moveTowardPosition(mecanum, currentLocation, purePursuitTarget, 0, 1);
        }

        while (opMode.opModeIsActive() && !Location.angleWithinTolerance(currentLocation, path.getLastLocation(), path.getTolerance())) {
            mecanum.moveMecanum(0, 0, getAngleTurnPower(currentLocation.getHeading(), path.getLastLocation().getHeading(), 1));
        }

        return currentLocation;
    }

    public static Location getPurePursuitPoint(Path path, Location currentLocation) {
        if (path.getLocations().size() < 2) return null;

        Location championStartLocation = null;
        Location championEndLocation = null;
        double championDistance = -1;

        // To know where we are in the path, we calculate the shortest distance between the robot
        // and each line, and use it.
        for (int i = 1; i < path.getLocations().size() - 1; i++) {
            Location startLocation = path.getLocations().get(i);
            Location endLocation = path.getLocations().get(i + 1);

            double distance = PerpendicularDistance.getShortestDistanceBetweenPointAndLine(startLocation, endLocation, currentLocation);
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

        // double championDistance = -1; Reuse the championDistance value from before.
        // Its initial value does not matter: it will be written before it's read.
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
    public static void moveTowardPosition(Mecanum mecanum, Location currentLocation, Location target, double preferredAngle, double turnSpeed) {
        double distanceToTarget = Location.distance(currentLocation, target);
        double absoluteAngleDiff = Location.angleLocations(currentLocation, target); // Angle between points
        double relativeAngleDiff = Location.angleBound(absoluteAngleDiff - currentLocation.getHeading()); // Angle for the robot to turn.

        double relativeXDiff = Math.cos(Math.toRadians(relativeAngleDiff)) * distanceToTarget;
        double relativeYDiff = Math.sin(Math.toRadians(relativeAngleDiff)) * distanceToTarget;

        double xPower = relativeXDiff / (Math.abs(relativeXDiff) + Math.abs(relativeYDiff));
        double yPower = relativeYDiff / (Math.abs(relativeXDiff) + Math.abs(relativeYDiff));

        double rotPower = getAngleTurnPower(relativeAngleDiff, preferredAngle, turnSpeed);

        // Don't turn when very close.
        if (distanceToTarget < 50) rotPower = 0;

        mecanum.moveMecanum(yPower, xPower, rotPower);
    }

    public static double getAngleTurnPower(double currentAngle, double targetAngle, double turnSpeed) {
        // If the angle difference is more than 15º, maximum power will be applied.
        // When it gets closer, the power will be gradually reduced.
        double angleTurnMagnitude = Location.angleTurn(currentAngle, targetAngle) / 15;
        return Range.clip(angleTurnMagnitude, -1, 1) * turnSpeed;
    }
}
