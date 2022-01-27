package org.baylorschool.library;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

import org.baylorschool.Globals;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class Location {

    private double x, y, z;
    private double roll, pitch, heading;

    private double purePursuitRadius = Globals.defaultPurePursuitRadius;
    private double purePursuitTurnSpeed = Globals.defaultPurePursuitTurnSpeed;
    private double purePursuitDistanceStopTurning = Globals.defaultPurePursuitDistanceStopTurning;

    private Runnable runnable = null;

    private boolean backwards = false;

    // Constructor to be used by Vuforia.
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

    // Constructor for actually copying in memory instead of dealing with references.
    public Location(Location that) {
        this.x = that.x;
        this.y = that.y;
        this.z = that.z;
        this.roll = that.roll;
        this.pitch = that.pitch;
        this.heading = that.heading;
        this.backwards = that.backwards;
        this.purePursuitRadius = that.purePursuitRadius;
    }

    // Explicit constructor.
    public Location(double x, double y, double z, double roll, double pitch, double heading) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.heading = heading;
    }

    // Simplified constructor.
    public Location(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.z = 0;
        this.roll = 0;
        this.pitch = 0;
    }

    // Simplified constructor.
    public Location(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
        this.roll = 0;
        this.pitch = 0;
        this.heading = -1;
    }

    /**
     * Checks whether this location is the same place as another in x and y.
     * @param location The location to compare with this one.
     * @return Whether x and y are the same in both locations.
     */
    public boolean samePlanePlaceAs(Location location) {
        return this.x == location.x && this.y == location.y;
    }

    /**
     * Adds location to telemetry.
     * @param telemetry Telemetry instance.
     */
    public void reportTelemetry(Telemetry telemetry) {
        telemetry.addData("Pos (inches)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                x, y, z);
        telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", roll, pitch, heading);
    }

    /**
     * Absolute difference between two locations in
     * @param location1 The first location.
     * @param location2 The second location.
     * @return A new Location instance whose values are the absolute differences.
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
     * Whether the robot is within the acceptable tolerance of a target location in x and y.
     * @param currentLocation Current location of the robot.
     * @param target Target location to check.
     * @param tolerance Location instance where x and y are the tolerances for those values.
     * @return whether it is within tolerance.
     */
    public static boolean withinTolerance(Location currentLocation, Location target, Location tolerance) {
        Location difference = difference(currentLocation, target);
        return difference.getX() < tolerance.getX() && difference.getY() < tolerance.getY();
    }

    /**
     * Whether a location's heading is within that of a target location.
     * @param currentLocation Location of the robot.
     * @param target Target location.
     * @param tolerance Location instance where heading is the tolerance for the value.
     * @return whether it is within tolerance.
     */
    public static boolean angleWithinTolerance(Location currentLocation, Location target, Location tolerance) {
        return Math.abs(Location.angleTurn(currentLocation.getHeading(), target.getHeading())) > tolerance.getHeading();
    }

    /**
     * Uses available sensors to make best guess of current location
     * @param currentLocation from previous iteration.
     * @param vuforia if available; if not, use null
     * @param imu for heading
     * @param telemetry for vuforia (can be null if vuforia is too).
     * @param mecanum for encoders
     * @return best guess of location
     */
    public static Location updateLocation(Location currentLocation, Vuforia vuforia, IMU imu, Telemetry telemetry, Mecanum mecanum) {
        // Update IMU first to make it be as close as possible in time to the data from the encoders.
        imu.updateOrientation();

        // Use vuforia if available
        Location vuforiaLocation = vuforia == null ? null : vuforia.lookForTargets(telemetry);

        // Reset current location if vuforia has target in sight
        if (vuforiaLocation != null) {
            imu.forceValue(vuforiaLocation.heading);
            return vuforiaLocation;
        }

        // Get heading from IMU.
        currentLocation.setHeading(imu.getHeading());

        // Compute movement from wheel encoders.
        // Movement is a vector, which we break down into x and y components with math.
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
     * Check whether the robot is pointing in the right direction (uses heading of this instance).
     * @param expectedValue in degrees
     * @param tolerance in degrees
     * @return whether difference is within tolerance.
     */
    public boolean rotationTolerance(double expectedValue, double tolerance) {
        double error = Math.abs(angleTurn(this.heading, expectedValue));
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

        return angleBound(Math.toDegrees(Math.atan2(deltaY, deltaX)));
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
     * @param location1 The first location.
     * @param location2 The second location.
     * @return distance in mm
     */
    public static double distance(Location location1, Location location2) {
        double deltaX = Math.abs(location1.getX() - location2.getX());
        double deltaY = Math.abs(location1.getY() - location2.getY());

        return Math.hypot(deltaX, deltaY);
    }

    /**
     * 2D distance squared between two locations to avoid SQRT.
     * @param location1 The first location.
     * @param location2 The second location.
     * @return distance squared in mm^2
     */
    public static double distanceSquared(Location location1, Location location2) {
        double deltaX = Math.abs(location1.getX() - location2.getX());
        double deltaY = Math.abs(location1.getY() - location2.getY());

        return deltaX * deltaX + deltaY * deltaY;
    }

    // Setters return this so that method calls can be chained in a single line. For example:
    // Location location = new Location(50, 50).backwards().moveTurn(30, .5);
    //                                          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

    public Location setX(double x) {
        this.x = x;
        return this;
    }

    public Location setY(double y) {
        this.y = y;
        return this;
    }

    public Location setZ(double z) {
        this.z = z;
        return this;
    }

    public Location setRoll(double roll) {
        this.roll = roll;
        return this;
    }

    public Location setPitch(double pitch) {
        this.pitch = pitch;
        return this;
    }

    public Location setHeading(double heading) {
        this.heading = heading;
        return this;
    }

    public boolean isBackwards() {
        return backwards;
    }

    public double getPurePursuitRadius() {
        return purePursuitRadius;
    }

    public Location setPurePursuitRadius(double purePursuitRadius) {
        this.purePursuitRadius = purePursuitRadius;
        return this;
    }

    public double getPurePursuitTurnSpeed() {
        return purePursuitTurnSpeed;
    }

    public Location setPurePursuitTurnSpeed(double purePursuitTurnSpeed) {
        this.purePursuitTurnSpeed = purePursuitTurnSpeed;
        return this;
    }

    // Make robot turn while it moves in Pure Pursuit.
    public Location moveTurn(double angle, double speed) {
        this.purePursuitTurnSpeed = speed;
        this.heading = angle;

        return this;
    }

    public Location moveTurn(double angle) {
        this.purePursuitTurnSpeed = 1;
        this.heading = angle;

        return this;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    /**
     * You probably want to use getPurePursuitDistanceStopTurning2(), with a "2" at the end.
     */
    @Deprecated
    public double getPurePursuitDistanceStopTurning() {
        return purePursuitDistanceStopTurning;
    }

    // Same value, squared
    public double getPurePursuitDistanceStopTurning2() {
        return purePursuitDistanceStopTurning * purePursuitDistanceStopTurning;
    }

    public Location setPurePursuitDistanceStopTurning(double purePursuitDistanceStopTurning) {
        this.purePursuitDistanceStopTurning = purePursuitDistanceStopTurning;
        return this;
    }
}
