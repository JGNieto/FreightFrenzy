package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Carousel {
    private DcMotor flyWheel;

    // Pneumatic wheel: 0.65
    // Green wheel: 0.8
    private final double slowSpeed = 0.65;
    private final double mediumSpeed = 0.8;
    private final double fastSpeed = 1;

    public enum CarouselSide {
        RED, BLUE
    }

    public Carousel(HardwareMap hardwareMap) {
        this.flyWheel = hardwareMap.get(DcMotor.class, "flyWheel");
    }

    public void move(CarouselSide side) {
        flyWheel.setPower(side == CarouselSide.BLUE ? fastSpeed : -fastSpeed);
    }

    public void dropDuck(CarouselSide side, LinearOpMode opMode) {
        flyWheel.setPower(side == CarouselSide.BLUE ? slowSpeed : -slowSpeed);
        opMode.sleep(600);
        flyWheel.setPower(side == CarouselSide.BLUE ? mediumSpeed : -mediumSpeed);
        opMode.sleep(200);
        flyWheel.setPower(side == CarouselSide.BLUE ? fastSpeed : -fastSpeed);
        opMode.sleep(400);
        this.stop();
    }

    public void stop() {
        flyWheel.setPower(0);
    }
}