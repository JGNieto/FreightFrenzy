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

    /**
     * Adds location to telemetry
     * @param telemetry
     */
    public void reportTelemtry(Telemetry telemetry) {
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
}
