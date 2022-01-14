package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.Gamepad;

public class ControlMap {
    protected Gamepad gamepad1;
    protected Gamepad gamepad2;

    private final boolean oneGamePad;

    private boolean slowMode = false;

    public ControlMap(Gamepad gamepad1, Gamepad gamepad2) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;

        this.oneGamePad = false;
    }

    public ControlMap() {
        this.oneGamePad = false;
    }

    public ControlMap(boolean oneGamePad) {
        this.oneGamePad = oneGamePad;
    }

    // Movement
    public double getX() {
        return gamepad1.left_stick_x;
    }

    public double getY() {
        return gamepad1.left_stick_y;
    }

    public double getRotation() {
        return gamepad1.right_stick_x;
    }

    public boolean fullRotationPower() {
        return gamepad1.right_stick_button;
    }

    public boolean isSlowMode() {
        updateSlowMode();
        return slowMode;
    }

    private void updateSlowMode() {
        if (gamepad1.a) {
            slowMode = true;
        }

        if (gamepad1.y) {
            slowMode = false;
        }
    }

    // Lift
    public boolean liftUp() {
        return gamepad2.dpad_up;
    }

    public boolean liftDown() {
        return gamepad2.dpad_down;
    }

    public boolean liftReleasing() {
        return gamepad2.left_bumper;
    }

    public boolean liftGrabbing() {
        return gamepad2.right_bumper;
    }

    // Carousel
    public boolean carouselRed() {
        return gamepad2.x;
    }

    public boolean carouselBlue() {
        return gamepad2.b;
    }

    // Tape Measurer
    public double tapeExtend() {
        if (gamepad2.y)
            return 1;
        else if (gamepad2.a)
            return -1;
        else
            return 0;
    }

    public double tapeTilt() {
        double power = gamepad2.left_stick_y;
        int sign = power < 0 ? -1 : 1;
        return power * power * sign;
    }

    // Setters
    public void setGamepad1(Gamepad gamepad1) {
        this.gamepad1 = gamepad1;
        if (oneGamePad)
            this.gamepad2 = gamepad1;
    }

    public void setGamepad2(Gamepad gamepad2) {
        if (!oneGamePad) // OneGamePad is a shortcut for making all controls be on a single gamepad.
            this.gamepad2 = gamepad2;
    }
}
