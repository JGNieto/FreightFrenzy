package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.baylorschool.Globals;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.library.localization.Localization;

public class Carousel {
    private final DcMotor flyWheel;

    // Stored as volatile so that it can be used by different threads.
    private volatile CarouselSide asyncMovementRunning = null;

    public enum CarouselSide {
        RED, BLUE
    }

    public Carousel(HardwareMap hardwareMap) {
        this.flyWheel = hardwareMap.get(DcMotor.class, Globals.flyWheel);
        flyWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        stop();
    }

    public Location dropDuck(CarouselSide side, Location currentLocation, LinearOpMode opMode, Localization localization) {
        long startTime = System.currentTimeMillis();
        setDropPower(side);
        while (opMode.opModeIsActive()) {
            currentLocation = localization.calculateNewLocation(currentLocation);
            if (System.currentTimeMillis() - startTime >= Globals.carouselSinglePause) break;
        }
        stop();
        return currentLocation;
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