package org.baylorschool.library;

import static com.qualcomm.robotcore.util.Range.scale;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Mecanum {

    private DcMotor frMotor;
    private DcMotor brMotor;
    private DcMotor flMotor;
    private DcMotor blMotor;

    private double latestDeltaFr = 0;
    private double latestDeltaFl = 0;
    private double latestDeltaBr = 0;
    private double latestDeltaBl = 0;

    private int lastReadingFr = 0;
    private int lastReadingFl = 0;
    private int lastReadingBr = 0;
    private int lastReadingBl = 0;

    private Side reverse;

    // TODO: THIS IS A PATCH, DO BACKWARDS
    private boolean backwards = false;

    private DcMotor.RunMode runMode;

    private static final double ticksPerRevolution = 537.7;
    private static final double wheelDiameter = 100; // In millimeters
    public static final double ticksPerMm = ticksPerRevolution / (Math.PI * wheelDiameter);
    private static final double autonomousSpeed = 0.3;

    // Encoder ticks to rotate 360 degrees.
    private static final double fullTurnEncoderCountFL = 2975;
    private static final double fullTurnEncoderCountFR = 3100;
    private static final double fullTurnEncoderCountBL = 3525;
    private static final double fullTurnEncoderCountBR = 3420;

    // In mm, the distance between two diagonally opposed wheels.
    // (also twice the distance of any wheel from the center of the robot)
    public static final double turningDiameter = 540;

    public enum Side {
        LEFT,
        RIGHT,
        NONE,
        BOTH
    }

    /**
     * Class for managing four mecanum wheels.
     * @param hardwareMap for retrieving motors.
     * @param reverse Side of the robot with wheels reversed
     * @param blMotor Back left motor name in config file.
     * @param flMotor Front left motor name in config file.
     * @param brMotor Back right motor name in config file.
     * @param frMotor Front right motor name in config file.
     */
    public Mecanum(HardwareMap hardwareMap, Side reverse, String blMotor, String flMotor,
                   String brMotor, String frMotor) {
        this.blMotor = hardwareMap.get(DcMotor.class, blMotor);
        this.flMotor = hardwareMap.get(DcMotor.class, flMotor);
        this.brMotor = hardwareMap.get(DcMotor.class, brMotor);
        this.frMotor = hardwareMap.get(DcMotor.class, frMotor);

        setReverse(reverse);
        resetEncoders();
    }
    /**
     * Class for managing four mecanum wheels.
     * Constructor with default values.
     * @param hardwareMap for retrieving motors.
     */
    public Mecanum(HardwareMap hardwareMap) {
        this.blMotor = hardwareMap.get(DcMotor.class, "blMotor");
        this.flMotor = hardwareMap.get(DcMotor.class, "flMotor");
        this.brMotor = hardwareMap.get(DcMotor.class, "brMotor");
        this.frMotor = hardwareMap.get(DcMotor.class, "frMotor");

        setReverse(Side.RIGHT);
        resetEncoders();
    }

    /**
     * Changes the side that is reverse.
     * @param reverse Side that requires reversing.
     */
    public void setReverse(Side reverse) {
        this.reverse = reverse;
        if (reverse.equals(Side.LEFT)) {
            frMotor.setDirection(DcMotor.Direction.FORWARD);
            brMotor.setDirection(DcMotor.Direction.FORWARD);
            flMotor.setDirection(DcMotor.Direction.REVERSE);
            blMotor.setDirection(DcMotor.Direction.REVERSE);
        } else if (reverse.equals(Side.RIGHT)) {
            frMotor.setDirection(DcMotor.Direction.REVERSE);
            brMotor.setDirection(DcMotor.Direction.REVERSE);
            flMotor.setDirection(DcMotor.Direction.FORWARD);
            blMotor.setDirection(DcMotor.Direction.FORWARD);
        } else if (reverse.equals(Side.BOTH)) {
            frMotor.setDirection(DcMotor.Direction.REVERSE);
            brMotor.setDirection(DcMotor.Direction.REVERSE);
            flMotor.setDirection(DcMotor.Direction.REVERSE);
            blMotor.setDirection(DcMotor.Direction.REVERSE);
        } else if (reverse.equals(Side.NONE)) {
            frMotor.setDirection(DcMotor.Direction.FORWARD);
            brMotor.setDirection(DcMotor.Direction.FORWARD);
            flMotor.setDirection(DcMotor.Direction.FORWARD);
            blMotor.setDirection(DcMotor.Direction.FORWARD);
        }
    }

    /**
     * Sets the run mode of all motors
     * @param mode
     */
    public void setMode(DcMotor.RunMode mode) {
        if (mode == DcMotor.RunMode.STOP_AND_RESET_ENCODER) {
            resetEncoderReadings();
        }

        frMotor.setMode(mode);
        brMotor.setMode(mode);
        flMotor.setMode(mode);
        blMotor.setMode(mode);
        this.runMode = mode;
    }

    /**
     * Resets the encoders by changing their mode twice
     */
    public void resetEncoders() {
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        resetEncoderReadings();
    }


    /**
     * Position to which wheels with encoders will move
     * @param targetPosition in ticks
     * @param dcMotor
     */
    public void setTargetPosition(int targetPosition, DcMotor dcMotor) {
        dcMotor.setTargetPosition(-(targetPosition + dcMotor.getCurrentPosition()));
    }

    /**
     * Position to which wheels with encoders will move
     * @param targetDistance in millimeters
     */
    public void setTargetDistance(double targetDistance) {
        setTargetDistance(targetDistance, frMotor);
        setTargetDistance(targetDistance, flMotor);
        setTargetDistance(targetDistance, brMotor);
        setTargetDistance(targetDistance, blMotor);
    }

    /**
     * Position to which wheels with encoders will move
     * @param targetDistance in millimeters
     * @param dcMotor
     */
    public void setTargetDistance(double targetDistance, DcMotor dcMotor) {
        setTargetPosition(Math.round((float) (targetDistance * ticksPerMm)), dcMotor);
    }

    /**
     * Rotates the amount specified
     * @param angle in degrees
     */
    public void rotate(double angle) {
        // Distance that each wheel has to travel.
        //double arch = Math.PI * turningDiameter * (angle / 360);
        int archFL = (int) (fullTurnEncoderCountFL * (angle/360));
        int archFR = (int) (fullTurnEncoderCountFR * (angle/360));
        int archBL = (int) (fullTurnEncoderCountBL * (angle/360));
        int archBR = (int) (fullTurnEncoderCountBR * (angle/360));
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setTargetPosition(-archFR, frMotor);
        setTargetPosition(-archBR, brMotor);
        setTargetPosition(archFL, flMotor);
        setTargetPosition(archBL, blMotor);

        setPowerAutonomous();
    }

    /**
     * Sets power of all motors to that specified by the static variable autonomousSpeed
     */
    public void setPowerAutonomous() {
        setPower(autonomousSpeed);
    }

    /**
     * Sets power of all 4 motors
     * @param power from -1 to 1
     */
    public void setPower(double power) {
        frMotor.setPower(power);
        flMotor.setPower(power);
        blMotor.setPower(power);
        brMotor.setPower(power);
    }

    /**
     * Retrieves encoder values from the motor, calculates deltas and stores them.
     */
    public void updateEncoderReadings() {
        int frReading = frMotor.getCurrentPosition() * -1;
        int flReading = flMotor.getCurrentPosition() * -1;
        int brReading = brMotor.getCurrentPosition() * -1;
        int blReading = blMotor.getCurrentPosition() * -1;

        latestDeltaFr = (frReading - lastReadingFr) / ticksPerMm;
        latestDeltaFl = (flReading - lastReadingFl) / ticksPerMm;
        latestDeltaBr = (brReading - lastReadingBr) / ticksPerMm;
        latestDeltaBl = (blReading - lastReadingBl) / ticksPerMm;

        lastReadingFr = frReading;
        lastReadingFl = flReading;
        lastReadingBr = brReading;
        lastReadingBl = blReading;
    }

    /**
     * Assumes all motors' readings are 0
     */
    private void resetEncoderReadings() {
        lastReadingFr = 0;
        lastReadingFl = 0;
        lastReadingBr = 0;
        lastReadingBl = 0;
    }

    /**
     * Checks if any of the motors is busy
     * @return isBusy
     */
    public boolean isBusy() {
        return frMotor.isBusy() || flMotor.isBusy() || brMotor.isBusy() || blMotor.isBusy();
    }

    public void moveMecanum(double y, double x, double rotation) {
        moveGamepad(-y, x, rotation, 1);
    }

    public void stop() {
        setPower(0);
    }

    /**
     * Changes motion of robot
     * @param y forward / backward power (1 to -1)
     * @param x left / right power (-1 to 1)
     * @param rotation left / right power (-1 to 1)
     */
    public void moveGamepad(double y, double x, double rotation, double motorCoefficient) {
        /*
            Taken from
            https://ftcforum.firstinspires.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
         */
        double rot = -rotation;
        double Magnitude = abs(x) + abs(rot) + abs(y);
        Magnitude = (Magnitude > 1) ? Magnitude : 1;

        flMotor.setPower(scale((scaleInput(y) + scaleInput(rot) - scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1) * motorCoefficient);
        blMotor.setPower(scale((scaleInput(y) + scaleInput(rot) + scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1) * motorCoefficient);
        frMotor.setPower(scale((scaleInput(y) - scaleInput(rot) + scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1) * motorCoefficient);
        brMotor.setPower(scale((scaleInput(y) - scaleInput(rot) - scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1) * motorCoefficient);
    }

    public void setBackwards(boolean backwards) {
        if (this.backwards != backwards) {
            if (reverse == Side.LEFT) setReverse(Side.RIGHT);
            else if (reverse == Side.RIGHT) setReverse(Side.LEFT);
            else if (reverse == Side.NONE) setReverse(Side.BOTH);
            else setReverse(Side.NONE);
            resetEncoders();
        }
        this.backwards = backwards;
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        frMotor.setZeroPowerBehavior(zeroPowerBehavior);
        flMotor.setZeroPowerBehavior(zeroPowerBehavior);
        blMotor.setZeroPowerBehavior(zeroPowerBehavior);
        brMotor.setZeroPowerBehavior(zeroPowerBehavior);
    }

    private double scaleInput(double x) {
        /*
        Squaring allows for finer adjustments when lower power.
        Disabled for now as it causes slower movement during diagonal (ask Javier).
        return x * x * (x > 0 ? 1 : -1);
         */
        return x;
    }

    public DcMotor getFrMotor() {
        return frMotor;
    }

    public DcMotor getBrMotor() {
        return brMotor;
    }

    public DcMotor getFlMotor() {
        return flMotor;
    }

    public DcMotor getBlMotor() {
        return blMotor;
    }

    public DcMotor.RunMode getRunMode() {
        return runMode;
    }

    public double getLatestDeltaFr() {
        return latestDeltaFr;
    }

    public double getLatestDeltaFl() {
        return latestDeltaFl;
    }

    public double getLatestDeltaBr() {
        return latestDeltaBr;
    }

    public double getLatestDeltaBl() {
        return latestDeltaBl;
    }

    public int getLastReadingFr() {
        return lastReadingFr;
    }

    public int getLastReadingFl() {
        return lastReadingFl;
    }

    public int getLastReadingBr() {
        return lastReadingBr;
    }

    public int getLastReadingBl() {
        return lastReadingBl;
    }
}
