package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Odometry {
    // MILLIMETERS

    // FIXME GET THESE VALUES CORRECT
    static final double ticksPerRevolution = -1; //
    static final double wheelRadius = 30;
    static final double dPar = 100; // Distance between center of robot and parallel wheels.
    static final double dPer = -70; // Distance between center of robot and perpendicular wheel.

    static final double mmPerTick = 2 * Math.PI * wheelRadius / ticksPerRevolution;

    private DcMotor encoderLeft;
    private DcMotor encoderRight;
    private DcMotor encoderMid;

    private int previousLeft = 0;
    private int previousRight = 0;
    private int previousMid = 0;

    private boolean firstLoop = true;

    public Odometry(DcMotor encoderLeft, DcMotor encoderRight, DcMotor encoderMid) {
        this.encoderLeft = encoderLeft;
        this.encoderRight = encoderRight;
        this.encoderMid = encoderMid;
    }

    public Location calculateNewLocation(Location currentLocation) {
        int measureLeft = encoderLeft.getCurrentPosition();
        int measureRight = encoderRight.getCurrentPosition();
        int measureMid = encoderMid.getCurrentPosition();

        int diffLeft = measureLeft - previousLeft;
        int diffRight = measureRight - previousRight;
        int diffMid = measureMid - previousMid;

        previousLeft = measureLeft;
        previousRight = measureRight;
        previousMid = measureMid;

        // First time it's called, we can't do much.
        if (firstLoop) {
            firstLoop = false;
            return currentLocation;
        }

        double dTheta = mmPerTick * (diffRight - diffLeft) / dPar;
        double dX = mmPerTick * (diffRight + diffLeft) / 2.0;
        double dY = mmPerTick * (diffMid - (diffRight - diffLeft) * dPer / dPar);

        double thetaAvg = Math.toRadians(currentLocation.getHeading()) + (dTheta / 2);
        double cosTheta = Math.cos(thetaAvg);
        double sinTheta = Math.sin(thetaAvg);

        currentLocation.setX(currentLocation.getX() + dX * cosTheta - dY * sinTheta);
        currentLocation.setY(currentLocation.getY() + dX * sinTheta + dY * cosTheta);
        currentLocation.setHeading(Location.angleBound(currentLocation.getHeading() + Math.toDegrees(dTheta)));

        return currentLocation;
    }

}
