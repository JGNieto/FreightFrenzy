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

    private DcMotor.RunMode runMode;

    private static final double ticksPerRevolution = 537.7;
    private static final double wheelDiameter = 10; // In millimeters
    public static final double ticksPerMm = ticksPerRevolution / (Math.PI * wheelDiameter);
    private final double autonomousSpeed = 0.6;

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
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    /**
     * Class for managing four mecanum wheels.
     * Constructor with default, global values.
     * @param hardwareMap for retrieving motors.
     */
    public Mecanum(HardwareMap hardwareMap) {
        this.blMotor = hardwareMap.get(DcMotor.class, "blMotor");
        this.flMotor = hardwareMap.get(DcMotor.class, "flMotor");
        this.brMotor = hardwareMap.get(DcMotor.class, "brMotor");
        this.frMotor = hardwareMap.get(DcMotor.class, "frMotor");

        setReverse(Side.RIGHT);
        setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Changes the side that is reverse.
     * @param reverse Side that requires reversing.
     */
    public void setReverse(Side reverse) {
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
     * Position to which wheels with encoders will move
     * @param targetPosition in ticks
     * @param dcMotor
     */
    public void setTargetPosition(int targetPosition, DcMotor dcMotor) {
        dcMotor.setTargetPosition(targetPosition + dcMotor.getTargetPosition());
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
        double arch = Math.PI * turningDiameter * (angle / 360);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setTargetDistance(arch, frMotor);
        setTargetDistance(arch, brMotor);
        setTargetDistance(-arch, flMotor);
        setTargetDistance(-arch, blMotor);

        frMotor.setPower(autonomousSpeed);
        brMotor.setPower(autonomousSpeed);
        flMotor.setPower(-autonomousSpeed);
        blMotor.setPower(-autonomousSpeed);
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
        brMotor.setPower(power);
        flMotor.setPower(power);
        blMotor.setPower(power);
    }

    /**
     * Retrieves encoder values from the motor, calculates deltas and stores them.
     */
    public void updateEncoderReadings() {
        int frReading = frMotor.getCurrentPosition() * 1;
        int flReading = flMotor.getCurrentPosition() * 1;
        int brReading = brMotor.getCurrentPosition() * 1;
        int blReading = blMotor.getCurrentPosition() * 1;

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
}
