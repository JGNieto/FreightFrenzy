package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.ExecutionFrequency;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.localization.ColorSensors;
import org.baylorschool.library.localization.Localization;

public class GrabFreightBlindlyCoopHub {
    // When grabbing freight blindly, the robot first moves forward to try to grab. If it doesn't,
    // it moves backward
    enum MovementStage {
        FORWARD, BACKWARD, SIDEWAYS
    }

    ///////////////////////////// Constants for blind movement /////////////////////////////
    // Distance between the front of the robot and the wall at which it turns around.
    static final int turnAroundDistance = (int) Places.middle(.65);
    static final int farAwayDistance = (int) Places.middle(1.5);
    static final int sidewaysMovement = 6;

    // Limit of how many times we will go through the forward-backward-sideways cycle before forcing
    // a direction change.
    static final int sidewaysMovementsLimit = 3;

    static final double pitchThreshold = 5.0;

    // Milliseconds
    static final long firstForwardTime = 1000; // Max time for forward motion the first time it happens.
    static final long forwardTime = 1100;
    static final long backwardTime = 900;
    static final long sidewaysTime = 200;

    // Motor speeds
    static final double forwardSpeed = .2;
    static final double backwardSpeed = -1;
    static final double sidewaysSpeed = .3;

    // Sideways limits
    static final double blueLeftLimit = Places.closeParallel(3) - 40;
    static final double blueRightLimit = Places.awayParallel(1) + 120;
    static final double redLeftLimit = Places.awayParallel(-1) - 120;
    static final double redRightLimit = Places.closeParallel(-3) + 40;

