package org.baylorschool.library;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

public class ControlMap {
    private Gamepad gamepad1;
    private Gamepad gamepad2;

    private boolean slowMode = false;

    public ControlMap(Gamepad gamepad1, Gamepad gamepad2) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
    }

    public ControlMap() {

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
    public double tapePower() {
        if (gamepad2.y)
            return 1;
        else if (gamepad2.a)
            return -1;
        else
            return 0;
    }

    public double tapeTilt() {
        return gamepad2.left_stick_y;
    }

    // Setters
    public void setGamepad1(Gamepad gamepad1) {
        this.gamepad1 = gamepad1;
    }

    public void setGamepad2(Gamepad gamepad2) {
        this.gamepad2 = gamepad2;
    }
}
