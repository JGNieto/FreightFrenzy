package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Places;
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

    // Points to which to go before actually entering the warehouse.
    static Location redEntryPoint = new Location(Places.middle(0.5), Places.awayPerpendicular(3), 0);
    static Location blueEntryPoint = new Location(Places.middle(0.5), Places.awayPerpendicular(-3), 0);

    static Location redInsidePoint = new Location(redEntryPoint).setX(Places.middle(2));
    static Location blueInsidePoint = new Location(blueEntryPoint).setX(Places.middle(2));

    public static Location enterWarehouse(WarehouseSide side, Location currentLocation, Sensors sensors, LinearOpMode opMode) {
        sensors.getMecanum().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sensors.getMecanum().moveMecanum(0, (side == WarehouseSide.RED ? -1 : 1) * .5, 0);
        opMode.sleep(1500);
        double yValue = Places.awayPerpendicular(3 * (side == WarehouseSide.RED ? -1 : 1));
        currentLocation.setY(yValue);
        currentLocation.setHeading(0);
        sensors.getMecanum().resetEncoders();
        Location[] locations = new Location[] {
                new Location(Places.middle(2), yValue),
        };

        return MoveWaypoints.moveToWaypoints(currentLocation, sensors, Arrays.asList(locations), opMode);
    }

    /**
     * Enters the warehouse
     * @param side Which warehouse to enter
     * @param currentLocation Current location of the robot
     * @param mecanum For movement
     * @param odometry For localization
     * @param locations A list of locations (which can be empty) that the robot will follow with pure pursuit before going to the entry point.
     * @param opMode OpMode that the robot is running at this time.
     * @return Current Location.
     */
    public static Location enterWarehouseOdometry(WarehouseSide side, Location currentLocation, Mecanum mecanum, Odometry odometry, ArrayList<Location> locations, LinearOpMode opMode) {
        locations.add(side == WarehouseSide.BLUE ? blueEntryPoint : redEntryPoint);
        Path path = new Path(locations);
        currentLocation = MovePurePursuit.movePurePursuit(currentLocation, path, opMode, odometry, mecanum);
        path = new Path(Collections.singletonList(side == WarehouseSide.BLUE ? blueInsidePoint : redInsidePoint));
        currentLocation = MovePurePursuit.movePurePursuit(currentLocation, path, opMode, odometry, mecanum);
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
}
