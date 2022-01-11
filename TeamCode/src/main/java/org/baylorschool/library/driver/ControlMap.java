package org.baylorschool.library.driver;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class ControlMap {
    private OpMode opMode;
    private boolean slowMode = false;

    public ControlMap(OpMode opMode) {
        this.opMode = opMode;
    }

    // Movement
    public double getX() {
        return opMode.gamepad1.left_stick_x;
    }

    public double getY() {
        return opMode.gamepad1.left_stick_y;
    }

    public double getRotation() {
        return opMode.gamepad1.right_stick_x;
    }

    public boolean isSlowMode() {
        updateSlowMode();
        return slowMode;
    }

    private void updateSlowMode() {
        if (opMode.gamepad1.a) {
            slowMode = true;
        }

        if (opMode.gamepad1.y) {
            slowMode = false;
        }
    }

    // Lift
    public boolean liftUp() {
        return opMode.gamepad2.dpad_up;
    }

    public boolean liftDown() {
        return opMode.gamepad2.dpad_down;
    }

    public boolean liftReleasing() {
        return opMode.gamepad2.dpad_down;
    }

    // Carousel
    public boolean carouselRed() {
        return opMode.gamepad2.x;
    }

    public boolean carouselBlue() {
        return opMode.gamepad2.b;
    }
}
