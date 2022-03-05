package org.baylorschool.library.localization;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.debugging.DebuggingClient;
import org.baylorschool.library.debugging.DebuggingInformation;

import java.security.InvalidParameterException;

public class Odometry implements Localization {
    private final DcMotor encoderLeft;
    private final DcMotor encoderRight;
    private final DcMotor encoderMid;

    private ColorSensors colorSensors;

    private TouchSensors touchSensors;

    private final IMU imu;

    private boolean withdrawn = true;
    private long previousTime = 0;
    private long previousTimeDiff = 0; // For computing how frequently this code is ran.

    private final Servo servoLeft;
    private final Servo servoRight;
    private final Servo servoMiddle;

    private int previousLeft = 0;
    private int previousRight = 0;
    private int previousMid = 0;
    private double previousImu = 0;

    private boolean firstLoop = true;
    private static final int diffMidThreshold = 300;

    public double diff; // For debugging purposes.

    public Odometry(DcMotor encoderLeft, DcMotor encoderRight, DcMotor encoderMid, Servo servoLeft, Servo servoRight, Servo servoMiddle, IMU imu, boolean withdrawn) {
        this.encoderLeft = encoderLeft;
        this.encoderRight = encoderRight;
        this.encoderMid = encoderMid;

        this.servoLeft = servoLeft;
        this.servoRight = servoRight;
        this.servoMiddle = servoMiddle;

        this.imu = imu;

        if (withdrawn)
            this.withdraw();
        else
            this.open();

        this.reset();
    }

    public Odometry(HardwareMap hardwareMap, IMU imu, boolean withdrawn) {
        this.encoderLeft = hardwareMap.get(DcMotor.class, Globals.odometryEncoderLeft);
        this.encoderRight = hardwareMap.get(DcMotor.class, Globals.odometryEncoderRight);
        this.encoderMid = hardwareMap.get(DcMotor.class, Globals.odometryEncoderMiddle);

        this.servoLeft = hardwareMap.get(Servo.class, Globals.servoLeftHw);
        this.servoRight = hardwareMap.get(Servo.class, Globals.servoRightHw);
        this.servoMiddle = hardwareMap.get(Servo.class, Globals.servoMiddleHw);

        this.imu = imu;

        if (withdrawn)
            this.withdraw();
        else
            this.open();

        this.reset();
        this.setUpTouchSensors(hardwareMap);
        this.colorSensors = new ColorSensors(hardwareMap);
    }

    public Odometry(HardwareMap hardwareMap, boolean withdrawn) {
        this.encoderLeft = null;
        this.encoderRight = null;
        this.encoderMid = null;

        this.imu = null;

        this.servoLeft = hardwareMap.get(Servo.class, Globals.servoLeftHw);
        this.servoRight = hardwareMap.get(Servo.class, Globals.servoRightHw);
        this.servoMiddle = hardwareMap.get(Servo.class, Globals.servoMiddleHw);

        if (withdrawn)
            this.withdraw();
        else
            this.open();

        this.reset();
        this.setUpTouchSensors(hardwareMap);
    }

    private void setUpTouchSensors(HardwareMap hardwareMap) {
        this.touchSensors = new TouchSensors(hardwareMap);
    }

    public void reset() {
        this.previousLeft = 0;
        this.previousMid = 0;
        this.previousRight = 0;

        resetMotor(encoderLeft);
        resetMotor(encoderMid);
        resetMotor(encoderRight);
    }

    private void resetMotor(DcMotor dcMotor) {
        if (dcMotor == null) return;
        DcMotor.RunMode mode = dcMotor.getMode();

        dcMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        dcMotor.setMode(mode);
    }

