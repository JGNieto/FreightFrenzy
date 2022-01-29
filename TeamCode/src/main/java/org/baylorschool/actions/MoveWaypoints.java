package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Path;
import org.baylorschool.library.localization.Localization;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MoveWaypoints {

    // The movement has three stages. We first go full power to the target (FULL), then we switch
    // the motors off momentarily (SLIP) and then we make small adjustments (PRECISE).
    enum MovementState {
        FULL,
        SLIP,
        PRECISE
    }

    public static Location moveWaypoints(Path path, Mecanum mecanum, Localization localization, Location currentLocation, LinearOpMode opMode) {
        Telemetry telemetry = opMode.telemetry;
        double finalAngle = path.getLastLocation().getHeading();
        MovementState movementX = MovementState.FULL;
        MovementState movementY = MovementState.FULL;

        while (opMode.opModeIsActive()) {
            currentLocation = localization.calculateNewLocation(currentLocation);

            if (path.checkGoal(currentLocation)) {
                movementX = MovementState.FULL;
                movementY = MovementState.FULL;
            }

            Location currentGoal = path.currentGoal();

            if (currentGoal == null) {
                break;
            }

            MovePurePursuit.moveTowardPosition(mecanum, currentLocation, currentGoal, 0, currentGoal.getPurePursuitTurnSpeed());

            currentLocation.reportTelemetry(telemetry);

            telemetry.addData("Target...", "%.0f, %.0f, %.0f", currentGoal.getX(), currentGoal.getY(), currentGoal.getHeading());

            telemetry.addData("Target", mecanum.getBlMotor().getTargetPosition());
            telemetry.update();
        }
        mecanum.stop();
        mecanum.setBackwards(false);
        localization.setBackwards(false);
        return currentLocation;
    }

}
