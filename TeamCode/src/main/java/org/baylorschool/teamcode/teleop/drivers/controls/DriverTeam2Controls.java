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
        else if (gamepad2.dpad_down)
            return Globals.DropLevel.BOTTOM;
        else
            return null;
    }

    public DriverTeam2Controls() {
        super();
    }
}
