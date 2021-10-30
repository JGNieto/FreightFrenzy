package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Carousel {
    private DcMotor flyWheel;

    // Pneumatic wheel: 0.65
    // Green wheel: 0.8
    private final double speed = 0.8;

    public enum CarouselSide {
        RED, BLUE
    }

    public Carousel(HardwareMap hardwareMap) {
        this.flyWheel = hardwareMap.get(DcMotor.class, "flyWheel");
    }

    public void move(CarouselSide side) {
        flyWheel.setPower(side == CarouselSide.BLUE ? speed : -speed);
    }

    public void stop() {
        flyWheel.setPower(0);
    }
}
