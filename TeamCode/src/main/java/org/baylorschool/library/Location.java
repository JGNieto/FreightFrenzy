package org.baylorschool.library;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Location {

    private double x, y, z;
    private double roll, pitch, heading;

    private double purePursuitRadius = 250;

    private boolean backwards = false;

    public Location(OpenGLMatrix matrix) {
        VectorF translation = matrix.getTranslation();
        Orientation rotation = Orientation.getOrientation(matrix, EXTRINSIC, XYZ, DEGREES);
        this.x = translation.get(0);
        this.y = translation.get(1);
        this.z = translation.get(2);

        this.roll = rotation.firstAngle;
        this.pitch = rotation.secondAngle;
        this.heading = rotation.thirdAngle;
    }

    // For copying in memory instead of references.
    public Location(Location anotherLocation) {
        this.x = anotherLocation.x;
        this.y = anotherLocation.y;
        this.z = anotherLocation.z;
        this.roll = anotherLocation.roll;
        this.pitch = anotherLocation.pitch;
        this.heading = anotherLocation.heading;
        this.backwards = anotherLocation.backwards;
    }

    public Location(double x, double y, double z, double roll, double pitch, double heading) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.heading = heading;
    }

    public Location(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.z = 0;
        this.roll = 0;
        this.pitch = 0;
    }

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
        this.roll = 0;
        this.pitch = 0;
        this.heading = -1;
    }

    // Check if this location is the same place as another in x and y.
    public boolean samePlanePlaceAs(Location location) {
        return this.x == location.x && this.y == location.y;
    }

    /**
     * Adds location to telemetry
     * @param telemetry
     */
    public void reportTelemetry(Telemetry telemetry) {
        telemetry.addData("Pos (inches)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                x, y, z);
        telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", roll, pitch, heading);
    }

    /**
     * Absolute difference between two locations in
     * @param location1
     * @param location2
     * @return location whose values are the differences
     */
    public static Location difference(Location location1, Location location2) {
        return new Location(
                Math.abs(location1.getX() - location2.getX()),
                Math.abs(location1.getY() - location2.getY()),
                Math.abs(location1.getZ() - location2.getZ()),
                Math.abs(location1.getRoll() - location2.getRoll()),
                Math.abs(location1.getPitch() - location2.getPitch()),
                Math.abs(location1.getHeading() - location2.getHeading())
        );
    }

    /**
     * Whether the robot is within the acceptable tolerance
     * @param currentLocation
     * @param target
     * @param tolerance
     * @return whether it is within tolerance.
     */
    public static boolean withinTolerance(Location currentLocation, Location target, Location tolerance) {
        Location difference = difference(currentLocation, target);
        return difference.getX() < tolerance.getX() && difference.getY() < tolerance.getY();
    }

    public static boolean angleWithinTolerance(Location currentLocation, Location target, Location tolerance) {
        return Math.abs(Location.angleTurn(currentLocation.getHeading(), target.getHeading())) > tolerance.getHeading();
    }

    /**
     * Uses available sensors to make best guess of current location
     * @param currentLocation from previous iteration
     * @param vuforia if available; if not, use null
     * @param imu for heading
     * @param telemetry for vuforia (can be null if vuforia is too)
     * @param mecanum for encoders
     * @return best guess of location
     */
    public static Location updateLocation(Location currentLocation, Vuforia vuforia, IMU imu, Telemetry telemetry, Mecanum mecanum) {
        // Use vuforia if available
        imu.updateOrientation();
        Location vuforiaLocation = vuforia == null ? null : vuforia.lookForTargets(telemetry);

        // Reset current location if vuforia has target in sight
        if (vuforiaLocation != null) {
            imu.forceValue(vuforiaLocation.heading);
            return vuforiaLocation;
        }

        currentLocation.setHeading(imu.getHeading());

        // Calculate movement from wheel encoders
        // Movement is a vector, which we break down into x and y components with math
        double movementModulus = (
                mecanum.getLatestDeltaBl() +
                mecanum.getLatestDeltaBr() +
                mecanum.getLatestDeltaFl() +
                mecanum.getLatestDeltaFr()
        ) / 4;
        double headingRadians = Math.toRadians(currentLocation.getHeading());

        double movementX = movementModulus * Math.cos(headingRadians);
        double movementY = movementModulus * Math.sin(headingRadians);

        currentLocation.setX(currentLocation.getX() + movementX);
        currentLocation.setY(currentLocation.getY() + movementY);

        return currentLocation;
    }

    /**
     * Check whether the robot is pointing in the right direction
     * @param expectedValue in degrees
     * @param tolerance in degrees
     * @return
     */
    public boolean rotationTolerance(double expectedValue, double tolerance) {
        double error = Math.abs(angleTurn(heading, expectedValue));
        return error < tolerance;
    }

    /**
     * Angle between two locations
     * @param locationStart
     * @param locationTarget
     * @return angle in degrees
     */
    public static double angleLocations(Location locationStart, Location locationTarget) {
        double deltaX = locationTarget.getX() - locationStart.getX();
        double deltaY = locationTarget.getY() - locationStart.getY();

        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    /**
     * How many degrees to go from one heading to another and in which way
     * Positive is counterclockwise and negative clockwise.
     * Angles between -180 and 180 degrees.
     * @param current angle
     * @param target angle
     * @return number of degrees to turn
     */
    public static double angleTurn(double current, double target) {
        // TODO: Check if there is a more efficient way to do this. This looks quite spaghetti.
        double val;
        int sign = 1;
        if (Math.abs((target - current) % 360) < 180) {
            val = (target - current) % 360;
        } else {
            if (target - current > 0) sign = -1;
            val = (360 - Math.abs(target - current)) % 360;
        }
        return -(val * sign);
    }

    /**
     * Adds or subtracts a certain distance based on location and heading.
     * @param location to which add or subtract to
     * @param x added or subtracted
     * @param y added or subtracted
     * @return modified location
     */
    public static Location moveLocation(Location location, double x, double y) {
        // TODO: Double check this code to ensure it is correct.
        double angle = Math.toRadians(location.getHeading());
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        double deltaX = (x * sinAngle) + (y * cosAngle);
        double deltaY = (x * cosAngle) + (y * sinAngle);

        location.setX(location.getX() + deltaX);
        location.setY(location.getY() + deltaY);
        return location;
    }

    /**
     * Makes the angle be between -180 and 180.
     * @param angle
     * @return bound angle
     */
    public static double angleBound(double angle) {
        angle = angle % 360;
        if (angle > 180)
            return - 180 + (angle - 180);
        else if (angle < -180)
            return 180 + (angle + 180);
        else return angle;
    }

    /**
     * 2D distance between two locations
     * @return distance in mm
     */
    public static double distance(Location location1, Location location2) {
        double deltaX = Math.abs(location1.getX() - location2.getX());
        double deltaY = Math.abs(location1.getY() - location2.getY());

        return Math.hypot(deltaX, deltaY);
    }

    public Location backwards() {
        this.backwards = true;
        return this;
    }

    public Location forward() {
        this.backwards = false;
        return this;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getRoll() {
        return roll;
    }

    public double getPitch() {
        return pitch;
    }

    public double getHeading() {
        return heading;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public boolean isBackwards() {
        return backwards;
    }

    public double getPurePursuitRadius() {
        return purePursuitRadius;
    }

    public void setPurePursuitRadius(double purePursuitRadius) {
        this.purePursuitRadius = purePursuitRadius;
    }
}
