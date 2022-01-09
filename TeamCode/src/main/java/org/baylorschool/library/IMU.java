package org.baylorschool.library;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.baylorschool.Globals;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class IMU {

    private double orientation;
    private double adjustment = 0;
    private boolean backwards = false;

    private Orientation allAngles;

    public enum Axis {
        X, Y, Z
    }

    private BNO055IMU imu;

    public void initializeImu(HardwareMap hardwareMap) {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    public void forceValue(double value) {
        adjustment = value - orientation;
    }

    public void updateOrientation() {
        allAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        orientation = getAngle(allAngles);
    }

    private double getAngle(Orientation angles) {
        if (Globals.imuRotationAxis == Axis.X)
            return angles.firstAngle;
        else if (Globals.imuRotationAxis == Axis.Y)
            return angles.secondAngle;
        else if (Globals.imuRotationAxis == Axis.Z)
            return angles.thirdAngle;
        else throw new IllegalArgumentException("Globals.imuRotationAxis must be X, Y or Z.");
    }

    public BNO055IMU getImu() {
        return imu;
    }

    public double getHeading() {
        return Location.angleBound(orientation + adjustment + (backwards ? 180 : 0));
    }

    public double getPitch() {
        if (Globals.imuPitchAxis == Axis.X)
            return allAngles.firstAngle;
        else if (Globals.imuPitchAxis == Axis.Y)
            return allAngles.secondAngle;
        else if (Globals.imuPitchAxis == Axis.Z)
            return allAngles.thirdAngle;
        else throw new IllegalArgumentException("Globals.imuPitchAxis must be X, Y or Z.");
    }

    public boolean isBackwards() {
        return backwards;
    }

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }
}