    /**
     * Moves the robot until it finds freight inside of the warehouse.
     * @param currentLocation Current location of the robot.
     * @param mecanum Mecanum instance.
     * @param lift Lift instance.
     * @param localization Localization instance.
     * @param opMode LinearOpMode instance.
     * @param warehouseSide Side of the warehouse we are in.
     * @param timeLimit Time limit in milliseconds, after which the robot will give up and return. Set to 0 for no time limit.
     * @return
     */
    public static Location grabFreightBlindly(Location currentLocation, Mecanum mecanum, Lift lift, Localization localization, LinearOpMode opMode, Globals.WarehouseSide warehouseSide, double timeLimit, ColorSensors colorSensors) {
        // The implementation of this action is a kind of state machine. We keep track of the situation
        // we are in and, each iteration of the loop, we decide whether to change to a different
        // state. When we change state we also change the power configuration of the mecanum motors
        // to execute the desired movement.

        // Retract lift and make it grab.
        lift.setHoldDown(true);
        lift.setRollerState(Lift.RollerState.GRABBING);

        // If we get too close to one side of the warehouse, we will simply start to move in the opposite
        // direction. This variable keeps track of the direction we are moving it at the time.
        int sidewaysDirection = warehouseSide == Globals.WarehouseSide.RED ? -1 : 1; // 1 is right, -1 is left.

        // Although we use the odometry information to stop moving once we reach our target, we also
        // set a time limit on all movements. This variable keeps track of when we started the movement
        // to know whether it has been too long.
        long stageStartTime = System.currentTimeMillis();

        // Time limit for finding something before we return.
        long startTime = stageStartTime;

        // State machine variable. First step is moving forward.
        MovementStage movementStage = MovementStage.FORWARD;

        // Variable to store y (sideways) coordinate between one movement and the next.
        double previousY = currentLocation.getY();

        boolean firstIteration = true;
        double pitchBaseline = currentLocation.getPitch();

        // Apart from limiting how far left or right we can go, we limit how many sideways movements
        // we can do before changing direction. This variable keeps track of how many we have done so far.
        int sidewaysMovements = 0;

        // Keep track of whether it is the first forward time.
        boolean firstTime = true;

        // final double leftLimit = warehouseSide == Globals.WarehouseSide.RED ? redLeftLimit : blueLeftLimit;
        // final double rightLimit = warehouseSide == Globals.WarehouseSide.RED ? redRightLimit : blueRightLimit;

        ExecutionFrequency executionFrequency = new ExecutionFrequency(opMode.telemetry);

        mecanum.moveNoScaling(forwardSpeed, 0, 0);

        // When we are in the warehouse, we want to continue executing the grabbing motion for as long
        // as time allows. This function will only return when either time runs out, or we grab freight.
        // This function will not make strategic decisions based on time remaining. The reason is
        // there is not much we can do from inside the warehouse if the robot does not have freight.
        while (lift.getCapturedElements() < 1 && opMode.opModeIsActive()) {
            opMode.telemetry.clearAll();

            // Update position.
            currentLocation = localization.calculateNewLocation(currentLocation);

            if (firstIteration) {
                firstIteration = false;
                pitchBaseline = currentLocation.getPitch();
            }

            // Compute distance between front of the robot and wall
            double distanceToWall = Places.middle(3) - (currentLocation.getX() + Globals.robotLength / 2);
            long currentTime = System.currentTimeMillis();

            // Stop if time has run out.
            if (timeLimit != 0 && currentTime - startTime > timeLimit)
                break;

            opMode.telemetry.addData("Distance", distanceToWall);
            opMode.telemetry.addData("Time", currentTime - stageStartTime);
            opMode.telemetry.addData("Side moves", sidewaysMovements);
            opMode.telemetry.addData("Direction", sidewaysDirection);

            switch (movementStage) {
                case FORWARD:
                    if (distanceToWall < turnAroundDistance || currentTime - stageStartTime > (firstTime ? firstForwardTime : forwardTime) || Math.abs(currentLocation.getPitch() - pitchBaseline) > pitchThreshold) {
                        firstTime = false;
                        stageStartTime = currentTime;
                        movementStage = MovementStage.BACKWARD;
                        mecanum.moveNoScaling(backwardSpeed,0, 0);
                        lift.setRollerState(Lift.RollerState.GRABBING);
                        colorSensors.resetBaseLine();
                        colorSensors.setLooking(true);
                    }
                    break;
                case BACKWARD:
                    if (distanceToWall > farAwayDistance || currentTime - stageStartTime > backwardTime || colorSensors.isTrigger()) {
                        lift.setRollerState(Lift.RollerState.GRABBING);
                        lift.setHoldDown(true);
                        stageStartTime = currentTime;
                        movementStage = MovementStage.SIDEWAYS;
                        previousY = currentLocation.getY();

                        colorSensors.setLooking(false);

                        // If, for whatever reason, the robot has rotated too much, rotate back.
                        if (Math.abs(currentLocation.getHeading()) > 100|| currentLocation.getHeading() < 80)
                            currentLocation = MoveWaypoints.rotatePID(currentLocation, localization, mecanum, 90, opMode);

                        // Check whether he have hit the side.
                        // Max of sideways movements should do the trick
                        /*if (currentLocation.getY() < leftLimit) {
                            sidewaysDirection = 1;
                            sidewaysMovements = 0;
                        } else if (currentLocation.getY() > rightLimit) {
                            sidewaysDirection = -1;
                            sidewaysMovements = 0;
                        }*/

                        // Check whether we have made too many sideways movements.
                        if (sidewaysMovements >= sidewaysMovementsLimit) {
                            sidewaysDirection *= -1;
                            sidewaysMovements = 0;
                            firstTime = true;
                        }

                        mecanum.moveNoScaling(0, - sidewaysSpeed * sidewaysDirection, 0);
                    }
                    break;
                case SIDEWAYS:
                    if (Math.abs(currentLocation.getY() - previousY) > sidewaysMovement || currentTime - stageStartTime > sidewaysTime) {
                        stageStartTime = currentTime;
                        movementStage = MovementStage.FORWARD;
                        ++sidewaysMovements;
                        mecanum.moveNoScaling(forwardSpeed, 0, 0);
                    }
                    break;
            }
            opMode.telemetry.addData("Movement", movementStage.toString());
            currentLocation.reportTelemetry(opMode.telemetry);
            executionFrequency.execution();
            opMode.telemetry.update();
        }
        lift.setHoldDown(false);
        lift.retract();
        return currentLocation;
    }
}
