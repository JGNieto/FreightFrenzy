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

    public static Location dropTheDuck(Carousel.CarouselSide side, Mecanum mecanum, LinearOpMode opMode, Carousel carousel) {
        mecanum.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        mecanum.moveMecanum(0, .23, 0);
        opMode.sleep(2000);
        mecanum.moveMecanum(-.18, 0, 0);
        opMode.sleep(1500);
        mecanum.moveMecanum(.045, 0, 0);
        opMode.sleep(150);
        mecanum.stop();
        carousel.dropDuck(side, opMode);

        mecanum.setTargetDistance(diagonalWheelDistanceCarousel, mecanum.getBlMotor());
        mecanum.setTargetDistance(diagonalWheelDistanceCarousel, mecanum.getFrMotor());
        mecanum.setTargetDistance(0, mecanum.getBrMotor());
        mecanum.setTargetDistance(0, mecanum.getBlMotor());
        mecanum.setPowerAutonomous();

        while (mecanum.isBusy()) {}

        return side == Carousel.CarouselSide.RED ? Places.redCarouselLocation : Places.blueCarouselLocation;
    }
}
