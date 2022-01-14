package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.baylorschool.Globals;

public class Carousel {
    private final DcMotor flyWheel;
    private volatile CarouselSide asyncMovementRunning = null;

    public enum CarouselSide {
        RED, BLUE
    }

    public Carousel(HardwareMap hardwareMap) {
        this.flyWheel = hardwareMap.get(DcMotor.class, Globals.flyWheel);
    }

    public void move(CarouselSide side) {
        flyWheel.setPower(side == CarouselSide.BLUE ? 1 : -1);
    }

    /*
    public void dropDuck(CarouselSide side, LinearOpMode opMode) {
        flyWheel.setPower(side == CarouselSide.BLUE ? slowSpeed : -slowSpeed);
        opMode.sleep(600);
        flyWheel.setPower(side == CarouselSide.BLUE ? mediumSpeed : -mediumSpeed);
        opMode.sleep(200);
        flyWheel.setPower(side == CarouselSide.BLUE ? fastSpeed : -fastSpeed);
        opMode.sleep(400);
        this.stop();
    }
     */

    public void setDropPower(CarouselSide side) {
        flyWheel.setPower(side == CarouselSide.BLUE ? Globals.carouselSingleSpeed : -Globals.carouselSingleSpeed);
    }

    public void dropDuck(CarouselSide side, LinearOpMode opMode) {
        setDropPower(side);
        opMode.sleep(Globals.carouselSinglePause);
        this.stop();
    }

    public void dropDuckAsync(CarouselSide side) {
        if (side == asyncMovementRunning) return;
        asyncMovementRunning = side;
        new Thread(() -> {
            try {
                setDropPower(side);
                Thread.sleep(Globals.carouselSinglePause);
            } catch (Exception e) {
            } finally {
                asyncMovementRunning = null;
                this.stop();
            }
        }).start();
    }

    public void stop() {
        flyWheel.setPower(0);
    }
}