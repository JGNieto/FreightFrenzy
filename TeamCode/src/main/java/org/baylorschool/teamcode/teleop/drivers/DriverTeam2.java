package org.baylorschool.teamcode.teleop.drivers;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.teamcode.teleop.TeleOpLogic;
import org.baylorschool.teamcode.teleop.drivers.controls.DriverTeam2Controls;


@TeleOp(name="DriverTeam2", group="Drivers")
public class DriverTeam2 extends TeleOpLogic {
    public DriverTeam2() {
        super(new DriverTeam2Controls());
    }
}