    public Location calculateNewLocation(Location currentLocation) {
        imu.updateOrientation();

        double measureImu;
        double diffImu = 0;
        if (imu != null) {
            measureImu = imu.getHeading();
            diffImu = measureImu - previousImu;
            previousImu = measureImu;
        }

        int measureLeft = encoderLeft.getCurrentPosition() * encoderMultiplier(encoderLeft) * Globals.leftOdometryCoefficient;
        int measureRight = encoderRight.getCurrentPosition() * encoderMultiplier(encoderRight) * Globals.rightOdometryCoefficient;
        int measureMid = encoderMid.getCurrentPosition() * encoderMultiplier(encoderMid) * Globals.middleOdometryCoefficient;
        long measuredTime = System.nanoTime();

        int diffLeft = measureLeft - previousLeft;
        int diffRight = measureRight - previousRight;
        int diffMid = measureMid - previousMid;
        long timeDiff = measuredTime - previousTime;

        previousLeft = measureLeft;
        previousRight = measureRight;
        previousMid = measureMid;
        previousTime = measuredTime;
        previousTimeDiff = timeDiff;

        // First time it's called, we can't do much.
        if (firstLoop) {
            firstLoop = false;
            return currentLocation;
        }

        // Threshold
        diff = 1000000.0 * diffMid / (timeDiff / 1000.0);
        if (Math.abs(1000000.0 * diffMid / (timeDiff / 1000.0)) < diffMidThreshold) diffMid = 0;

        double dTheta;
        double dX = Globals.mmPerTick * (diffRight + diffLeft) / 2.0;
        double dY = Globals.mmPerTick * (diffMid - (diffRight - diffLeft) * Globals.dPer / Globals.dPar);

        if (imu != null) {
            currentLocation.setPitch(Location.angleBound(imu.getPitch()));
            dTheta = Math.toRadians(diffImu);
        } else {
            dTheta = Globals.mmPerTick * (diffRight - diffLeft) / Globals.dPar;
        }

        double thetaAvg = Math.toRadians(currentLocation.getHeading()) + (dTheta / 2);
        double cosTheta = Math.cos(thetaAvg);
        double sinTheta = Math.sin(thetaAvg);

        // https://en.wikipedia.org/wiki/Rotation_of_axes
        currentLocation.setX(currentLocation.getX() + dX * cosTheta - dY * sinTheta);
        currentLocation.setY(currentLocation.getY() + dX * sinTheta + dY * cosTheta);
        currentLocation.setHeading(Location.angleBound(currentLocation.getHeading() + Math.toDegrees(dTheta)));

        // Causes side effect and mutates currentLocation.
        touchSensors.computeLocation(currentLocation);
        colorSensors.computeLocation(currentLocation);

        DebuggingInformation.setLocation(currentLocation);
        DebuggingClient.getInstance().update();

        return currentLocation;
    }

    private int encoderMultiplier(DcMotor dcMotor) {
        return dcMotor.getDirection() == DcMotorSimple.Direction.REVERSE ? -1 : 1;
    }

    @Override
    public void setBackwards(boolean backwards) {
        if (backwards == true) {
            throw new InvalidParameterException("Cannot setBackwards to true on class Odometry.");
        }
    }

    @Override
    public boolean isBackwards() {
        return false;
    }

    public void withdraw() {
        this.withdrawn = true;
        moveServoNullSafe(servoLeft, Globals.positionWithdrawnLeft);
        moveServoNullSafe(servoRight, Globals.positionWithdrawnRight);
        moveServoNullSafe(servoMiddle, Globals.positionWithdrawnMiddle);
    }

    public void open() {
        this.withdrawn = false;
        moveServoNullSafe(servoLeft, Globals.positionOpenLeft);
        moveServoNullSafe(servoRight, Globals.positionOpenRight);
        moveServoNullSafe(servoMiddle, Globals.positionOpenMiddle);
    }

    // Moves servo if it is not null.
    public void moveServoNullSafe(Servo servo, double position) {
        if (servo != null)
            servo.setPosition(position);
    }

    public TouchSensors getTouchSensors() {
        return touchSensors;
    }

    public int getPreviousLeft() {
        return previousLeft;
    }

    public int getPreviousRight() {
        return previousRight;
    }

    public int getPreviousMid() {
        return previousMid;
    }

    public long getPreviousTimeDiff() {
        return previousTimeDiff;
    }

    public ColorSensors getColorSensors() {
        return colorSensors;
    }

    public void setColorSensors(ColorSensors colorSensors) {
        this.colorSensors = colorSensors;
    }
}
