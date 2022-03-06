package org.baylorschool.teamcode.teleop.drivers.controls;

import org.baylorschool.library.ControlMap;

public class MichaelJacksonsControls extends ControlMap {
    public MichaelJacksonsControls() {
        super();
    }

    @Override
    public double tapeTilt() {
        if (gamepad1.dpad_up) {
            return 0.2;
        } else if (gamepad1.dpad_down) {
            return -0.2;
        } else if (gamepad1.dpad_left) {
            return -0.4;
        } else if (gamepad1.dpad_right) {
            return 0.4;
        } else {
            return gamepad2.left_stick_y;
        }
    }

    @Override
    public double tapeExtend() {
        if (gamepad1.left_trigger > .01 || gamepad1.right_trigger > .01) {
            return - gamepad1.left_trigger + gamepad1.right_trigger;
        } else {
            if (gamepad2.y)
                return 1;
            else if (gamepad2.a)
                return -1;
        }
        return 0;
    }
}
