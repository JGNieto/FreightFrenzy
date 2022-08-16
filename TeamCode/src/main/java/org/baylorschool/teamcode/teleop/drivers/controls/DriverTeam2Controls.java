package org.baylorschool.teamcode.teleop.drivers.controls;

import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.baylorschool.Globals;
import org.baylorschool.library.ControlMap;
import org.baylorschool.library.lift.Lift;

public class DriverTeam2Controls extends ControlMap {
    @Override
    public boolean liftDown() {
        return gamepad2.dpad_left;
    }

    @Override
    public boolean liftUp() {
        return gamepad2.dpad_right;
    }

    @Override
    public Globals.DropLevel liftDropLevel() {
        if (gamepad2.dpad_up)
            return Globals.DropLevel.TOP;
        else if (gamepad2.right_trigger > 0.3)
            return Globals.DropLevel.BOTTOM;
        else
            return null;
    }

    @Override
    public Globals.DropLevel liftDropLevel(Lift lift) {
        return null;
    }

    @Override
    public boolean liftRetract() {
        return gamepad2.dpad_down;
    }

    @Override
    public Globals.DropLevel liftDropLevel(DigitalChannel limitswitch) {
        return null;
    }

    public DriverTeam2Controls() {
        super();
    }
}
