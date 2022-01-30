package org.baylorschool.teamcode.teleop.drivers.controls;

import org.baylorschool.Globals;
import org.baylorschool.library.ControlMap;

public class DriverTeam2Controls extends ControlMap {
    @Override
    public boolean liftDown() {
        return false;
    }

    @Override
    public boolean liftUp() {
        return false;
    }

    @Override
    public Globals.DropLevel liftDropLevel() {
        if (gamepad2.dpad_up)
            return Globals.DropLevel.TOP;
        else
            return null;
    }

    @Override
    public boolean liftRetract() {
        return gamepad2.dpad_down;
    }

    public DriverTeam2Controls() {
        super();
    }
}