package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.math.CircleIntersect;

import java.util.List;

public class TwoBarLift extends Lift {
    private static final double ticksPerRevolution = 751.8;

    // If the difference between encoder position and expected position is greater than this,
    // use liftPowerUp or liftPowerDown, else use liftPowerHold.
    private static final int ticksDifferenceBeforeUsingMovementPower = (int) (ticksPerRevolution * 15 / 360);

    public static final int bottomLevelTSHEncoder = 169;
    public static final int middleLevelTSHEncoder = 260;
    public static final int topLevelTSHEncoder = 360;

    private static final double rollerGrabPower = -1;
    private static final double rollerReleasePower = 0.5;

    private static final double liftPowerUp = .4;
    private static final double liftPowerDown = -.2;
    private static final double liftPowerHold = .2;

    // Distance away from the CENTER of the Team Shipping Hub to drop on each level.
    private static final double dropDistanceTop = 561;
    private static final double dropDistanceMiddle = 684;
    private static final double dropDistanceBottom = 621;

    private static int releaseDelay = 2000;

    private int targetEncoderPosition = 0;
    private DcMotor twoBarMotor;
    private DcMotor rollerMotor;

    // Keep track of whether we just stopped moving.
    private boolean wasMoving;

    public TwoBarLift(LinearOpMode opMode) {
        super(opMode);

        twoBarMotor = opMode.hardwareMap.get(DcMotor.class, Globals.twoBarHw);
        rollerMotor = opMode.hardwareMap.get(DcMotor.class, Globals.rollerHw);
    }

    public void initialize() {
        twoBarMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        twoBarMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        twoBarMotor.setTargetPosition(targetEncoderPosition);
        twoBarMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void loopIteration() {
        opMode.telemetry.addData("LiftMove", movement.toString());
        opMode.telemetry.addData("LiftPos", twoBarMotor.getCurrentPosition());
        opMode.telemetry.addData("LiftTar", twoBarMotor.getTargetPosition());
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

        if (rollerState == RollerState.RELEASING)
            rollerMotor.setPower(rollerReleasePower);
        else if (rollerState == RollerState.GRABBING)
            rollerMotor.setPower(rollerGrabPower);
        else
            rollerMotor.setPower(0);
    }

    public void retract() {
        this.targetEncoderPosition = 0;
    }

    public Location getScoringLocation(Location currentLocation, Hub hub, Globals.DropLevel dropLevel) {
        return super.getScoringLocation(currentLocation, hub, dropLevel, dropDistanceTop, dropDistanceMiddle, dropDistanceBottom);
    }

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

    public void releaseItem() {
        this.rollerState = RollerState.RELEASING;
        opMode.sleep(releaseDelay);
        this.rollerState = RollerState.STOP;
    }
}
