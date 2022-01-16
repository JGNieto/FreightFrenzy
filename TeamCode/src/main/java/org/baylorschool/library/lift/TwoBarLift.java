package org.baylorschool.library.lift;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.baylorschool.Globals;
import org.baylorschool.library.Location;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TwoBarLift extends Lift {
    private static final double ticksPerRevolution = 751.8;

    // If the difference between encoder position and expected position is greater than this,
    // use liftPowerUp or liftPowerDown, else use liftPowerHold.
    private static final int ticksDifferenceBeforeUsingMovementPower = (int) (ticksPerRevolution * 15 / 360);

    public static final int bottomLevelTSHEncoder = 234;
    public static final int middleLevelTSHEncoder = 333;
    public static final int topLevelTSHEncoder = 435;

    private static final double rollerGrabPower = -1;
    private static final double rollerReleasePower = 0.5;

    private static final double liftPowerUp = .4;
    private static final double liftPowerDown = -.2;
    private static final double liftPowerHold = .2;

    // Distance away from the CENTER of the Team Shipping Hub to drop on each level.
    private static final double dropDistanceTop = 541;
    private static final double dropDistanceMiddle = 654;
    private static final double dropDistanceBottom = 611;

    private static final int releaseDelay = 2000;
    private static final int rollerThrottle = 2000; // Minimum milliseconds between limit switch becoming free and grabbing again.

    private volatile int targetEncoderPosition = 0;
    private final DcMotor twoBarMotor;
    private final DcMotor rollerMotor;
    private final DigitalChannel limitSwitch;

    // Keep track of whether we just stopped moving.
    private volatile boolean wasMoving;
    private volatile long lastTimeLimitSwitch = 0;

    public TwoBarLift(LinearOpMode opMode) {
        super(opMode);

        twoBarMotor = opMode.hardwareMap.get(DcMotor.class, Globals.twoBarHw);
        rollerMotor = opMode.hardwareMap.get(DcMotor.class, Globals.rollerHw);
        limitSwitch = opMode.hardwareMap.get(DigitalChannel.class, Globals.rollerSwitch);
    }

    public void moveDown(LinearOpMode opMode) {
        twoBarMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        twoBarMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        twoBarMotor.setPower(liftPowerDown);
        opMode.sleep(500);
        twoBarMotor.setPower(0);
    }

    @Override
    public void initialize() {
        twoBarMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        twoBarMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        twoBarMotor.setTargetPosition(targetEncoderPosition);
        twoBarMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        rollerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void initializeSync(LinearOpMode opMode) {
        rollerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        twoBarMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        twoBarMotor.setPower(liftPowerDown);
        opMode.sleep(1000);
        twoBarMotor.setPower(0);

        twoBarMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        twoBarMotor.setTargetPosition(targetEncoderPosition);
        twoBarMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        twoBarMotor.setPower(liftPowerHold);

        rollerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        startThread();
    }


    @Override
    public void loopIteration() {
        opMode.telemetry.addData("LiftMove", movement.toString());
        opMode.telemetry.addData("LiftPos", twoBarMotor.getCurrentPosition());
        opMode.telemetry.addData("LiftTar", twoBarMotor.getTargetPosition());
        opMode.telemetry.addData("Lift Power", twoBarMotor.getPower());
        opMode.telemetry.addData("Limit Switch", limitSwitch.getState() ? "Empty" : "Full");
        if (movement == LiftMovement.UP) {
            wasMoving = true;
            twoBarMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            twoBarMotor.setPower(liftPowerUp);
        } else if (movement == LiftMovement.DOWN) {
            wasMoving = true;
            twoBarMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            twoBarMotor.setPower(liftPowerDown);
        } else {
            if (wasMoving) {
                wasMoving = false;
                twoBarMotor.setPower(liftPowerHold);
                targetEncoderPosition = twoBarMotor.getCurrentPosition();
            }
            if (!twoBarMotor.isBusy()) {
                twoBarMotor.setTargetPosition(targetEncoderPosition);
                twoBarMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                if (Math.abs(twoBarMotor.getCurrentPosition() - targetEncoderPosition) > ticksDifferenceBeforeUsingMovementPower) {
                    if (twoBarMotor.getCurrentPosition() > targetEncoderPosition) {
                        twoBarMotor.setPower(liftPowerDown);
                    } else {
                        twoBarMotor.setPower(liftPowerUp);
                    }
                } else {
                    twoBarMotor.setPower(liftPowerHold);
                }
            }
        }
        if (!limitSwitch.getState()) {
             lastTimeLimitSwitch = System.currentTimeMillis();
        }

        if (rollerState == RollerState.RELEASING)
            rollerMotor.setPower(rollerReleasePower);
        else if (rollerState == RollerState.GRABBING && System.currentTimeMillis() - lastTimeLimitSwitch > rollerThrottle)
            rollerMotor.setPower(rollerGrabPower);
        else
            rollerMotor.setPower(0);
    }

    @Override
    public void retract() {
        this.targetEncoderPosition = 0;
    }

    @Override
    public Location getScoringLocation(Location currentLocation, Hub hub, Globals.DropLevel dropLevel) {
        return super.getScoringLocation(currentLocation, hub, dropLevel, dropDistanceTop, dropDistanceMiddle, dropDistanceBottom);
    }

    @Override
    public void moveToDropLevel(Globals.DropLevel dropLevel) {
        if (dropLevel == Globals.DropLevel.TOP)
            targetEncoderPosition = topLevelTSHEncoder;
        else if (dropLevel == Globals.DropLevel.MIDDLE)
            targetEncoderPosition = middleLevelTSHEncoder;
        else if (dropLevel == Globals.DropLevel.BOTTOM)
            targetEncoderPosition = bottomLevelTSHEncoder;
        else
            retract();
    }

    @Override
    public void releaseItem() {
        this.rollerState = RollerState.RELEASING;
        opMode.sleep(releaseDelay);
        this.rollerState = RollerState.STOP;
    }
}
