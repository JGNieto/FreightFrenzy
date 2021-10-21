package org.baylorschool.library;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

public class IMU {

    private double orientation;
    private double adjustment = 0;
    private Axis axis = Axis.Z;

    public enum Axis {
        X, Y, Z
    }

    private BNO055IMU imu;

    public IMU() {
        // FIXME: Initialize IMU
    }

    public void forceValue(double value) {
        adjustment = value - orientation;
    }

    public void updateOrientation() {
        orientation = getAngle(
                imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
        );
    }

    private double getAngle(Orientation angles) {
        if (axis == Axis.X)
            return angles.firstAngle;
        else if (axis == Axis.Y)
            return angles.secondAngle;
        else
            return angles.thirdAngle;
    }

    public BNO055IMU getImu() {
        return imu;
    }

    public double getHeading() {
        return Location.angleBound(orientation + adjustment);
    }
}
