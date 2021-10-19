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

    public void reportTelemtry(Telemetry telemetry) {
        telemetry.addData("Pos (inches)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                x, y, z);
        telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", roll, pitch, heading);
    }

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

    public static Location updateLocation(Location currentLocation, Vuforia vuforia, IMU imu, Telemetry telemetry) {
        Location vuforiaLocation = vuforia.lookForTargets(telemetry);
        imu.updateOrientation();
        if (vuforiaLocation != null) {
            imu.forceValue(vuforiaLocation.heading);
            return vuforiaLocation;
        }
        currentLocation.setHeading(imu.getHeading());
        return currentLocation;
    }

    /**
     * Makes the angle be between -180 and 180.
     * @param angle
     * @return
     */
    public static double angleBound(double angle) {
        if (angle > 180)
            return -180 + (angle - 180);
        else if (angle < -180)
            return 180 + (angle + 180);
        else return angle;
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
}
