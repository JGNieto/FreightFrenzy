package org.baylorschool.teamcode.teleop.drivers;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.library.ControlMap;
import org.baylorschool.teamcode.teleop.TeleOpLogic;


@TeleOp(name="DriverTeam2", group="Drivers")
public class DriverTeam2 extends TeleOpLogic {
    public DriverTeam2() {
        super(new DriverTeam2Controls());
    }
}

class DriverTeam2Controls extends ControlMap {

}