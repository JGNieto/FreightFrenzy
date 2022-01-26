package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.Globals;
import org.baylorschool.Places;
import org.baylorschool.library.ExecutionFrequency;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.lift.Lift;

public class GrabFreightBlindly {
    // When grabbing freight blindly, the robot first moves forward to try to grab. If it doesn't,
    // it moves backward
    enum MovementStage {
        FORWARD, BACKWARD, SIDEWAYS
    }

    ///////////////////////////// Constants for blind movement /////////////////////////////
    // Distance between the front of the robot and the wall at which it turns around.
    static final int turnAroundDistance = 170;
    static final int farAwayDistance = 270;
    static final int sidewaysMovement = 70;

    // Nanoseconds (milliseconds * 1000)
    static final long forwardTime = 400 * 1000;
    static final long backwardTime = 200 * 1000;
    static final long sidewaysTime = 100 * 1000;

    // Motor speeds
    static final double forwardSpeed = .3;
    static final double backwardSpeed = -.5;
    static final double sidewaysSpeed = .5;

    // Sideways limits
    static final double blueLeftLimit = Places.closeParallel(-3) + 40;
    static final double blueRightLimit = Places.awayParallel(-1) - 120;
    static final double redLeftLimit = Places.awayParallel(1) + 120;
    static final double redRightLimit = Places.closeParallel(3) - 40;

    public static Location grabFreightBlindly(Location currentLocation, Mecanum mecanum, Lift lift, Odometry odometry, LinearOpMode opMode, Globals.WarehouseSide warehouseSide) {
        // The implementation of this action is a kind of state machine. We keep track of the situation
        // we are in and, each iteration of the loop, we decide whether to change to a different
        // state. When we change state we also change the power configuration of the mecanum motors
        // to execute the desired movement.

        // Retract lift and make it grab.
        lift.retract();
        lift.setRollerState(Lift.RollerState.GRABBING);

        // If we get too close to one side of the warehouse, we will simply start to move in the opposite
        // direction. This variable keeps track of the direction we are moving it at the time.
        int sidewaysDirection = warehouseSide == Globals.WarehouseSide.RED ? -1 : 1; // 1 is right, -1 is left.

        // Although we use the odometry information to stop moving once we reach our target, we also
        // set a time limit on all movements. This variable keeps track of when we started the movement
        // to know whether it has been too long.
        long stageStartTime = System.nanoTime();

        // State machine variable. First step is moving forward.
        MovementStage movementStage = MovementStage.FORWARD;

        // Variable to store y (sideways) coordinate between one movement and the next.
        double previousY = currentLocation.getY();

        final double leftLimit = warehouseSide == Globals.WarehouseSide.RED ? redLeftLimit : blueLeftLimit;
        final double rightLimit = warehouseSide == Globals.WarehouseSide.RED ? redRightLimit : blueRightLimit;

        ExecutionFrequency executionFrequency = new ExecutionFrequency(opMode.telemetry);

        // When we are in the warehouse, we want to continue executing the grabbing motion for as long
        // as time allows. This function will only return when either time runs out, or we grab freight.
        // This function will not make strategic decisions based on time remaining. The reason is
        // there is not much we can do from inside the warehouse if the robot does not have freight.
        while (lift.getCapturedElements() < 1 && opMode.opModeIsActive()) {
            // Update position.
            currentLocation = odometry.calculateNewLocation(currentLocation);

            // Compute distance between front of the robot and wall
            double distanceToWall = Places.middle(3) - (currentLocation.getX() + Globals.robotLength / 2);
            long currentTime = System.nanoTime();

            switch (movementStage) {
                case FORWARD:
                    if (distanceToWall < turnAroundDistance || currentTime - stageStartTime > forwardTime) {
                        stageStartTime = currentTime;
                        movementStage = MovementStage.BACKWARD;
                        mecanum.moveNoScaling(0, backwardSpeed, 0);
                    }
                    break;
                case BACKWARD:
                    if (distanceToWall > farAwayDistance || currentTime - stageStartTime > backwardTime) {
                        stageStartTime = currentTime;
                        movementStage = MovementStage.SIDEWAYS;

                        // Check whether he have hit the side.
                        if (currentLocation.getX() < leftLimit)
                            sidewaysDirection = 1;
                        else if (currentLocation.getX() > rightLimit)
                            sidewaysDirection = -1;

                        mecanum.moveNoScaling(sidewaysSpeed * sidewaysDirection, 0, 0);
                    }
                    break;
                case SIDEWAYS:
                    if (Math.abs(currentLocation.getY() - previousY) > sidewaysMovement || currentTime - stageStartTime > sidewaysTime) {
                        stageStartTime = currentTime;
                        movementStage = MovementStage.FORWARD;
                        mecanum.moveNoScaling(0, forwardSpeed, 0);
                    }
                    break;
            }
            currentLocation.reportTelemetry(opMode.telemetry);
            executionFrequency.execution(currentTime);
            opMode.telemetry.update();
        }
        return currentLocation;
    }

}
