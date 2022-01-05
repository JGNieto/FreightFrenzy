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
        orientation = getAngle(
                imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
        );
    }

    private double getAngle(Orientation angles) {
        if (Globals.imuAxis == Axis.X)
            return angles.firstAngle;
        else if (Globals.imuAxis == Axis.Y)
            return angles.secondAngle;
        else if (Globals.imuAxis == Axis.Z)
            return angles.thirdAngle;
        else throw new IllegalArgumentException("Globals.imuAxis must be X, Y or Z.");
    }

    public BNO055IMU getImu() {
        return imu;
    }

    public double getHeading() {
        return Location.angleBound(orientation + adjustment + (backwards ? 180 : 0));
    }

    public boolean isBackwards() {
        return backwards;
    }

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }
}
