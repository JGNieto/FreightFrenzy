package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;

public class DropDuck {

    static final Location redLocation = new Location(-1390.3,-1610.8,0,0,0,0);
    static final Location blueLocation = new Location(0,0,0,0,0,-90);

    public static Location dropTheDuck(Carousel.CarouselSide side, Mecanum mecanum, LinearOpMode opMode, Carousel carousel) {
        mecanum.moveMecanum(0, .5, 0);
        opMode.sleep(2000);
        mecanum.moveMecanum(.4, 0, 0);
        //while (sensorNotTouched()) {}
        opMode.sleep(1500);
        mecanum.stop();
        carousel.move(side);
        opMode.sleep(1500);
        carousel.stop();
        return side == Carousel.CarouselSide.RED ? redLocation : blueLocation;
    }
}
