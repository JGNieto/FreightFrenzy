package org.baylorschool.teamcode.teleop.test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.baylorschool.library.lift.TwoBarLift;

@TeleOp(name="TwoBarLiftEncoders", group="Test")
@Disabled
public class TwoBarLiftEncoders extends LinearOpMode {

    private TwoBarLift lift;

    @Override
    public void runOpMode() {

        lift = new TwoBarLift(this);

        waitForStart();

        lift.startThread();

        while (opModeIsActive()) {
            if (gamepad1.dpad_up)
                lift.setMovement(TwoBarLift.LiftMovement.UP);
            else if (gamepad1.dpad_down)
                lift.setMovement(TwoBarLift.LiftMovement.DOWN);
            else
                lift.setMovement(TwoBarLift.LiftMovement.HOLD);

            if (gamepad1.dpad_left)
                lift.setRollerState(TwoBarLift.RollerState.RELEASING);
            else if (gamepad1.dpad_right)
                lift.setRollerState(TwoBarLift.RollerState.GRABBING);
            else
                lift.setRollerState(TwoBarLift.RollerState.STOP);

        }

        lift.closeThread();
    }
}
