package org.baylorschool.movement;

import static com.qualcomm.robotcore.util.Range.scale;
import static java.lang.Math.abs;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Mecanum {

    private DcMotor frMotor;
    private DcMotor brMotor;
    private DcMotor flMotor;
    private DcMotor blMotor;

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
     * Changes motion of robot
     * @param y forward / backward power (1 to -1)
     * @param x left / right power (-1 to 1)
     * @param rotation left / right power (-1 to 1)
     */
    public void moveGamepad(double y, double x, double rotation) {
        /*
            Taken from
            https://ftcforum.firstinspires.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
         */
        double rot = -rotation;
        double Magnitude = abs(x) + abs(rot) + abs(y);
        Magnitude = (Magnitude > 1) ? Magnitude : 1;

        flMotor.setPower(scale((scaleInput(y) + scaleInput(rot) - scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1));
        blMotor.setPower(scale((scaleInput(y) + scaleInput(rot) + scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1));
        frMotor.setPower(scale((scaleInput(y) - scaleInput(rot) + scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1));
        brMotor.setPower(scale((scaleInput(y) - scaleInput(rot) - scaleInput(x)),
                -Magnitude, +Magnitude, -1, +1));
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
}
