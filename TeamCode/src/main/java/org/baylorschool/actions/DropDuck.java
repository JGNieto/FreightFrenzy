package org.baylorschool.actions;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.baylorschool.library.Carousel;
import org.baylorschool.library.Location;
import org.baylorschool.library.Mecanum;

public class DropDuck {

    static final Location redLocation = new Location(-1390.3,-1610.8,0,0,0,0);
    static final Location blueLocation = new Location(0,0,0,0,0,-90);

    public static Location dropTheDuck(Carousel.CarouselSide side, Mecanum mecanum, LinearOpMode opMode, Carousel carousel) {
        mecanum.moveMecanum(0, 1, 0);
        opMode.sleep(1000);
        mecanum.moveMecanum(-1, 0, 0);
        //while (sensorNotTouched()) {}
        carousel.move(side);
        opMode.sleep(3000);
        return side == Carousel.CarouselSide.RED ? redLocation : blueLocation;
    }
}
