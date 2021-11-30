package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.math.CircleIntersect;

import java.util.List;

public class TwoBarLift {
    // Millimeters and degrees
    private static final double barHeight = 397;
    private static final double barLength = 400;
    private static final double restingAngle = 17.5; // Angle with respect to the vertical of the lift when the robot is powered off.
    private static final double ticksPerRevolution = 751.8;

    private static final double gearRatio = 70.0 / 44.0;

    // If the difference between encoder position and expected position is greater than this,
    // use liftPowerUp or liftPowerDown, else use liftPowerHold.
    private static final int ticksDifferenceBeforeUsingMovementPower = (int) (ticksPerRevolution * 15 / 360);

    // Height to clear the effector through TSH (Team Shipping Hub)
    private static final double clearanceHeight = 25;

    public static final double bottomLevelTSH = 76 + clearanceHeight;
    public static final double middleLevelTSH = 216 + clearanceHeight;
    public static final double topLevelTSH = 375 + clearanceHeight;

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

    // Use separate thread to fix cases when the main OpMode thread is in an infinite loop.
    // Ex: waiting for isBusy to be false
    private boolean threadShouldStop = false;
    private Thread thread;

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

    public enum Hub {
        RED,
        BLUE,
    }

    public TwoBarLift(LinearOpMode opMode) {
        this.opMode = opMode;

        twoBarMotor = opMode.hardwareMap.get(DcMotor.class, "twobar");
        rollerMotor = opMode.hardwareMap.get(DcMotor.class, "roller");
    }

    public void startThread() {
        initialize();

        thread = new Thread(() -> {
            try {
                while (!threadShouldStop) {
                    loopIteration();
                    Thread.sleep(1);
                }
            } catch (InterruptedException exception) {
                return;
            }
        });
        thread.start();
    }

    public void initialize() {
        twoBarMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        twoBarMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        twoBarMotor.setTargetPosition(targetEncoderPosition);
        twoBarMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void closeThread() {
        threadShouldStop = true;
        thread.interrupt();
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

    private int getTargetEncoderValue(double targetAngle) {
        return (int) ((((targetAngle - restingAngle) * gearRatio) / 360) * ticksPerRevolution);
    }

    public void setTargetAngle(double targetAngle) {
        this.targetEncoderPosition = getTargetEncoderValue(targetAngle);
    }

    public void setTargetHeight(double targetHeight) {
        setTargetAngle(Math.toDegrees(Math.acos((barHeight - targetHeight) / barLength)));
    }

    public void retract() {
        setTargetAngle(0);
    }

    public void loopIterationTeleOp() {
        if (opMode.gamepad1.dpad_up)
            setMovement(TwoBarLift.LiftMovement.UP);
        else if (opMode.gamepad1.dpad_down)
            setMovement(TwoBarLift.LiftMovement.DOWN);
        else
            setMovement(TwoBarLift.LiftMovement.HOLD);

        if (opMode.gamepad1.left_bumper)
            setRollerState(TwoBarLift.RollerState.RELEASING);
        else if (opMode.gamepad1.right_bumper)
            setRollerState(TwoBarLift.RollerState.GRABBING);
        else
            setRollerState(TwoBarLift.RollerState.STOP);

        loopIteration();
    }

    /**
     * Location for scoring taking into account desired TSH level and current location.
     * @param currentLocation of the robot
     * @param hub where the freight must be dropped.
     * @param dropLevel of the Team Shipping Hub
     * @return the Location where the robot should go to score.
     */
    public static Location getScoringLocation(Location currentLocation, Hub hub, Globals.DropLevel dropLevel) {
        double radius;
        Location targetHubLocation;
        switch (hub) {
            case RED:
                targetHubLocation = Places.redTeamShippingHub;
                break;
            case BLUE:
                targetHubLocation = Places.blueTeamShippingHub;
                break;
            default:
                throw new IllegalArgumentException("Hub argument for getScoringLocation() invalid.");
        }

        switch (dropLevel) {
            case TOP:
                radius = dropDistanceTop;
                break;
            case MIDDLE:
                radius = dropDistanceMiddle;
                break;
            case BOTTOM:
                radius = dropDistanceBottom;
                break;
            default:
                throw new IllegalArgumentException("Drop Level argument for getScoringLocation() invalid.");
        }
        // Point B equals the circle center because we want the point in the circle that intersects
        // the line between the robot and the center.
        List<Location> potentialLocations = CircleIntersect.getCircleLineIntersectionLocation(currentLocation, targetHubLocation, targetHubLocation, radius);
        Location closestLocation = null;
        double distanceToLocation = -1;

        for (Location location : potentialLocations) {
            double distance = Location.distance(currentLocation, location);
            if (closestLocation == null || distance < distanceToLocation) {
                closestLocation = location;
                distanceToLocation = distance;
            }
        }

        return closestLocation;
    }

    public void moveToDropLevel(Globals.DropLevel dropLevel) {
        if (dropLevel == Globals.DropLevel.TOP)
            //setTargetHeight(topLevelTSH);
            targetEncoderPosition = topLevelTSHEncoder;
        else if (dropLevel == Globals.DropLevel.MIDDLE)
            //setTargetHeight(middleLevelTSH);
            targetEncoderPosition = middleLevelTSHEncoder;
        else if (dropLevel == Globals.DropLevel.BOTTOM)
            //setTargetHeight(bottomLevelTSH);
            targetEncoderPosition = bottomLevelTSHEncoder;
        else
            retract();
    }

    public void releaseItem() {
        setRollerState(RollerState.RELEASING);
        opMode.sleep(2000);
        setRollerState(RollerState.STOP);
    }

    public void setMovement(LiftMovement movement) {
        this.movement = movement;
    }

    public void setRollerState(RollerState rollerState) {
        this.rollerState = rollerState;
    }
}
