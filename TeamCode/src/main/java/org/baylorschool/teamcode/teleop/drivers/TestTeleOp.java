package org.baylorschool.teamcode.teleop.drivers;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.library.ControlMap;
import org.baylorschool.teamcode.teleop.TeleOpLogic;


@TeleOp(name="TestTeleOp", group="Test")
public class TestTeleOp extends TeleOpLogic {
    public TestTeleOp() {
        super(new ControlMap());
    }
}
