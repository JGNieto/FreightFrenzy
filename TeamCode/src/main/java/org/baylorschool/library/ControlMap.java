package org.baylorschool.library;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.baylorschool.Globals;
import org.baylorschool.library.lift.Lift;

public abstract class ControlMap {
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

    public boolean liftRetract() {
        return false;
    }

    public abstract Globals.DropLevel liftDropLevel(DigitalChannel limitswitch);

    public Globals.DropLevel liftDropLevel() {
        return null;
    }

    public boolean liftHoldDown() {
        return gamepad2.left_trigger > .3;
    }

    // Carousel
    public boolean carouselRed() {
        return gamepad2.b;
    }

    public boolean carouselBlue() {
        return gamepad2.x;
    }

    // Tape Measurer
    public double tapeExtend() {
        if (gamepad2.y)
            return 1;
        else if (gamepad2.a)
            return -1;
        else
            return - gamepad1.left_trigger + gamepad1.right_trigger;
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

    public abstract Globals.DropLevel liftDropLevel(Lift lift);
}
