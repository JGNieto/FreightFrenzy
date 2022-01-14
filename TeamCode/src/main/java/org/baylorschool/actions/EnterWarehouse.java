package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Places;
import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Odometry;
import org.baylorschool.library.Path;
import org.baylorschool.library.Sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class EnterWarehouse {
    public enum WarehouseSide {
        RED, BLUE
    }

    // Locations to go to before actually entering the warehouse.
    static Location redEntryPoint = new Location(Places.middle(0.5), Places.awayPerpendicular(3), 0);
    static Location blueEntryPoint = new Location(Places.middle(0.5), Places.awayPerpendicular(-3), 0);

    // Locations to go to to enter the warehouse.
    static Location redInsidePoint = new Location(redEntryPoint).setX(Places.middle(2));
    static Location blueInsidePoint = new Location(blueEntryPoint).setX(Places.middle(2));

    /**
     * Moves the robot to the inside of the warehouse. Current location should be near the wall adjacent to the desired warehouse.
     * @param side Side of the warehouse we want to enter.
     * @param currentLocation Current location of the robot.
     * @param sensors Sensors instance.
     * @param opMode OpMode instance.
     * @return New location of the robot.
     */
    public static Location enterWarehouse(WarehouseSide side, Location currentLocation, Sensors sensors, LinearOpMode opMode) {
        // Set wheels to without encoder so that they don't modify their power levels if they detect more resistance.
        // This is so that they don't change behaviour when the robot touches the wall.
        sensors.getMecanum().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Move the robot toward the wall to align it.
        sensors.getMecanum().moveMecanum(0, (side == WarehouseSide.RED ? -1 : 1) * .5, 0);
        opMode.sleep(1500);

        // Y Value of the location when the robot is next to the wall.
        double yValue = Places.awayPerpendicular(3 * (side == WarehouseSide.RED ? -1 : 1));

        // The heading that the robot has after the alignment.
        double headingAlign = Math.abs(currentLocation.getHeading()) < 90 ? 0 : 180;

        // Update Y value and heading to its known values.
        currentLocation.setY(yValue);
        currentLocation.setHeading(headingAlign);

        // Reset the encoders so that all values are calculated freshly.
        sensors.getMecanum().resetEncoders();

        // Define path.
        Location[] locations = new Location[] {
                new Location(Places.middle(2), yValue, headingAlign),
        };

        // Execute movement and return the new currentLocation that moveToWaypoints also returns.
        return MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(locations), opMode);
    }

    /**
     * Enters the warehouse using Odometry and Pure Pursuit.
     * @param side Which warehouse to enter
     * @param currentLocation Current location of the robot
     * @param mecanum For movement
     * @param odometry For localization
     * @param locations A list of locations (which can be empty) that the robot will follow with Pure Pursuit before going to the entry point. E.g to avoid obstacles.
     * @param opMode OpMode that the robot is running at this time.
     * @return Current Location.
     */
    public static Location enterWarehouseOdometry(WarehouseSide side, Location currentLocation, Mecanum mecanum, Odometry odometry, ArrayList<Location> locations, LinearOpMode opMode) {
        // Add entry location to the end of the list of waypoints that Pure Pursuit will follow.
        locations.add(side == WarehouseSide.BLUE ? blueEntryPoint : redEntryPoint);

        // Create path instance from the locations list.
        Path path = new Path(locations);

        // Move using Pure Pursuit to the entry location.
        currentLocation = MovePurePursuit.movePurePursuit(currentLocation, path, opMode, odometry, mecanum);

        // Create path to move into the warehouse. Although Pure Pursuit is technically used, it will not affect much.
        path = new Path(Collections.singletonList(side == WarehouseSide.BLUE ? blueInsidePoint : redInsidePoint));

        // Enter the warehouse.
        currentLocation = MovePurePursuit.movePurePursuit(currentLocation, path, opMode, odometry, mecanum);

        // Return the new currentLocation.
        return currentLocation;
    }

    /**
     * Enters the warehouse
     * @param side Which warehouse to enter
     * @param currentLocation Current location of the robot
     * @param mecanum For movement
     * @param odometry For localization
     * @param opMode OpMode that the robot is running at this time.
     * @return Current Location.
     */
    public static Location enterWarehouseOdometry(WarehouseSide side, Location currentLocation, Mecanum mecanum, Odometry odometry, LinearOpMode opMode) {
        return enterWarehouseOdometry(side, currentLocation, mecanum, odometry, new ArrayList<>(), opMode);
    }

    // Constants for the parkWarehouse function.
    static final double timeToEnter = 2000; // The time during which the robot will be moving towards the warehouse.
    static final double enterPower = -0.6; // Power of the motors entering the warehouse.
    static final double timeToEnterAfterFlat = 200; // Time of motion after the robot's back has been lifted.
    static final double liftedThreshold = 3; // Degrees to consider lifted.

    /**
     * Enters the warehouse using full power over the bars.
     * Only designed for parking, as the location after this operation cannot be determined.
     * @param currentLocation Current location of the robot. Correct heading is not necessary.
     * @param sensors Sensors instance.
     * @param opMode OpMode that the robot is running at this time.
     */
    public static void parkWarehouse(Location currentLocation, Sensors sensors, LinearOpMode opMode) {
        // Pointers to use as shorthand.
        Mecanum mecanum = sensors.getMecanum();
        IMU imu = sensors.getImu();

        // Turn robot if not aligned.
        if (!currentLocation.rotationTolerance(0, 4)) {
            Location targetLocation = new Location(currentLocation).setHeading(180);
            MoveWaypoints.moveToWaypoints(currentLocation, sensors, Collections.singletonList(targetLocation), opMode);
        }

        sensors.getImu().updateOrientation();
        double baselinePitch = sensors.getImu().getPitch();
        double endTime = System.currentTimeMillis() + timeToEnter;

        boolean hasLiftedFront = false;
        int liftedFrontSign = 1;

        boolean hasLiftedBack = false;
        boolean hasFlattened = false;

        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        mecanum.setPower(-enterPower);

        while (System.currentTimeMillis() <= endTime && opMode.opModeIsActive()) {
            imu.updateOrientation();

            double pitchDelta = imu.getPitch() - baselinePitch;
            int sign = pitchDelta > 0 ? 1 : -1;

            if (Math.abs(pitchDelta) > liftedThreshold) {
                if (!hasLiftedFront) {
                    hasLiftedFront = true;
                    liftedFrontSign = sign;
                } else if (sign != liftedFrontSign && !hasLiftedBack) {
                    hasLiftedBack = true;
                }
            } else if (hasLiftedBack && !hasFlattened) {
                hasFlattened = true;
                endTime = System.currentTimeMillis() + timeToEnterAfterFlat;
            }

            opMode.telemetry.addData("Pitch", pitchDelta);
            opMode.telemetry.addData("Front", hasLiftedFront);
            opMode.telemetry.addData("Back", hasLiftedBack);
            opMode.telemetry.addData("Flat", hasFlattened);
            opMode.telemetry.update();
        }
        mecanum.stop();
    }
}
