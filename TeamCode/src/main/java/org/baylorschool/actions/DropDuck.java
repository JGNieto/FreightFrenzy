package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.baylorschool.Places;
import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;

public class DropDuck {

    // Number of millimeters of movement on the diagonal wheels to free ourselves from carousel.
    static final int diagonalWheelDistanceCarousel = 100;

    /**
     * Drops the duck with correct timing.
     * @param side Side of the field where the robot is.
     * @param mecanum Mecanum instance.
     * @param opMode OpMode instance.
     * @param carousel Carousel instance
     * @param clearCarousel Whether the robot should move slightly to stop being in contact with the robot. (NOT TESTED)
     * @return Our best guess of the current location of the robot.
     */
    public static Location dropTheDuck(Carousel.CarouselSide side, Mecanum mecanum, LinearOpMode opMode, Carousel carousel, boolean clearCarousel) {
        // Set wheels to without encoder so that they don't modify their power levels if they detect more resistance.
        // This is so that they don't change behaviour when the robot touches the wall.
        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Move the robot toward the wall to align it.
        mecanum.moveNoScaling(0, .23, 0);
        opMode.sleep(2000);

        // Move the robot toward the carousel.
        mecanum.moveNoScaling(-.18, 0, 0);
        opMode.sleep(1500);

        // Separate very slightly from the robot.
        mecanum.moveNoScaling(.045, 0, 0);
        opMode.sleep(150);

        // Stop the wheels.
        mecanum.stop();

        // Drop the duck.
        carousel.dropDuck(side, opMode);

        // If we want clearCarousel, do so.
        if (clearCarousel) {
            // Set appropriate target distances.
            mecanum.setTargetDistance(diagonalWheelDistanceCarousel, mecanum.getBlMotor());
            mecanum.setTargetDistance(diagonalWheelDistanceCarousel, mecanum.getFrMotor());
            mecanum.setTargetDistance(0, mecanum.getBrMotor());
            mecanum.setTargetDistance(0, mecanum.getBlMotor());

            // Set power
            mecanum.setPowerAutonomous();

            // Wait until movement is done.
            while (opMode.opModeIsActive() && mecanum.isBusy()) { }

            // Return correct location based on side.
            return side == Carousel.CarouselSide.RED ? Places.redCarouselLocationAway : Places.blueCarouselLocationAway;
        }

        // Return correct location based on side.
        return side == Carousel.CarouselSide.RED ? Places.redCarouselLocation : Places.blueCarouselLocation;
    }
}
