package org.baylorschool.teamcode.teleop.drivers;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.baylorschool.Globals;
import org.baylorschool.library.ControlMap;
import org.baylorschool.library.lift.Lift;
import org.baylorschool.teamcode.teleop.TeleOpLogic;


@TeleOp(name="TestTeleOp", group="Test")
public class TestTeleOp extends TeleOpLogic {
    public TestTeleOp() {
        super(new TestTeleOpControls());
    }
}

class TestTeleOpControls extends ControlMap {
    public TestTeleOpControls() {
        super(true);
    }

    @Override
    public Globals.DropLevel liftDropLevel(DigitalChannel limitswitch) {
        return null;
    }

    @Override
    public double tapeTilt() {
        return gamepad1.right_trigger - gamepad1.left_trigger;
    }

    @Override
    public Globals.DropLevel liftDropLevel(Lift lift) {
        return null;
    }
}