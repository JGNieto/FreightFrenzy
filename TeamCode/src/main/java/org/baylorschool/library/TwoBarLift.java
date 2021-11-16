package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class TwoBarLift {
    // Millimeters and degrees
    private static final double barHeight = 397;
    private static final double barLength = 400;
    private static final double restingAngle = 17.5; // Angle with respect to the vertical of the lift when the robot is powered off.
    private static final double ticksPerRevolution = 537.7;

    // Height to clear the effector through TSH (Team Shipping Hub)
    private static final double clearanceHeight = 25;

    public static final double bottomLevelTSH = 76;
    public static final double middleLevelTSH = 216;
    public static final double topLevelTSH = 375;

    private static final double rollerPower = 1;
    private static final double liftPowerUp = .4;
    private static final double liftPowerDown = 0;
    private static final double liftPowerHold = .2;

    private int targetEncoderPosition = 0;
    private DcMotor twoBarMotor;
    private DcMotor rollerMotor;
    private LinearOpMode opMode;

    // These variables are only to be used by TeleOp systems.
    // Autonomous systems should use setTargetHeight and setTargetAngle.
    private LiftMovement movement = LiftMovement.HOLD;

    private RollerState rollerState = RollerState.STOP;

    // Keep track of whether we just stopped moving.
    private boolean wasMoving;

    public enum RollerState {
        GRABBING,
        RELEASING,
        STOP,
    }

    public enum LiftMovement {
        UP,
        DOWN,
        HOLD
    }

    public TwoBarLift(LinearOpMode opMode) {
        this.opMode = opMode;

        twoBarMotor = opMode.hardwareMap.get(DcMotor.class, "twobar");
        rollerMotor = opMode.hardwareMap.get(DcMotor.class, "roller");

        twoBarMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        twoBarMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        twoBarMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void loopIteration() {
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
                twoBarMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                twoBarMotor.setTargetPosition(targetEncoderPosition);
                twoBarMotor.setPower(liftPowerHold);
            }
        }

        if (rollerState == RollerState.RELEASING)
            rollerMotor.setPower(rollerPower);
        else if (rollerState == RollerState.GRABBING)
            rollerMotor.setPower(-rollerPower);
        else
            rollerMotor.setPower(0);
    }

    private int getTargetEncoderValue(double targetAngle) {
        return (int) (((targetAngle - restingAngle) / 360) * ticksPerRevolution);
    }

    public void setTargetAngle(double targetAngle) {
        this.targetEncoderPosition = getTargetEncoderValue(targetAngle);
    }

    public void setTargetHeight(double targetHeight) {
        setTargetAngle(Math.toDegrees(Math.acos((barHeight - targetHeight) / barLength)));
    }

    public void setMovement(LiftMovement movement) {
        this.movement = movement;
    }

    public void setRollerState(RollerState rollerState) {
        this.rollerState = rollerState;
    }
}
