package org.baylorschool.teamcode.teleop.drivers;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.library.ControlMap;
import org.baylorschool.teamcode.teleop.TeleOpLogic;


@TeleOp(name="MichaelJacksons", group="Drivers")
public class MichaelJacksons extends TeleOpLogic {
    public MichaelJacksons() {
        super(new MichaelJacksonsControls());
    }
}

class MichaelJacksonsControls extends ControlMap {
}