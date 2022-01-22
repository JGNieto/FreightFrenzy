package org.baylorschool.library.lift;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.ControlMap;
import org.baylorschool.library.Location;
import org.baylorschool.library.math.CircleIntersect;

import java.util.List;

public abstract class Lift {
    // Use separate thread to fix cases when the main OpMode thread is in an infinite loop.
    // Ex: waiting for isBusy to be false
    protected boolean threadShouldStop = false;
    protected Thread thread;

    // These variables are only to be used by TeleOp systems.
    // Autonomous systems should use setTargetHeight and setTargetAngle.
    protected LiftMovement movement = LiftMovement.HOLD;
    protected RollerState rollerState = RollerState.STOP;

    protected LinearOpMode opMode;

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

    public Lift(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    /**
     * Instructs the lift to move to the drop level. Does not interrupt thread.
     * @param dropLevel in the Shipping Hub (use BOTTOM for shared Hub).
     */
    public abstract void moveToDropLevel(Globals.DropLevel dropLevel);

    /**
     * Releases the item (interrupts opMode thread).
     */
    public abstract void releaseItem();

    /**
     * Retracts the lift (warning: quite aggressive).
     */
    public abstract void retract();

    /**
     * Initializes the hardware for operation.
     */
    public abstract void initialize();

    /**
     * Initializes the hardware for operation interrupting OpMode thread.
     * @param opMode OpMode instance for sleep.
     */
    public abstract void initializeSync(LinearOpMode opMode);

    /**
     * To be executed every loop iteration.
     */
    public abstract void loopIteration();

    /**
     * Returns the program's best guess of how many elements have been captured.
     * Note: in Freight Frenzy, the result will either be 0 or 1. An integer is used for flexibility
     * moving forward into other seasons where it may be the case that an intake can have more than
     * one item. However, this season could have been implemented with a boolean.
     * @return number of items estimation.
     */
    public abstract int getCapturedElements();

    /**
     * Location for scoring taking into account desired TSH level and current location.
     * @param currentLocation of the robot
     * @param hub where the freight must be dropped.
     * @param dropLevel of the Team Shipping Hub
     * @return the Location where the robot should go to score.
     */
    public abstract Location getScoringLocation(Location currentLocation, Hub hub, Globals.DropLevel dropLevel);

    /**
     * Retracts the lift after a certain amount of time.
     * @param delay in milliseconds.
     */
    public void retract(long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException exception) {
            } finally {
                // Finally is used to make sure the angle is changed even if, for some unforeseen
                // reason, the thread is interrupted.
                retract();
            }
        }).start();
    }

    // Function to be used by children.
    protected Location getScoringLocation(Location currentLocation, Hub hub, Globals.DropLevel dropLevel, double dropDistanceTop, double dropDistanceMiddle, double dropDistanceBottom) {
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

    /**
     * Starts the thread.
     */
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

    /**
     * Indicates that the thread must stop.
     */
    public void closeThread() {
        try {
            thread.interrupt();
            threadShouldStop = true;
        } catch (NullPointerException e) { } // In case thread has already stopped.
    }

    /**
     * To be called every iteration of the loop in TeleOp.
     * Reads the state of the gamepad.
     */
    public void loopIterationTeleOp(Gamepad gamepad) {
        if (gamepad.dpad_up)
            movement = TwoBarLift.LiftMovement.UP;
        else if (gamepad.dpad_down)
            movement = TwoBarLift.LiftMovement.DOWN;
        else
            movement = TwoBarLift.LiftMovement.HOLD;

        if (gamepad.left_bumper)
            rollerState = TwoBarLift.RollerState.RELEASING;
        else if (gamepad.right_bumper)
            rollerState = TwoBarLift.RollerState.GRABBING;
        else
            rollerState = TwoBarLift.RollerState.STOP;

        loopIteration();
    }

    /**
     * To be called every iteration of the loop in TeleOp.
     * Reads the state of the Control Map.
     */
    public void loopIterationTeleOp(ControlMap controlMap) {
        if (controlMap.liftUp())
            movement = TwoBarLift.LiftMovement.UP;
        else if (controlMap.liftDown())
            movement = TwoBarLift.LiftMovement.DOWN;
        else
            movement = TwoBarLift.LiftMovement.HOLD;

        if (controlMap.liftReleasing())
            rollerState = TwoBarLift.RollerState.RELEASING;
        else if (controlMap.liftGrabbing())
            rollerState = TwoBarLift.RollerState.GRABBING;
        else
            rollerState = TwoBarLift.RollerState.STOP;

        loopIteration();
    }

    public void setMovement(LiftMovement movement) {
        this.movement = movement;
    }

    public void setRollerState(RollerState rollerState) {
        this.rollerState = rollerState;
    }
}
