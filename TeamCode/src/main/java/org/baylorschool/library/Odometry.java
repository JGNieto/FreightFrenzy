package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Odometry {
    // MILLIMETERS

    // FIXME GET THESE VALUES CORRECT
    static final double ticksPerRevolution = -1;
    static final double wheelRadius = 30;
    static final double dPar = 100; // Distance between center of robot and parallel wheels.
    static final double dPer = -70; // Distance between center of robot and perpendicular wheel.

    // Servo position value for the respective positions.
    static final double positionWithdrawn = 0;
    static final double positionOpen = 0.8;

    static final double mmPerTick = 2 * Math.PI * wheelRadius / ticksPerRevolution;

    private DcMotor encoderLeft;
    private DcMotor encoderRight;
    private DcMotor encoderMid;

    private boolean withdrawn = true;

    private Servo servoLeft;
    private Servo servoRight;
    private Servo servoMid;

    private int previousLeft = 0;
    private int previousRight = 0;
    private int previousMid = 0;

    private boolean firstLoop = true;

    public Odometry(DcMotor encoderLeft, DcMotor encoderRight, DcMotor encoderMid, Servo servoLeft, Servo servoRight, Servo servoMid, boolean withdrawn) {
        this.encoderLeft = encoderLeft;
        this.encoderRight = encoderRight;
        this.encoderMid = encoderMid;

        this.servoLeft = servoLeft;
        this.servoRight = servoRight;
        this.servoMid = servoMid;

        if (withdrawn)
            this.withdraw();
        else
            this.open();
    }

    public Location calculateNewLocation(Location currentLocation) {
        if (withdrawn) // Can't do much if wheels are up. Don't want to throw exception to avoid crash.
            return currentLocation;

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

    public void withdraw() {
        this.withdrawn = true;
        moveServoNullSafe(servoLeft, positionWithdrawn);
        moveServoNullSafe(servoRight, positionWithdrawn);
        moveServoNullSafe(servoMid, positionWithdrawn);
    }

    public void open() {
        this.withdrawn = false;
        moveServoNullSafe(servoLeft, positionOpen);
        moveServoNullSafe(servoRight, positionOpen);
        moveServoNullSafe(servoMid, positionOpen);
    }

    // Moves servo if it is not null.
    public void moveServoNullSafe(Servo servo, double position) {
        if (servo != null)
            servo.setPosition(position);
    }

}
