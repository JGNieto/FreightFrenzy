package org.baylorschool.library.localization;

import org.baylorschool.library.IMU;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;
import org.baylorschool.library.Vuforia;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MotorEncoders implements Localization {
    private final Mecanum mecanum;
    private final IMU imu;
    private final Telemetry telemetry;
    private final Vuforia vuforia;

    public MotorEncoders(Mecanum mecanum, IMU imu, Telemetry telemetry, Vuforia vuforia) {
        this.mecanum = mecanum;
        this.imu = imu;
        this.telemetry = telemetry;
        this.vuforia = vuforia;
    }

    public MotorEncoders(Mecanum mecanum, IMU imu, Telemetry telemetry) {
        this.mecanum = mecanum;
        this.imu = imu;
        this.telemetry = telemetry;
        this.vuforia = null;
    }

    @Override
    public Location calculateNewLocation(Location currentLocation) {        // Update IMU first to make it be as close as possible in time to the data from the encoders.
        imu.updateOrientation();
        mecanum.updateEncoderReadings();

        // Use vuforia if available
        Location vuforiaLocation = vuforia == null ? null : vuforia.lookForTargets(telemetry);

        // Reset current location if vuforia has target in sight
        if (vuforiaLocation != null) {
            imu.forceValue(vuforiaLocation.getHeading());
            return vuforiaLocation;
        }

        // Get heading from IMU.
        currentLocation.setHeading(imu.getHeading());

        // Compute movement from wheel encoders.
        // Movement is a vector, which we break down into x and y components with math.
        double movementModulus = (
                mecanum.getLatestDeltaBl() +
                        mecanum.getLatestDeltaBr() +
                        mecanum.getLatestDeltaFl() +
                        mecanum.getLatestDeltaFr()
        ) / 4;

        double headingRadians = Math.toRadians(currentLocation.getHeading());

        double movementX = movementModulus * Math.cos(headingRadians);
        double movementY = movementModulus * Math.sin(headingRadians);

        currentLocation.setX(currentLocation.getX() + movementX);
        currentLocation.setY(currentLocation.getY() + movementY);

        return currentLocation;
    }

    @Override
    public void setBackwards(boolean backwards) {
        imu.setBackwards(backwards);
    }

    @Override
    public boolean isBackwards() {
        return false;
    }
}
