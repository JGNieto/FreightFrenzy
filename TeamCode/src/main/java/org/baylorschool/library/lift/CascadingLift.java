package org.baylorschool.library.lift;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.baylorschool.Globals;
import org.baylorschool.library.ControlMap;
import org.baylorschool.library.Location;

public class CascadingLift extends Lift {

    private static final int maxEncoderValue = 1000; // TODO
    private static final int minEncoderValueServoOpen = 50; // TODO

    private static final double liftPower = 1;
    private static final double downPower = -0.2;

    private static final double servoOpen = 1;
    private static final double servoClosed = 0;

    private static final double grabbingPower = 0.6;
    private static final double grabAdjustPower = 0.2;
    private static final int grabExtraTurns = 1;
    private static final int grabbingEncoderMultiplier = 288 / 3;
    private static final int grabbingEncoderTolerance = 20;

    private static final int releaseDelay = 2000;
    private static final int retractWaitTime = 200;

    private final DcMotor liftMotor;
    private final DcMotor grabMotor;
    private final Servo releaseServo;

    private int zeroValueLift = 0;
    private int zeroValueGrabber = 0;

    private boolean grabbing = false;
    private boolean releasing = false;

    public CascadingLift(LinearOpMode opMode) {
        super(opMode);

        this.liftMotor = opMode.hardwareMap.get(DcMotor.class, Globals.cascadingLiftHw);
        this.grabMotor = opMode.hardwareMap.get(DcMotor.class, Globals.cascadingMotorHw);
        this.releaseServo = opMode.hardwareMap.get(Servo.class, Globals.cascadingServoHw);
    }

    @Override
    public void moveToDropLevel(Globals.DropLevel dropLevel) {
        // TODO: Implement this method.
    }

    @Override
    public void releaseItem() {
        this.rollerState = RollerState.RELEASING;
        opMode.sleep(releaseDelay);
        this.rollerState = RollerState.STOP;
    }

    @Override
    public void retract() {
        new Thread(() -> {
            try {
                liftMotor.setPower(downPower);
                Thread.sleep(retractWaitTime);
            } catch (InterruptedException e) {
            } finally {
                liftMotor.setPower(0);
            }
        }).start();
    }

    @Override
    public void initialize() {
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        grabMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        grabMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        grabMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        zeroValueLift = liftMotor.getCurrentPosition();
        zeroValueGrabber = grabMotor.getCurrentPosition();

        releaseServo.setPosition(servoClosed);
    }

    @Override
    public void initializeSync(LinearOpMode opMode) {
        initialize(); // There is no difference between intialize and intializeSync in cascade lift.
    }

    @Override
    public void loopIteration() {
        if (telemetryEnabled) {
            opMode.telemetry.addData("LiftMove", movement.toString());
            opMode.telemetry.addData("LiftPos", liftMotor.getCurrentPosition());
            opMode.telemetry.addData("LiftTar", liftMotor.getTargetPosition());
            opMode.telemetry.addData("Lift Power", liftMotor.getPower());
        }
        int encoderValue = liftMotor.getCurrentPosition();
        if (movement == LiftMovement.UP) {
            if (encoderValue - zeroValueLift < maxEncoderValue)
                liftMotor.setPower(liftPower);
            else
                liftMotor.setPower(0);
        } else if (movement == LiftMovement.DOWN) {
            if (encoderValue > zeroValueLift)
                liftMotor.setPower(downPower);
            else
                liftMotor.setPower(0);
        } else {
            liftMotor.setPower(0);
        }

        if (encoderValue < minEncoderValueServoOpen)
            releasing = false;

        if (releasing)
            releaseServo.setPosition(servoOpen);
        else
            releaseServo.setPosition(servoClosed);

        if (grabbing) {
            grabMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            grabMotor.setPower(grabbingPower);
        } else {
            int grabEncoderValue = grabMotor.getCurrentPosition() - zeroValueGrabber;
            int mod = grabEncoderValue % grabbingEncoderMultiplier;
            if (!grabMotor.isBusy() && mod < grabbingEncoderTolerance) {
                grabMotor.setPower(0);

                grabEncoderValue += grabbingEncoderMultiplier * grabExtraTurns; // Add some extra turns.

                grabMotor.setTargetPosition(grabEncoderValue + mod + zeroValueGrabber);
                grabMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                grabMotor.setPower(grabAdjustPower);
            }
        }
    }

    @Override
    public int getCapturedElements() { // No hardware to detect, so default to 0.
        return 0;
    }

    @Override
    public void moveDown(LinearOpMode opMode) {
        liftMotor.setPower(downPower);
        opMode.sleep(retractWaitTime);
        liftMotor.setPower(0);
    }

    @Override
    public Location getScoringLocation(Location currentLocation, Hub hub, Globals.DropLevel dropLevel) {
        throw new UnsupportedOperationException("Cascading lift scoring location not implemented.");
    }

    @Override
    public void setRollerState(RollerState rollerState) {
        // We do it this way for backward compatibility with code designed for TwoBarLift.
        super.setRollerState(rollerState);
        grabbing = rollerState == RollerState.GRABBING;
        releasing = rollerState == RollerState.RELEASING;
    }

    @Override
    public void loopIterationTeleOp(ControlMap controlMap) {
        if (controlMap.liftUp())
            movement = TwoBarLift.LiftMovement.UP;
        else if (controlMap.liftDown())
            movement = TwoBarLift.LiftMovement.DOWN;
        else
            movement = TwoBarLift.LiftMovement.HOLD;

        releasing = controlMap.liftReleasing();
        grabbing = controlMap.liftGrabbing();

        if (releasing)
            rollerState = RollerState.RELEASING;
        else if (grabbing)
            rollerState = RollerState.GRABBING;
        else
            rollerState = RollerState.STOP;

        loopIteration();
    }
}
