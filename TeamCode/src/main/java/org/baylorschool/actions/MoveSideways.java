package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.localization.Localization;
import org.baylorschool.library.localization.TouchSensors;

public class MoveSideways {

    static final double DEFAULT_SPEED = 0.5;

    /**
     * Moves sideways until a wall is detected or it times out.
     * @param direction Which direction to move in.
     * @param timeLimitMS Time limit in ms. Set to 0 if no time limit is desired.
     * @param touchSensors TouchSensors instance.
     * @param speed Speed to set the motors. No need to set a negative value. Set to 0 for default.
     * @param currentLocation Current location of the robot.
     * @param mecanum Mecanum instance.
     * @param localization Localization instance. May be null.
     * @param opMode LinearOpMode instance.
     * @return
     */
    public static Location moveSidewaysUntilTouch(TouchSensors.Direction direction, long timeLimitMS, TouchSensors touchSensors, double speed, Location currentLocation, Mecanum mecanum, Localization localization, LinearOpMode opMode) {
        long startTime = System.currentTimeMillis();
        if (!touchSensors.canDetect(direction)) {
            opMode.telemetry.log().add("Called moveSidewaysUntilTouch() toward " + direction.name() + " direction without required hardware.");
        }

        opMode.telemetry.log().add("Moving toward " + direction.name());

        if (speed == 0)
            speed = DEFAULT_SPEED;
        else speed = Math.abs(speed);

        switch (direction) {
            case RIGHT:
                mecanum.moveNoScaling(0, -speed, 0);
                break;
            case LEFT:
                mecanum.moveNoScaling(0, speed, 0);
                break;
            case BACK:
                mecanum.moveNoScaling(-speed, 0, 0);
                break;
        }

        while ((timeLimitMS != 0 || System.currentTimeMillis() - startTime < timeLimitMS) && !touchSensors.pressed(direction)) {
            if (localization != null)
                currentLocation = localization.calculateNewLocation(currentLocation);

            opMode.telemetry.addLine("Moving sideways.");
            currentLocation.reportTelemetry(opMode.telemetry);
            opMode.telemetry.update();
        }

        mecanum.stop();

        return currentLocation;
    }

}
